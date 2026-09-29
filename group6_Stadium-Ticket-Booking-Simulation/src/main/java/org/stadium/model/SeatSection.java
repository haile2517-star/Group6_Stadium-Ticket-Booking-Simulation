package org.stadium.model;

import org.stadium.model.enums.SeatType;

public class SeatSection extends BaseEntity {
    private String sectionId;
    private String stadiumId;
    private String sectionName;
    private SeatType sectionType;
    private int totalRows;
    private int seatsPerRow;

    public SeatSection() {
    }

    public SeatSection(String sectionId, String stadiumId, String sectionName, SeatType sectionType, int totalRows, int seatsPerRow) {
        this.sectionId = sectionId;
        this.stadiumId = stadiumId;
        this.sectionName = sectionName;
        this.sectionType = sectionType;
        this.totalRows = totalRows;
        this.seatsPerRow = seatsPerRow;
    }

    @Override
    public String getId() {
        return this.sectionId;
    }

    public int getTotalSeats() {
        return this.totalRows * this.seatsPerRow;
    }

    @Override
    public String toCsvLine() {
        return (sectionId != null ? sectionId : "") + ","
                + (stadiumId != null ? stadiumId : "") + ","
                + (sectionName != null ? sectionName : "") + ","
                + (sectionType != null ? sectionType.name() : "") + ","
                + totalRows + ","
                + seatsPerRow;
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        this.sectionId = parts[0].trim();
        this.stadiumId = parts[1].trim();
        this.sectionName = parts[2].trim();
        this.sectionType = (parts[3] == null || parts[3].trim().isEmpty()) ? null : SeatType.valueOf(parts[3].trim());
        this.totalRows = (parts[4] == null || parts[4].trim().isEmpty()) ? 0 : Integer.parseInt(parts[4].trim());
        this.seatsPerRow = (parts[5] == null || parts[5].trim().isEmpty()) ? 0 : Integer.parseInt(parts[5].trim());
    }

    // Getters and Setters
    public String getSectionId() {
        return sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public SeatType getSectionType() {
        return sectionType;
    }

    public void setSectionType(SeatType sectionType) {
        this.sectionType = sectionType;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getSeatsPerRow() {
        return seatsPerRow;
    }

    public void setSeatsPerRow(int seatsPerRow) {
        this.seatsPerRow = seatsPerRow;
    }

    @Override
    public String toString() {
        return "SeatSection{" +
                "sectionId='" + sectionId + '\'' +
                ", stadiumId='" + stadiumId + '\'' +
                ", sectionName='" + sectionName + '\'' +
                ", sectionType=" + sectionType +
                ", totalRows=" + totalRows +
                ", seatsPerRow=" + seatsPerRow +
                ", totalSeats=" + getTotalSeats() +
                '}';
    }
}
