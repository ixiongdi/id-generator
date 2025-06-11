package icu.congee.id.generator.distributed.segmentid.concurrent;import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentRingBuffer<T> {
    private final T[] buffer;
    private int head; // 读取位置
    private int tail; // 写入位置
    private int count; // 元素数量
    private final int capacity; // 缓冲区容量
    
    private final ReentrantLock lock;
    private final Condition notEmpty;
    private final Condition notFull;
    private final boolean overwrite;

    /**
     * 创建环形缓冲区
     * @param capacity 缓冲区容量
     * @param overwrite 当缓冲区满时，是否覆盖最旧数据
     * @param fair 是否使用公平锁
     */
    @SuppressWarnings("unchecked")
    public ConcurrentRingBuffer(int capacity, boolean overwrite, boolean fair) {
        if (capacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
        this.lock = new ReentrantLock(fair);
        this.notEmpty = lock.newCondition();
        this.notFull = lock.newCondition();
        this.overwrite = overwrite;
    }

    /**
     * 创建环形缓冲区（默认非公平锁）
     * @param capacity 缓冲区容量
     * @param overwrite 当缓冲区满时，是否覆盖最旧数据
     */
    public ConcurrentRingBuffer(int capacity, boolean overwrite) {
        this(capacity, overwrite, false);
    }

    // 阻塞操作：在空间可用前等待
    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            while (count == capacity && !overwrite) {
                notFull.await();
            }
            
            if (count == capacity && overwrite) {
                // 覆盖最旧数据
                buffer[tail] = item;
                head = (head + 1) % capacity;
                tail = (tail + 1) % capacity;
                notEmpty.signal(); // 通知等待的消费者
            } else {
                // 正常添加数据
                buffer[tail] = item;
                tail = (tail + 1) % capacity;
                count++;
                notEmpty.signal(); // 通知等待的消费者
            }
        } finally {
            lock.unlock();
        }
    }

    // 超时阻塞操作
    public boolean put(T item, long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        lock.lockInterruptibly();
        try {
            while (count == capacity && !overwrite) {
                if (nanos <= 0) {
                    return false; // 超时返回
                }
                nanos = notFull.awaitNanos(nanos);
            }
            
            if (count == capacity && overwrite) {
                buffer[tail] = item;
                head = (head + 1) % capacity;
                tail = (tail + 1) % capacity;
                notEmpty.signal();
            } else {
                buffer[tail] = item;
                tail = (tail + 1) % capacity;
                count++;
                notEmpty.signal();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    // 非阻塞操作
    public boolean offer(T item) {
        lock.lock();
        try {
            if (count == capacity && !overwrite) {
                return false; // 队列满且不允许覆盖
            }
            
            if (count == capacity && overwrite) {
                buffer[tail] = item;
                head = (head + 1) % capacity;
                tail = (tail + 1) % capacity;
                notEmpty.signal();
            } else {
                buffer[tail] = item;
                tail = (tail + 1) % capacity;
                count++;
                notEmpty.signal();
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    // 阻塞取出
    public T take() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) {
                notEmpty.await();
            }
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    // 超时阻塞取出
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        lock.lockInterruptibly();
        try {
            while (count == 0) {
                if (nanos <= 0) {
                    return null; // 超时返回
                }
                nanos = notEmpty.awaitNanos(nanos);
            }
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    // 非阻塞取出
    public T poll() {
        lock.lock();
        try {
            if (count == 0) {
                return null; // 队列为空
            }
            return dequeue();
        } finally {
            lock.unlock();
        }
    }

    // 取出元素（内部方法）
    private T dequeue() {
        T item = buffer[head];
        buffer[head] = null; // 清除引用
        head = (head + 1) % capacity;
        count--;
        notFull.signal(); // 通知生产者
        return item;
    }

    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    public int capacity() {
        return capacity;
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return count == 0;
        } finally {
            lock.unlock();
        }
    }

    public boolean isFull() {
        lock.lock();
        try {
            return count == capacity;
        } finally {
            lock.unlock();
        }
    }

    public void clear() {
        lock.lock();
        try {
            head = tail = count = 0;
            for (int i = 0; i < capacity; i++) {
                buffer[i] = null;
            }
            notFull.signalAll(); // 唤醒所有等待的生产者
        } finally {
            lock.unlock();
        }
    }
}