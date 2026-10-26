package org.stadium.controller;

import org.stadium.model.Seat;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;
import org.stadium.model.enums.SeatStatus;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.SectionRepository;
import org.stadium.repository.StadiumRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StadiumController {

    private final StadiumRepository stadiumRepository;
    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;

    public StadiumController(StadiumRepository stadiumRepository,
                             SectionRepository sectionRepository,
                             SeatRepository seatRepository) {
        this.stadiumRepository = stadiumRepository;
        this.sectionRepository = sectionRepository;
        this.seatRepository = seatRepository;
    }

    public StadiumController(SectionRepository sectionRepository, SeatRepository seatRepository) {
        String path = this.resolvePath("stadiums.csv");
        this.stadiumRepository = new StadiumRepository(path);
        this.sectionRepository = sectionRepository;
        this.seatRepository = seatRepository;
    }

    public StadiumController() {
        String pathStadiums = this.resolvePath("stadiums.csv");
        String pathSections = this.resolvePath("sections.csv");
        String pathSeats = this.resolvePath("seats.csv");
        this.stadiumRepository = new StadiumRepository(pathStadiums);
        this.sectionRepository = new SectionRepository(pathSections);
        this.seatRepository = new SeatRepository(pathSeats);
    }

    public String resolvePath(String fileName) {
        if (new File("data/" + fileName).exists()) {
            return "data/" + fileName;
        } else if (new File("../data/" + fileName).exists()) {
            return "../data/" + fileName;
        }
        return "data/" + fileName;
    }

    public List<Stadium> getAllStadiums() {
        if (this.stadiumRepository == null) {
            return Collections.emptyList();
        }
        return this.stadiumRepository.findAll();
    }

    public List<SeatSection> getSections(String stadiumId) {
        if (stadiumId == null || stadiumId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        if (this.sectionRepository == null) {
            return Collections.emptyList();
        }
        return this.sectionRepository.findByStadiumId(stadiumId.trim());
    }

    public String buildSeatMap(String sectionId) {
        if (sectionId == null || sectionId.trim().isEmpty()) {
            return "Invalid section ID.";
        }

        String targetSectionId = sectionId.trim();
        SeatSection section = this.sectionRepository != null ? this.sectionRepository.findById(targetSectionId) : null;
        if (section == null) {
            return "Section not found: " + targetSectionId;
        }

        List<Seat> seats = this.seatRepository != null ? this.seatRepository.findBySectionId(targetSectionId) : Collections.emptyList();
        if (seats == null || seats.isEmpty()) {
            return "No seats found for section: " + targetSectionId;
        }

        Map<Integer, List<Seat>> rows = new TreeMap<>();
        int availableCount = 0;
        int bookedCount = 0;
        int lockedCount = 0;

        for (Seat seat : seats) {
            rows.computeIfAbsent(seat.getRowNumber(), k -> new ArrayList<>()).add(seat);
            SeatStatus status = seat.getStatus();
            if (status == SeatStatus.AVAILABLE) {
                availableCount++;
            } else if (status == SeatStatus.BOOKED) {
                bookedCount++;
            } else if (status == SeatStatus.LOCKED) {
                lockedCount++;
            }
        }

        StringBuilder map = new StringBuilder();

        map.append("\n======================================================================\n");
        map.append(String.format("  SECTION: %s (%s)\n", section.getSectionName(), section.getSectionId()));
        map.append(String.format("  Type: %s | Total Rows: %d | Seats/Row: %d\n",
                section.getSectionType() != null ? section.getSectionType().name() : "N/A",
                section.getTotalRows(),
                section.getSeatsPerRow()));
        map.append(String.format("  Summary: Total: %d | Available [O]: %d | Booked [X]: %d | Locked [L]: %d\n",
                seats.size(), availableCount, bookedCount, lockedCount));
        map.append("======================================================================\n");

        map.append("                         ====================\n");
        map.append("                         |      STAGE       |\n");
        map.append("                         ====================\n\n");

        Pattern digitPattern = Pattern.compile("(\\d+)$");

        for (Map.Entry<Integer, List<Seat>> entry : rows.entrySet()) {
            int rowNumber = entry.getKey();
            List<Seat> rowSeats = entry.getValue();

            rowSeats.sort(Comparator.comparingInt(seat -> this.extractSeatSequence(seat.getSeatNumber(), digitPattern)));

            map.append(String.format("Row %02d: ", rowNumber));

            for (Seat seat : rowSeats) {
                map.append("[").append(this.getSeatSymbol(seat.getStatus())).append("] ");
            }

            map.append("\n");
        }

        map.append("\nLegend: [O] Available    [X] Booked    [L] Locked    [?] Unknown\n");
        map.append("======================================================================\n");

        return map.toString();
    }

    private int extractSeatSequence(String seatNumber, Pattern pattern) {
        if (seatNumber == null || seatNumber.trim().isEmpty()) {
            return 0;
        }
        Matcher matcher = pattern.matcher(seatNumber.trim());
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    private String getSeatSymbol(SeatStatus status) {
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

    public List<Seat> getSeats(String sectionId) {
        if (sectionId == null || sectionId.trim().isEmpty() || this.seatRepository == null) {
            return Collections.emptyList();
        }
        return this.seatRepository.findBySectionId(sectionId.trim());
    }

    public List<Seat> getSeatsBySection(String sectionId) {
        return this.getSeats(sectionId);
    }

    public SeatSection getSection(String sectionId) {
        if (sectionId == null || sectionId.trim().isEmpty() || this.sectionRepository == null) {
            return null;
        }
        return this.sectionRepository.findById(sectionId.trim());
    }

    public Stadium getStadium(String stadiumId) {
        if (stadiumId == null || stadiumId.trim().isEmpty() || this.stadiumRepository == null) {
            return null;
        }
        return this.stadiumRepository.findById(stadiumId.trim());
    }

    public List<Seat> getAvailableSeats(String sectionId) {
        if (sectionId == null || sectionId.trim().isEmpty() || this.seatRepository == null) {
            return Collections.emptyList();
        }
        return this.seatRepository.findAvailableBySection(sectionId.trim());
    }

    public StadiumRepository getStadiumRepository() {
        return this.stadiumRepository;
    }

    public SectionRepository getSectionRepository() {
        return this.sectionRepository;
    }

    public SeatRepository getSeatRepository() {
        return this.seatRepository;
    }
}
