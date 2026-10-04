import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class StadiumRepository {

    private final String filePath;

    public StadiumRepository(String filePath) {
        this.filePath = filePath;
    }

    /*
     * Find stadium by stadiumId
     */
    public Stadium findByStadiumId(String stadiumId) {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            // Skip header
            br.readLine();

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length >= 3) {

                    String id = data[0].trim();
                    String name = data[1].trim();
                    String location = data[2].trim();

                    if (id.equalsIgnoreCase(stadiumId)) {
                        return new Stadium(id, name, location);
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading stadiums.csv: " + e.getMessage());
        }

        return null;
    }

    /*
     * Find all stadiums
     */
    public List<Stadium> findAll() {

        List<Stadium> stadiums = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            br.readLine();

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length >= 3) {

                    stadiums.add(
                            new Stadium(
                                    data[0].trim(),
                                    data[1].trim(),
                                    data[2].trim()
                            )
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading stadiums.csv: " + e.getMessage());
        }

        return stadiums;
    }
}