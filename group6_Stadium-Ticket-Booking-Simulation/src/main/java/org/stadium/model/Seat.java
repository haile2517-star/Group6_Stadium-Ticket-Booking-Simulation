package org.stadium.model;

import org.stadium.model.enums.SeatStatus;
import org.stadium.model.enums.SeatType;

public class Seat extends BaseEntity {
    private String seatId;
    private String sectionId;
    private int rowNumber;
    private String seatNumber;
    private SeatType seatType;
    private SeatStatus status;
    private int version;

    public Seat() {
    }

    public Seat(String seatId, String sectionId, int rowNumber, String seatNumber, SeatType seatType, SeatStatus status, int version) {
        this.seatId = seatId;
        this.sectionId = sectionId;
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.status = status;
        this.version = version;
    }

    @Override
    public String getId() {
        return this.seatId;
    }

    // Các hàm nghiệp vụ trạng thái & Khóa lạc quan (Optimistic Locking)
    public int getVersion() {
        return this.version;
    }

    public void incrementVersion() {
        this.version++;
    }

    public boolean isAvailable() {
        return this.status == SeatStatus.AVAILABLE;
    }

    public void markAsLocked() {
        this.status = SeatStatus.LOCKED;
    }

    public void markAsBooked() {
        this.status = SeatStatus.BOOKED;
    }

    public void markAsAvailable() {
        this.status = SeatStatus.AVAILABLE;
    }

    @Override
    public String toCsvLine() {
        return (seatId != null ? seatId : "") + ","
                + (sectionId != null ? sectionId : "") + ","
                + rowNumber + ","
                + (seatNumber != null ? seatNumber : "") + ","
                + (seatType != null ? seatType.name() : "") + ","
                + (status != null ? status.name() : "") + ","
                + version;
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        this.seatId = parts[0].trim();
        this.sectionId = parts[1].trim();
        this.rowNumber = (parts[2] == null || parts[2].trim().isEmpty()) ? 0 : Integer.parseInt(parts[2].trim());
        this.seatNumber = parts[3].trim();
        this.seatType = (parts[4] == null || parts[4].trim().isEmpty()) ? null : SeatType.valueOf(parts[4].trim());
        this.status = (parts[5] == null || parts[5].trim().isEmpty()) ? null : SeatStatus.valueOf(parts[5].trim());
        this.version = (parts[6] == null || parts[6].trim().isEmpty()) ? 0 : Integer.parseInt(parts[6].trim());
    }

    // Getters and Setters
    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public SeatType getSeatType() {
        return seatType;
    }

    public void setSeatType(SeatType seatType) {
        this.seatType = seatType;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatId='" + seatId + '\'' +
                ", sectionId='" + sectionId + '\'' +
                ", rowNumber=" + rowNumber +
                ", seatNumber='" + seatNumber + '\'' +
                ", seatType=" + seatType +
                ", status=" + status +
                ", version=" + version +
                '}';
    }
}
