public class Seat {

    private String seatId;
    private String sectionId;
    private String seatNumber;
    private String seatType;
    private String status;
    private String matchId;
    private int version;

    public Seat(String seatId,
                String sectionId,
                String seatNumber,
                String seatType,
                String status,
                String matchId,
                int version) {

        this.seatId = seatId;
        this.sectionId = sectionId;
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.status = status;
        this.matchId = matchId;
        this.version = version;
    }

    public String getSeatId() {
        return seatId;
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public String getStatus() {
        return status;
    }

    public String getMatchId() {
        return matchId;
    }

    public int getVersion() {
        return version;
    }

    public boolean isAvailable() {
        return "AVAILABLE".equalsIgnoreCase(status);
    }

    public boolean isLocked() {
        return "LOCKED".equalsIgnoreCase(status);
    }

    public boolean isBooked() {
        return "BOOKED".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "Seat{"
                + "seatId='" + seatId + '\''
                + ", sectionId='" + sectionId + '\''
                + ", seatNumber='" + seatNumber + '\''
                + ", seatType='" + seatType + '\''
                + ", status='" + status + '\''
                + ", matchId='" + matchId + '\''
                + ", version=" + version
                + '}';
    }
}