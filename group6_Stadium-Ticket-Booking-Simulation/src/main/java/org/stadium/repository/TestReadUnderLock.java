package org.stadium;

import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class TestReadUnderLock {

    private static final ReentrantLock jvmLock = new ReentrantLock(true);
    private static FileLock currentLock = null;
    private static FileChannel currentChannel = null;
    private static RandomAccessFile currentRaf = null;
    private static File lockFile = new File("target/seats.csv.lock");

    public static void acquireFileLock() {
        jvmLock.lock();
        try {
            currentRaf = new RandomAccessFile(lockFile, "rw");
            currentChannel = currentRaf.getChannel();
            currentLock = currentChannel.lock();
        } catch (Exception e) {
            releaseFileLock();
            throw new RuntimeException("Failed to acquire file lock: " + e.getMessage(), e);
        }
    }

    public static void releaseFileLock() {
        try {
            if (currentLock != null && currentLock.isValid()) {
                currentLock.release();
            }
        } catch (Exception ignored) {
        } finally {
            try {
                if (currentChannel != null && currentChannel.isOpen()) {
                    currentChannel.close();
                }
            } catch (Exception ignored) {
            } finally {
                try {
                    if (currentRaf != null) {
                        currentRaf.close();
                    }
                } catch (Exception ignored) {
                } finally {
                    currentLock = null;
                    currentChannel = null;
                    currentRaf = null;
                    if (jvmLock.isHeldByCurrentThread()) {
                        jvmLock.unlock();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Testing 10 concurrent threads with Java NIO FileLock + JVM Lock...");
        lockFile.getParentFile().mkdirs();

        int numThreads = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numThreads);
        AtomicInteger counter = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    startLatch.await();
                    acquireFileLock();
                    try {
                        int current = counter.get();
                        Thread.sleep(20);
                        counter.set(current + 1);
                        System.out.println("[Thread " + id + "] Increment counter to: " + (current + 1));
                    } finally {
                        releaseFileLock();
                    }
                } catch (Exception e) {
                    System.err.println("[Thread " + id + "] Error: " + e.getMessage());
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }

        startLatch.countDown();
        doneLatch.await();

        System.out.println("Final counter: " + counter.get() + " (Expected: " + numThreads + ")");
        if (counter.get() == numThreads) {
            System.out.println("SUCCESS! All threads safely synchronized with Java NIO FileLock.");
        } else {
            System.err.println("FAILED! Race condition detected.");
        }

        lockFile.delete();
    }
}
