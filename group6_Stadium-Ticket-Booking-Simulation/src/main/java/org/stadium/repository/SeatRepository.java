package org.stadium.repository;

import org.stadium.model.Seat;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class SeatRepository extends CsvRepository<Seat> {

    private final ReentrantLock jvmLock = new ReentrantLock();
    private RandomAccessFile lockRaf;
    private FileChannel lockChannel;
    private FileLock currentFileLock;

    public SeatRepository(String filePath) {
        super(filePath);
    }

    @Override
    public Seat createEntity() {
        return new Seat();
    }

    @Override
    public String getHeader() {
        return "seatId,sectionId,rowNumber,seatNumber,seatType,status,version";
    }

    public List<Seat> findBySectionId(String sectionId) {
        return this.findByCondition(s -> s.getSectionId().equals(sectionId));
    }

    public List<Seat> findAvailableBySection(String sectionId) {
        return this.findByCondition(s -> s.getSectionId().equals(sectionId) && s.isAvailable());
    }

    @Override
    public synchronized Seat findById(String id) {
        return super.findById(id);
    }

    public synchronized boolean updateWithOptimisticLock(Seat seat, int expectedVersion) {
        if (seat == null || seat.getId() == null || expectedVersion < 0) {
            return false;
        }
        this.loadFromFile();
        Seat current = this.findById(seat.getId());
        if (current == null || current.getVersion() != expectedVersion) {
            return false;
        }
        current.setSeatType(seat.getSeatType());
        current.setStatus(seat.getStatus());
        current.incrementVersion();
        seat.setVersion(current.getVersion());
        this.saveToFile();
        return true;
    }

    public void acquireFileLock() {
        this.jvmLock.lock();
        try {
            String lockFilePath = this.getFilePath() + ".lock";
            File lockFile = new File(lockFilePath);
            File parentDir = lockFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            this.lockRaf = new RandomAccessFile(lockFile, "rw");
            this.lockChannel = this.lockRaf.getChannel();
            this.currentFileLock = this.lockChannel.lock();
        } catch (IOException e) {
            this.cleanUpLockResources();
            if (this.jvmLock.isHeldByCurrentThread()) {
                this.jvmLock.unlock();
            }
            throw new RuntimeException("[SeatRepository] Loi chiem FileLock tren kenh file seats.csv: " + e.getMessage(), e);
        }
    }

    public void releaseFileLock() {
        try {
            if (this.currentFileLock != null && this.currentFileLock.isValid()) {
                try {
                    this.currentFileLock.release();
                } catch (IOException ignored) {
                }
            }
        } finally {
            this.cleanUpLockResources();
            if (this.jvmLock.isHeldByCurrentThread()) {
                this.jvmLock.unlock();
            }
        }
    }

    private void cleanUpLockResources() {
        if (this.lockChannel != null) {
            try {
                this.lockChannel.close();
            } catch (IOException ignored) {
            }
            this.lockChannel = null;
        }
        if (this.lockRaf != null) {
            try {
                this.lockRaf.close();
            } catch (IOException ignored) {
            }
            this.lockRaf = null;
        }
        this.currentFileLock = null;
    }
}

