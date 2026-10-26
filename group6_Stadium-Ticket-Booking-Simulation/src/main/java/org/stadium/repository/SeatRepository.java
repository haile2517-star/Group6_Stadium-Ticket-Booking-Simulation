package org.stadium.repository;

import org.stadium.model.Seat;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class SeatRepository extends CsvRepository<Seat> {

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
        this.fileLock.lock();
    }

    public void releaseFileLock() {
        if (this.fileLock.isHeldByCurrentThread()) {
            this.fileLock.unlock();
        }
    }
}
