package org.stadium.repository;

import org.stadium.model.Seat;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class SeatRepository extends CsvRepository<Seat> {

    // Khóa dùng cho cơ chế FILE_LOCK (các thread trong cùng JVM dùng chung seats.csv)
    private final ReentrantLock fileLock = new ReentrantLock();

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
        return findByCondition(s -> s.getSectionId().equals(sectionId));
    }

    public List<Seat> findAvailableBySection(String sectionId) {
        return findByCondition(s -> s.getSectionId().equals(sectionId) && s.isAvailable());
    }

    // Chỉ cập nhật khi version hiện tại vẫn bằng expectedVersion (Optimistic Locking)
    public synchronized boolean updateWithOptimisticLock(Seat seat, int expectedVersion) {
        if (seat == null) {
            return false;
        }
        Seat current = findById(seat.getId());
        if (current == null || current.getVersion() != expectedVersion) {
            return false;
        }
        current.setSeatType(seat.getSeatType());
        current.setStatus(seat.getStatus());
        current.incrementVersion();
        saveToFile();
        return true;
    }

    public void acquireFileLock() {
        fileLock.lock();
    }

    public void releaseFileLock() {
        if (fileLock.isHeldByCurrentThread()) {
            fileLock.unlock();
        }
    }
}
