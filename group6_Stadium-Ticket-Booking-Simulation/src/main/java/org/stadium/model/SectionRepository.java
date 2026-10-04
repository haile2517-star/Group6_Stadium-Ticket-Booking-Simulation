import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class SectionRepository {

    private final String filePath;

    public SectionRepository(String filePath) {
        this.filePath = filePath;
    }

    /*
     * Find section by sectionId
     */
    public Section findBySectionId(String sectionId) {

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
                    String stadiumId = data[1].trim();
                    String name = data[2].trim();

                    if (id.equalsIgnoreCase(sectionId)) {

                        return new Section(
                                id,
                                stadiumId,
                                name
                        );
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading sections.csv: "
                    + e.getMessage());
        }

        return null;
    }

    /*
     * Find all sections
     */
    public List<Section> findAll() {

        List<Section> sections = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            br.readLine();

            while ((line = br.readLine()) != null) {

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] data = line.split(",", -1);

                if (data.length >= 3) {

                    sections.add(
                            new Section(
                                    data[0].trim(),
                                    data[1].trim(),
                                    data[2].trim()
                            )
                    );
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading sections.csv: "
                    + e.getMessage());
        }

        return sections;
    }
}