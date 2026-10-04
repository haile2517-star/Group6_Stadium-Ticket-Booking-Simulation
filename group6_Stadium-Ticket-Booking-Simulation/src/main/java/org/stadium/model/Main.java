import java.util.List;

public class Main {

    public static void main(String[] args) {

        /*
         * CSV file locations
         */
        String stadiumFile = "data/stadiums.csv";
        String sectionFile = "data/sections.csv";
        String seatFile = "data/seats.csv";

        /*
         * Create repositories
         */
        StadiumRepository stadiumRepository =
                new StadiumRepository(stadiumFile);

        SectionRepository sectionRepository =
                new SectionRepository(sectionFile);

        SeatRepository seatRepository =
                new SeatRepository(seatFile);

        // =====================================================
        // 1. TEST StadiumRepository
        // =====================================================

        System.out.println("=================================");
        System.out.println("1. STADIUM REPOSITORY");
        System.out.println("=================================");

        Stadium stadium =
                stadiumRepository.findByStadiumId("STD001");

        if (stadium != null) {

            System.out.println("Found stadium:");
            System.out.println(stadium);

        } else {

            System.out.println("Stadium not found!");
        }

        // =====================================================
        // 2. TEST SectionRepository
        // =====================================================

        System.out.println();
        System.out.println("=================================");
        System.out.println("2. SECTION REPOSITORY");
        System.out.println("=================================");

        Section section =
                sectionRepository.findBySectionId("SEC001");

        if (section != null) {

            System.out.println("Found section:");
            System.out.println(section);

        } else {

            System.out.println("Section not found!");
        }

        // =====================================================
        // 3. Find seats by section
        // =====================================================

        System.out.println();
        System.out.println("=================================");
        System.out.println("3. FIND SEATS BY SECTION");
        System.out.println("=================================");

        List<Seat> seats =
                seatRepository.findBySectionId("SEC001");

        for (Seat seat : seats) {
            System.out.println(seat);
        }

        System.out.println(
                "Total seats: " + seats.size()
        );

        // =====================================================
        // 4. Find available seats
        // =====================================================

        System.out.println();
        System.out.println("=================================");
        System.out.println("4. AVAILABLE SEATS");
        System.out.println("=================================");

        List<Seat> availableSeats =
                seatRepository.findAvailableBySection("SEC001");

        for (Seat seat : availableSeats) {
            System.out.println(seat);
        }

        System.out.println(
                "Available seats: "
                + availableSeats.size()
        );

        // =====================================================
        // 5. Find seats by match + section
        // =====================================================

        System.out.println();
        System.out.println("=================================");
        System.out.println("5. MATCH + SECTION");
        System.out.println("=================================");

        List<Seat> matchSeats =
                seatRepository.findByMatchAndSection(
                        "MATCH001",
                        "SEC001"
                );

        for (Seat seat : matchSeats) {
            System.out.println(seat);
        }

        System.out.println(
                "Total: " + matchSeats.size()
        );

        // =====================================================
        // 6. Find specific seat + match
        // =====================================================

        System.out.println();
        System.out.println("=================================");
        System.out.println("6. SEAT + MATCH");
        System.out.println("=================================");

        Seat selectedSeat =
                seatRepository.findBySeatAndMatch(
                        "SEAT001",
                        "MATCH001"
                );

        if (selectedSeat != null) {

            System.out.println(
                    "Seat found:"
            );

            System.out.println(selectedSeat);

        } else {

            System.out.println(
                    "Seat not found!"
            );
        }

        // =====================================================
        // END
        // =====================================================

        System.out.println();
        System.out.println("=================================");
        System.out.println("PROGRAM FINISHED");
        System.out.println("=================================");
    }
}
