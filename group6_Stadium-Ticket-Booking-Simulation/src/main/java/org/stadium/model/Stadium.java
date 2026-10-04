
public class Stadium {

    private String stadiumId;
    private String stadiumName;
    private String location;

    public Stadium(String stadiumId, String stadiumName, String location) {
        this.stadiumId = stadiumId;
        this.stadiumName = stadiumName;
        this.location = location;
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "Stadium{"
                + "stadiumId='" + stadiumId + '\''
                + ", stadiumName='" + stadiumName + '\''
                + ", location='" + location + '\''
                + '}';
    }
}