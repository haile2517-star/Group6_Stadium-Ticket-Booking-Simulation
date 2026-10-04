import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SeatRepository {

    private final String filePath;

    public SeatRepository(String filePath) {
        this.filePath = filePath;
    }

    /*
     * Read all seats from CSV
     */
    public List<Seat> findAll() {

        List<Seat> seats = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            // Skip header
            br.readLine();

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length >= 7) {

                    String seatId = data[0].trim();
                    String sectionId = data[1].trim();
                    String seatNumber = data[2].trim();
                    String seatType = data[3].trim();
                    String status = data[4].trim();
                    String matchId = data[5].trim();

                    int version;

                    try {
                        version = Integer.parseInt(data[6].trim());
                    } catch (NumberFormatException e) {
                        version = 0;
                    }

                    Seat seat = new Seat(
                            seatId,
                            sectionId,
                            seatNumber,
                            seatType,
                            status,
                            matchId,
                            version
                    );

                    seats.add(seat);
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "Error reading seats.csv: "
                    + e.getMessage()
            );
        }

        return seats;
    }

    /*
     * ---------------------------------------------------------
     * findBySectionId(String sectionId)
     * ---------------------------------------------------------
     */
    public List<Seat> findBySectionId(String sectionId) {

        List<Seat> result = new ArrayList<>();

        List<Seat> seats = findAll();

        for (Seat seat : seats) {

            if (seat.getSectionId()
                    .equalsIgnoreCase(sectionId)) {

                result.add(seat);
            }
        }

        return result;
    }

    /*
     * ---------------------------------------------------------
     * findAvailableBySection(String sectionId)
     * ---------------------------------------------------------
     */
    public List<Seat> findAvailableBySection(String sectionId) {

        List<Seat> result = new ArrayList<>();

        List<Seat> seats = findBySectionId(sectionId);

        for (Seat seat : seats) {

            if (seat.isAvailable()) {
                result.add(seat);
            }
        }

        return result;
    }

    /*
     * ---------------------------------------------------------
     * findByMatchAndSection(String matchId, String sectionId)
     * ---------------------------------------------------------
     */
    public List<Seat> findByMatchAndSection(
            String matchId,
            String sectionId) {

        List<Seat> result = new ArrayList<>();

        List<Seat> seats = findAll();

        for (Seat seat : seats) {

            boolean sameMatch =
                    seat.getMatchId()
                            .equalsIgnoreCase(matchId);

            boolean sameSection =
                    seat.getSectionId()
                            .equalsIgnoreCase(sectionId);

            if (sameMatch && sameSection) {
                result.add(seat);
            }
        }

        return result;
    }

    /*
     * ---------------------------------------------------------
     * findBySeatAndMatch(String seatId, String matchId)
     * ---------------------------------------------------------
     */
    public Seat findBySeatAndMatch(
            String seatId,
            String matchId) {

        List<Seat> seats = findAll();

        for (Seat seat : seats) {

            boolean sameSeat =
                    seat.getSeatId()
                            .equalsIgnoreCase(seatId);

            boolean sameMatch =
                    seat.getMatchId()
                            .equalsIgnoreCase(matchId);

            if (sameSeat && sameMatch) {
                return seat;
            }
        }

        return null;
    }
}
