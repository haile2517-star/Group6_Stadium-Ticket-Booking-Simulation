package org.stadium.view;

import org.stadium.controller.StadiumController;
import org.stadium.model.Seat;

import java.util.List;

public class SeatMapView {

    private StadiumController stadiumController;

    public SeatMapView() {
    }

    public SeatMapView(StadiumController stadiumController) {
        this.stadiumController = stadiumController;
    }

    public void displaySeatMap(String asciiMap) {
        String border = "+-------------------------------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                            SEAT MAP (ASCII)                             |");
        System.out.println(border);
        if (asciiMap == null || asciiMap.trim().isEmpty()) {
            System.out.println("| [!] No seat map data available to display.                              |");
            System.out.println(border);
            return;
        }

        System.out.println(asciiMap);
        System.out.println(border);
        System.out.println("| Legend: [O] AVAILABLE  |  [X] BOOKED  |  [L] LOCKED                     |");
        System.out.println(border);
    }

    public void displayAvailableSeats(List<Seat> seats) {
        String border = "+---------------------------------------------------------------------------+";
        String gridBorder = "+----------------------+--------------+-----+---------+-----------+-----------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                           AVAILABLE SEATS LIST                            |");
        System.out.println(border);
        if (seats == null || seats.isEmpty()) {
            System.out.println("| [!] No available seats in this section.                                   |");
            System.out.println(border);
            return;
        }

        System.out.println(gridBorder);
        System.out.println("| Seat ID              | Section      | Row | Seat No | Seat Type | Status    |");
        System.out.println(gridBorder);

        int limit = Math.min(seats.size(), 30);
        for (int i = 0; i < limit; i++) {
            Seat seat = seats.get(i);
            String id = this.padOrTruncate(seat.getSeatId(), 20);
            String sec = this.padOrTruncate(seat.getSectionId(), 12);
            String row = this.padOrTruncate(String.valueOf(seat.getRowNumber()), 3);
            String num = this.padOrTruncate(seat.getSeatNumber(), 7);
            String type = this.padOrTruncate(seat.getSeatType() != null ? seat.getSeatType().name() : "N/A", 9);
            String status = this.padOrTruncate(seat.getStatus() != null ? seat.getStatus().name() : "N/A", 9);

            System.out.printf("| %s | %s | %s | %s | %s | %s |%n", id, sec, row, num, type, status);
        }
        System.out.println(gridBorder);
        if (seats.size() > limit) {
            System.out.printf("| Showing first %-2d of %-3d seats. Tip: view seat map for full layout.    |%n", limit,
                    seats.size());
            System.out.println(border);
        }
        System.out.printf("| Total: %-66s |%n", seats.size() + " available seats");
        System.out.println(border);
    }

    public void displaySeatMapForSection(String sectionId) {
        if (this.stadiumController == null) {
            System.out.println("[!] StadiumController is not configured in SeatMapView.");
            return;
        }
        String asciiMap = this.stadiumController.buildSeatMap(sectionId);
        this.displaySeatMap(asciiMap);
    }

    public void displayAvailableSeatsForSection(String sectionId) {
        if (this.stadiumController == null) {
            System.out.println("[!] StadiumController is not configured in SeatMapView.");
            return;
        }
        List<Seat> seats = this.stadiumController.getAvailableSeats(sectionId);
        this.displayAvailableSeats(seats);
    }

    public StadiumController getStadiumController() {
        return this.stadiumController;
    }

    public void setStadiumController(StadiumController stadiumController) {
        this.stadiumController = stadiumController;
    }

    private String padOrTruncate(String text, int width) {
        if (text == null) {
            text = "N/A";
        }
        text = text.trim();
        if (text.length() > width) {
            if (width <= 3) {
                return text.substring(0, width);
            }
            return text.substring(0, width - 3) + "...";
        }
        return String.format("%-" + width + "s", text);
    }
}
