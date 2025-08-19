package uno.xifan.id.generator.distributed.segmentid.concurrent;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

/**
 * 环形缓冲区（RingBuffer）实现
 * 支持线程安全的读写操作
 */
public class RingBuffer<T> {
    private final T[] buffer;
    private int head = 0;  // 读指针
    private int tail = 0;  // 写指针
    private int count = 0; // 缓冲区中的元素数量
    private final int capacity;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    @SuppressWarnings("unchecked")
    public RingBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
    }

    /**
     * 向缓冲区写入一个元素
     * 如果缓冲区已满，则阻塞直到有空间可用
     */
    public void put(T item) throws InterruptedException {
        lock.lock();
        try {
            // 等待直到缓冲区不满
            while (count == capacity) {
                notFull.await();
            }
            
            buffer[tail] = item;
            tail = (tail + 1) % capacity;
            count++;
            
            // 通知等待的读取线程
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从缓冲区读取一个元素
     * 如果缓冲区为空，则阻塞直到有元素可用
     */
    public T take() throws InterruptedException {
        lock.lock();
        try {
            // 等待直到缓冲区不为空
            while (count == 0) {
                notEmpty.await();
            }
            
            T item = buffer[head];
            head = (head + 1) % capacity;
            count--;
            
            // 通知等待的写入线程
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 非阻塞方式尝试写入元素
     * 如果缓冲区已满，返回false
     */
    public boolean offer(T item) {
        lock.lock();
        try {
            if (count == capacity) {
                return false;
            }
            
            buffer[tail] = item;
            tail = (tail + 1) % capacity;
            count++;
            
            notEmpty.signal();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 非阻塞方式尝试读取元素
     * 如果缓冲区为空，返回null
     */
    public T poll() {
        lock.lock();
        try {
            if (count == 0) {
                return null;
            }
            
            T item = buffer[head];
            head = (head + 1) % capacity;
            count--;
            
            notFull.signal();
            return item;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 获取当前缓冲区中的元素数量
     */
    public int size() {
        lock.lock();
        try {
            return count;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 检查缓冲区是否为空
     */
    public boolean isEmpty() {
        lock.lock();
        try {
            return count == 0;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 检查缓冲区是否已满
     */
    public boolean isFull() {
        lock.lock();
        try {
            return count == capacity;
        } finally {
            lock.unlock();
        }
    }
}
