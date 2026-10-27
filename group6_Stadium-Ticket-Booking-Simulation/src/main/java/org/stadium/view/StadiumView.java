package org.stadium.view;

import org.stadium.controller.StadiumController;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;

import java.util.List;

public class StadiumView {

    private StadiumController stadiumController;
    private SeatMapView seatMapView;

    public StadiumView() {
        this.seatMapView = new SeatMapView();
    }

    public StadiumView(StadiumController stadiumController) {
        this.stadiumController = stadiumController;
        this.seatMapView = new SeatMapView(this.stadiumController);
    }

    public StadiumView(StadiumController stadiumController, SeatMapView seatMapView) {
        this.stadiumController = stadiumController;
        this.seatMapView = seatMapView != null ? seatMapView : new SeatMapView(this.stadiumController);
    }

    public void displayStadiumList(List<Stadium> stadiums) {
        String border = "+-------------------------------------------------------------------------+";
        String gridBorder = "+-------+---------------------------+----------+--------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                              STADIUM LIST                               |");
        System.out.println(border);
        if (stadiums == null || stadiums.isEmpty()) {
            System.out.println("| [INFO] No stadium data available to display.                            |");
            System.out.println(border);
            return;
        }

        System.out.println(gridBorder);
        System.out.println("| ID    | Stadium Name              | Capacity | City / Address           |");
        System.out.println(gridBorder);
        for (Stadium stadium : stadiums) {
            String id = this.padOrTruncate(stadium.getStadiumId(), 5);
            String name = this.padOrTruncate(stadium.getStadiumName(), 25);
            String cap = String.format("%,d", stadium.getCapacity());
            String capStr = this.padOrTruncate(cap, 8);
            String addr = this.padOrTruncate(stadium.getAddress(), 24);
            System.out.printf("| %s | %s | %s | %s |%n", id, name, capStr, addr);
        }
        System.out.println(gridBorder);
        System.out.printf("| Total: %-64s |%n", stadiums.size() + " stadiums available");
        System.out.println(border);
    }

    public void displaySectionList(String stadiumId, List<SeatSection> sections) {
        String border = "+-------------------------------------------------------------------------+";
        String gridBorder = "+---------------+----------------------+-----------+----+-------+---------+";
        String title = "SECTION LIST (" + (stadiumId != null ? stadiumId : "N/A") + ")";
        System.out.println();
        System.out.println(border);
        System.out.printf("| %-71s |%n", this.padOrTruncate(title, 71));
        System.out.println(border);
        if (sections == null || sections.isEmpty()) {
            System.out.println("| [INFO] No sections found for this stadium.                              |");
            System.out.println(border);
            return;
        }

        System.out.println(gridBorder);
        System.out.println("| Section ID    | Section Name         | Type      | R  | S/Row | Seats   |");
        System.out.println(gridBorder);
        for (SeatSection section : sections) {
            String id = this.padOrTruncate(section.getSectionId(), 13);
            String name = this.padOrTruncate(section.getSectionName(), 20);
            String type = this.padOrTruncate(section.getSectionType() != null ? section.getSectionType().name() : "N/A", 9);
            String rows = this.padOrTruncate(String.valueOf(section.getTotalRows()), 2);
            String sRow = this.padOrTruncate(String.valueOf(section.getSeatsPerRow()), 5);
            String total = this.padOrTruncate(String.format("%,d", section.getTotalSeats()), 7);

            System.out.printf("| %s | %s | %s | %s | %s | %s |%n", id, name, type, rows, sRow, total);
        }
        System.out.println(gridBorder);
        System.out.printf("| Total: %-64s |%n", sections.size() + " sections in this stadium");
        System.out.println(border);
    }

    public void displaySectionDetails(SeatSection sec, int count) {
        if (sec == null) {
            System.out.println("[INFO] Section information does not exist.");
            return;
        }
        System.out.println();
        String border = "+-------------------------------------------------------------------------+";
        System.out.println(border);
        System.out.println("|                             SECTION DETAILS                             |");
        System.out.println(border);
        System.out.printf("| Section ID   : %-56s |%n", this.padOrTruncate(sec.getSectionId(), 56));
        System.out.printf("| Section Name : %-56s |%n", this.padOrTruncate(sec.getSectionName(), 56));
        System.out.printf("| Stadium ID   : %-56s |%n", this.padOrTruncate(sec.getStadiumId(), 56));
        System.out.printf("| Seat Type    : %-56s |%n", this.padOrTruncate(sec.getSectionType() != null ? sec.getSectionType().name() : "N/A", 56));
        String layout = sec.getTotalRows() + " rows  x  " + sec.getSeatsPerRow() + " seats/row  =  " + sec.getTotalSeats() + " total seats";
        System.out.printf("| Layout       : %-56s |%n", this.padOrTruncate(layout, 56));
        System.out.printf("| Available    : %-56s |%n", this.padOrTruncate(count + " seats [O]", 56));
        System.out.println(border);
    }

    public void displaySeatMap(String seatMapAscii) {
        if (this.seatMapView != null) {
            this.seatMapView.displaySeatMap(seatMapAscii);
        } else {
            if (seatMapAscii == null || seatMapAscii.isEmpty()) {
                System.out.println("[!] No seat map available to display.");
                return;
            }
            System.out.println(seatMapAscii);
        }
    }

    public void displayAllStadiums() {
        if (this.stadiumController == null) {
            System.out.println("[!] StadiumController is not configured in StadiumView.");
            return;
        }
        List<Stadium> stadiums = this.stadiumController.getAllStadiums();
        this.displayStadiumList(stadiums);
    }

    public void displaySectionsByStadium(String stadiumId) {
        if (this.stadiumController == null) {
            System.out.println("[!] StadiumController is not configured in StadiumView.");
            return;
        }
        List<SeatSection> sections = this.stadiumController.getSections(stadiumId);
        this.displaySectionList(stadiumId, sections);
    }

    public void displaySectionFullDetails(String sectionId) {
        if (this.stadiumController == null) {
            System.out.println("[!] StadiumController is not configured in StadiumView.");
            return;
        }
        SeatSection section = this.stadiumController.getSection(sectionId);
        if (section == null) {
            System.out.println("[!] Section not found with ID: " + sectionId);
            return;
        }
        int availableCount = this.stadiumController.getAvailableSeats(sectionId).size();
        this.displaySectionDetails(section, availableCount);
    }

    public StadiumController getStadiumController() {
        return this.stadiumController;
    }

    public void setStadiumController(StadiumController stadiumController) {
        this.stadiumController = stadiumController;
        if (this.seatMapView != null) {
            this.seatMapView.setStadiumController(stadiumController);
        }
    }

    public SeatMapView getSeatMapView() {
        return this.seatMapView;
    }

    public void setSeatMapView(SeatMapView seatMapView) {
        this.seatMapView = seatMapView;
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
