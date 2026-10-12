package model;

import java.util.ArrayList;
import java.util.List;

public class Section {

    private String sectionId;
    private String stadiumId;
    private String sectionName;
    private List<Seat> seats;

    public Section(String sectionId, String stadiumId,
                   String sectionName) {
        this.sectionId = sectionId;
        this.stadiumId = stadiumId;
        this.sectionName = sectionName;
        this.seats = new ArrayList<>();
    }

    public String getSectionId() {
        return sectionId;
    }

    public String getStadiumId() {
        return stadiumId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void addSeat(Seat seat) {
        seats.add(seat);
    }
}