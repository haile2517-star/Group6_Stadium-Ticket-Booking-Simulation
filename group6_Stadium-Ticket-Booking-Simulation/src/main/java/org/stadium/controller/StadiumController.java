
package model;

import model.Seat;
import model.Section;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StadiumController {

    private final List<Section> sections;

    public StadiumController() {
        sections = new ArrayList<>();
        createSampleData();
    }

    // Create sample stadium sections and seats
    private void createSampleData() {
        Section vip = new Section("SEC01", "STA01", "VIP");
        Section standard = new Section("SEC02", "STA01", "STANDARD");
        Section economy = new Section("SEC03", "STA01", "ECONOMY");

        addSampleSeats(vip, 4, 8);
        addSampleSeats(standard, 4, 8);
        addSampleSeats(economy, 4, 8);

        // Sample seat statuses
        vip.getSeats().get(1).setStatus(Seat.SeatStatus.BOOKED);
        vip.getSeats().get(2).setStatus(Seat.SeatStatus.LOCKED);

        standard.getSeats().get(5).setStatus(Seat.SeatStatus.BOOKED);
        economy.getSeats().get(8).setStatus(Seat.SeatStatus.LOCKED);

        sections.add(vip);
        sections.add(standard);
        sections.add(economy);
    }

    // Create seats for each row
    private void addSampleSeats(
            Section section, int rowCount, int seatsPerRow) {

        for (int r = 0; r < rowCount; r++) {
            String row = String.valueOf((char) ('A' + r));

            for (int n = 1; n <= seatsPerRow; n++) {
                String seatId = section.getSectionId()
                        + "-" + row + n;

                Seat seat = new Seat(
                        seatId,
                        row,
                        n,
                        Seat.SeatStatus.AVAILABLE
                );

                section.addSeat(seat);
            }
        }
    }

    // Get sections belonging to a stadium
    public List<Section> getSections(String stadiumId) {
        List<Section> result = new ArrayList<>();

        if (stadiumId == null || stadiumId.trim().isEmpty()) {
            return result;
        }

        for (Section section : sections) {
            if (stadiumId.equals(section.getStadiumId())) {
                result.add(section);
            }
        }

        return result;
    }

    // Build ASCII seat map
    public String buildSeatMap(String sectionId) {
        Section section = findSection(sectionId);

        if (section == null) {
            return "Section not found: " + sectionId;
        }

        Map<String, List<Seat>> rows = new TreeMap<>();

        for (Seat seat : section.getSeats()) {
            rows.computeIfAbsent(
                    seat.getRow(),
                    key -> new ArrayList<>()
            ).add(seat);
        }

        StringBuilder map = new StringBuilder();

        map.append("\n========== ")
           .append(section.getSectionName())
           .append(" ==========\n");

        map.append("              STAGE\n\n");

        for (Map.Entry<String, List<Seat>> entry : rows.entrySet()) {
            List<Seat> rowSeats = entry.getValue();

            rowSeats.sort(
                    Comparator.comparingInt(Seat::getNumber)
            );

            map.append(String.format("%-3s ", entry.getKey()));

            for (Seat seat : rowSeats) {
                map.append("[")
                   .append(getSeatSymbol(seat.getStatus()))
                   .append("] ");
            }

            map.append("\n");
        }

        map.append("\nLegend: [O] Available  ")
           .append("[X] Booked  ")
           .append("[L] Locked\n");

        return map.toString();
    }

    // Find a section by ID
    private Section findSection(String sectionId) {
        if (sectionId == null) {
            return null;
        }

        for (Section section : sections) {
            if (sectionId.equals(section.getSectionId())) {
                return section;
            }
        }

        return null;
    }

    // Convert seat status to a symbol
    private String getSeatSymbol(Seat.SeatStatus status) {
        if (status == null) {
            return "?";
        }

        switch (status) {
            case AVAILABLE:
                return "O";
            case BOOKED:
                return "X";
            case LOCKED:
                return "L";
            default:
                return "?";
        }
    }
}
