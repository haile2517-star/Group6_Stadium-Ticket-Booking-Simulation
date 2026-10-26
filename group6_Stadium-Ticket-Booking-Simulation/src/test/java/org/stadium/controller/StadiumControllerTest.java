package org.stadium.controller;

import org.stadium.model.Seat;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.SectionRepository;
import org.stadium.repository.StadiumRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class StadiumControllerTest {

    private int testsRun = 0;
    private int testsPassed = 0;

    public static void main(String[] args) {
        new StadiumControllerTest().run();
    }

    public void run() {
        System.out.println("=== BẮT ĐẦU KIỂM THỬ STADIUMCONTROLLER ===\n");

        File tempDir = new File("target/test-data-stadium");
        tempDir.mkdirs();

        File tempStadiums = new File(tempDir, "stadiums_test.csv");
        File tempSections = new File(tempDir, "sections_test.csv");
        File tempSeats = new File(tempDir, "seats_test.csv");

        try {
            this.initSampleData(tempStadiums, tempSections, tempSeats);

            StadiumRepository stadiumRepo = new StadiumRepository(tempStadiums.getAbsolutePath());
            SectionRepository sectionRepo = new SectionRepository(tempSections.getAbsolutePath());
            SeatRepository seatRepo = new SeatRepository(tempSeats.getAbsolutePath());

            StadiumController controller = new StadiumController(stadiumRepo, sectionRepo, seatRepo);

            this.test("1. getSections với stadiumId hợp lệ", () -> {
                List<SeatSection> sections = controller.getSections("STD01");
                this.assertCondition(sections != null, "Danh sách sections không được null");
                this.assertCondition(sections.size() == 2, "STD01 phải có 2 sections, thực tế: " + sections.size());
                this.assertCondition("SEC_A".equals(sections.get(0).getSectionId()), "Mã section đầu tiên phải là SEC_A");
            });

            this.test("2. getSections với stadiumId không tồn tại", () -> {
                List<SeatSection> sections = controller.getSections("STD_UNKNOWN");
                this.assertCondition(sections != null && sections.isEmpty(), "Phải trả về danh sách rỗng");
            });

            this.test("3. getSections với stadiumId null hoặc chuỗi rỗng", () -> {
                List<SeatSection> secNull = controller.getSections(null);
                List<SeatSection> secEmpty = controller.getSections("   ");
                this.assertCondition(secNull != null && secNull.isEmpty(), "Null stadiumId phải trả về rỗng");
                this.assertCondition(secEmpty != null && secEmpty.isEmpty(), "Empty stadiumId phải trả về rỗng");
            });

            this.test("4. buildSeatMap với sectionId hợp lệ", () -> {
                String seatMap = controller.buildSeatMap("SEC_A");
                this.assertCondition(seatMap != null, "Sơ đồ ghế không được null");
                this.assertCondition(seatMap.contains("SECTION: Khan dai A"), "Bản đồ phải chứa tên khán đài");
                this.assertCondition(seatMap.contains("STAGE"), "Bản đồ phải có khu vực STAGE");
                this.assertCondition(seatMap.contains("[O]"), "Bản đồ phải có ký hiệu ghế trống [O]");
                this.assertCondition(seatMap.contains("[X]"), "Bản đồ phải có ký hiệu ghế đã đặt [X]");
                this.assertCondition(seatMap.contains("[L]"), "Bản đồ phải có ký hiệu ghế khóa [L]");
                this.assertCondition(seatMap.contains("Row 01:"), "Bản đồ phải có nhãn Row 01");
                this.assertCondition(seatMap.contains("Row 02:"), "Bản đồ phải có nhãn Row 02");
            });

            this.test("5. buildSeatMap với sectionId không tồn tại", () -> {
                String seatMap = controller.buildSeatMap("SEC_NOT_EXIST");
                this.assertCondition(seatMap != null && seatMap.contains("Section not found"),
                        "Phải báo lỗi không tìm thấy section");
            });

            this.test("6. buildSeatMap với sectionId null hoặc chuỗi rỗng", () -> {
                String mapNull = controller.buildSeatMap(null);
                String mapEmpty = controller.buildSeatMap("   ");
                this.assertCondition(mapNull.contains("Invalid section ID"), "Phải báo lỗi ID không hợp lệ");
                this.assertCondition(mapEmpty.contains("Invalid section ID"), "Phải báo lỗi ID không hợp lệ");
            });

            this.test("7. getAvailableSeats lọc chính xác ghế AVAILABLE và getSeats", () -> {
                List<Seat> availableSeats = controller.getAvailableSeats("SEC_A");
                this.assertCondition(availableSeats != null, "Danh sách không được null");
                this.assertCondition(availableSeats.size() == 2, "SEC_A có 2 ghế AVAILABLE, thực tế: " + availableSeats.size());

                List<Seat> allSeats = controller.getSeats("SEC_A");
                this.assertCondition(allSeats != null && allSeats.size() == 4, "SEC_A phải có 4 ghế tổng cộng");
            });

            this.test("8. getStadium và getAllStadiums", () -> {
                Stadium stadium = controller.getStadium("STD01");
                this.assertCondition(stadium != null, "Phải tìm thấy sân STD01");
                this.assertCondition("My Dinh".equals(stadium.getStadiumName()), "Tên sân phải là My Dinh");

                List<Stadium> all = controller.getAllStadiums();
                this.assertCondition(all.size() == 1, "Tổng số sân phải là 1");
            });

        } catch (Exception e) {
            System.err.println("Lỗi trong quá trình chạy kiểm thử: " + e.getMessage());
            e.printStackTrace();
        } finally {
            tempSeats.delete();
            tempSections.delete();
            tempStadiums.delete();
            tempDir.delete();
        }

        System.out.println("\n-------------------------------------------");
        System.out.printf("KẾT QUẢ KIỂM THỬ: %d/%d test cases đã vượt qua.%n", this.testsPassed, this.testsRun);
        System.out.println("-------------------------------------------");

        if (this.testsPassed != this.testsRun) {
            throw new AssertionError("Có test case thất bại!");
        }
    }

    private void initSampleData(File stadiumFile, File sectionFile, File seatFile) throws IOException {
        try (FileWriter fw = new FileWriter(stadiumFile)) {
            fw.write("stadiumId,stadiumName,address,capacity\n");
            fw.write("STD01,My Dinh,Ha Noi,40000\n");
        }

        try (FileWriter fw = new FileWriter(sectionFile)) {
            fw.write("sectionId,stadiumId,sectionName,sectionType,totalRows,seatsPerRow\n");
            fw.write("SEC_A,STD01,Khan dai A,VIP,2,2\n");
            fw.write("SEC_B,STD01,Khan dai B,STANDARD,2,2\n");
        }

        try (FileWriter fw = new FileWriter(seatFile)) {
            fw.write("seatId,sectionId,rowNumber,seatNumber,seatType,status,version\n");
            fw.write("S1,SEC_A,1,R01-S01,VIP,AVAILABLE,0\n");
            fw.write("S2,SEC_A,1,R01-S02,VIP,BOOKED,0\n");
            fw.write("S3,SEC_A,2,R02-S01,VIP,LOCKED,0\n");
            fw.write("S4,SEC_A,2,R02-S02,VIP,AVAILABLE,0\n");
        }
    }

    private void test(String name, Runnable runnable) {
        this.testsRun++;
        try {
            runnable.run();
            this.testsPassed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable t) {
            System.err.println("[FAIL] " + name + ": " + t.getMessage());
        }
    }

    private void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
