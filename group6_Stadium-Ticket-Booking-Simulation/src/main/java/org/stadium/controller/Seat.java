package model;

public class Seat {

    public enum SeatStatus {
        AVAILABLE, LOCKED, BOOKED
    }

    private String seatId;
    private String row;
    private int number;
    private SeatStatus status;

    public Seat(String seatId, String row, int number,
                SeatStatus status) {
        this.seatId = seatId;
        this.row = row;
        this.number = number;
        this.status = status;
    }

    public String getSeatId() {
        return seatId;
    }

    public String getRow() {
        return row;
    }

    public int getNumber() {
        return number;
    }

    public SeatStatus getStatus() {
        return status;
    }

    public void setStatus(SeatStatus status) {
        this.status = status;
    }
}