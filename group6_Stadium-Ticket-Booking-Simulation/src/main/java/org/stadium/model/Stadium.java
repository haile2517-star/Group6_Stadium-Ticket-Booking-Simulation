package org.stadium.model;

public class Stadium extends BaseEntity {
    private String stadiumId;
    private String stadiumName;
    private String address;
    private int capacity;

    public Stadium() {
    }

    public Stadium(String stadiumId, String stadiumName, String address, int capacity) {
        this.stadiumId = stadiumId;
        this.stadiumName = stadiumName;
        this.address = address;
        this.capacity = capacity;
    }

    @Override
    public String getId() {
        return this.stadiumId;
    }

    @Override
    public String toCsvLine() {
        return (stadiumId != null ? stadiumId : "") + ","
                + (stadiumName != null ? stadiumName : "") + ","
                + (address != null ? address : "") + ","
                + capacity;
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        this.stadiumId = parts[0].trim();
        this.stadiumName = parts[1].trim();
        this.address = parts[2].trim();
        this.capacity = (parts[3] == null || parts[3].trim().isEmpty()) ? 0 : Integer.parseInt(parts[3].trim());
    }

    // Getters and Setters
    public String getStadiumId() {
        return stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return "Stadium{" +
                "stadiumId='" + stadiumId + '\'' +
                ", stadiumName='" + stadiumName + '\'' +
                ", address='" + address + '\'' +
                ", capacity=" + capacity +
                '}';
    }
}
