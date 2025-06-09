package icu.congee.id.generator.distributed.segmentid;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RingBuffer<T> {
    private final T[] buffer;  // 存储数据的数组
    private int readIndex;     // 读指针位置
    private int writeIndex;    // 写指针位置
    private final int capacity; // 缓冲区容量（实际可用容量为capacity-1）
    private boolean isFull;    // 缓冲区是否已满的标记
    
    private final Lock lock = new ReentrantLock(); // 保证线程安全的锁

    @SuppressWarnings("unchecked")
    public RingBuffer(int capacity) {
        if (capacity <= 1) {
            throw new IllegalArgumentException("Capacity must be greater than 1");
        }
        this.capacity = capacity;
        this.buffer = (T[]) new Object[capacity];
        this.readIndex = 0;
        this.writeIndex = 0;
        this.isFull = false;
    }

    /**
     * 写入数据到缓冲区
     * @param item 要写入的数据
     * @return 写入是否成功
     */
    public boolean write(T item) {
        lock.lock();
        try {
            if (isFull) {
                // 缓冲区已满，可以选择覆盖旧数据或返回失败
                // 这里实现覆盖策略：丢弃最旧数据，写入新数据
                readIndex = (readIndex + 1) % capacity;
            }
            
            buffer[writeIndex] = item;
            writeIndex = (writeIndex + 1) % capacity;
            
            // 当写指针追上读指针时，标记为已满
            isFull = (writeIndex == readIndex);
            
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 从缓冲区读取数据
     * @return 读取到的数据，缓冲区为空时返回null
     */
    public T read() {
        lock.lock();
        try {
            if (isEmpty()) {
                return null; // 缓冲区为空
            }
            
            T item = buffer[readIndex];
            readIndex = (readIndex + 1) % capacity;
            isFull = false; // 读取后缓冲区不再满
            
            return item;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 检查缓冲区是否为空
     * @return 是否为空
     */
    public boolean isEmpty() {
        return (!isFull && (readIndex == writeIndex));
    }

    /**
     * 获取当前缓冲区中的元素数量
     * @return 元素数量
     */
    public int size() {
        if (isFull) {
            return capacity;
        }
        return (writeIndex - readIndex + capacity) % capacity;
    }

    // 测试用例
    public static void main(String[] args) throws InterruptedException {
        RingBuffer<Integer> rb = new RingBuffer<>(3);
        
        // 测试写入
        System.out.println(rb.write(1)); // true
        System.out.println(rb.write(2)); // true
        System.out.println(rb.write(3)); // true（此时缓冲区已满）
        System.out.println(rb.write(4)); // true（覆盖策略生效）
        
        // 测试读取
        System.out.println(rb.read()); // 2（被覆盖的数据）
        System.out.println(rb.read()); // 3
        System.out.println(rb.read()); // 4
        System.out.println(rb.read()); // null（缓冲区已空）
        
        // 测试多线程场景
        Thread producer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                rb.write(i);
                System.out.println("Produced: " + i);
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                Integer item = rb.read();
                System.out.println("Consumed: " + item);
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }
}