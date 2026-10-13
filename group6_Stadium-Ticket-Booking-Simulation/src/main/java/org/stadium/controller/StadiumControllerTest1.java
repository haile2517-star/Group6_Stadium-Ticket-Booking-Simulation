package org.stadium.controller;

import org.stadium.model.Seat;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;
import org.stadium.model.enums.SeatStatus;
import org.stadium.model.enums.SeatType;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.SectionRepository;
import org.stadium.repository.StadiumRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Bộ kiểm thử đơn vị cho StadiumController.
 * Chạy độc lập qua phương thức main (giống AuthControllerTest và BookingControllerTest).
 */
public class StadiumControllerTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU KIỂM THỬ STADIUMCONTROLLER ===\n");

        File tempDir = new File("target/test-data-stadium");
        tempDir.mkdirs();

        File tempStadiums = new File(tempDir, "stadiums_test.csv");
        File tempSections = new File(tempDir, "sections_test.csv");
        File tempSeats = new File(tempDir, "seats_test.csv");

        try {
            initSampleData(tempStadiums, tempSections, tempSeats);

            StadiumRepository stadiumRepo = new StadiumRepository(tempStadiums.getAbsolutePath());
            SectionRepository sectionRepo = new SectionRepository(tempSections.getAbsolutePath());
            SeatRepository seatRepo = new SeatRepository(tempSeats.getAbsolutePath());

            StadiumController controller = new StadiumController(stadiumRepo, sectionRepo, seatRepo);

            // Test 1: getSections với stadiumId hợp lệ
            test("1. getSections với stadiumId hợp lệ", () -> {
                List<SeatSection> sections = controller.getSections("STD01");
                assertCondition(sections != null, "Danh sách sections không được null");
                assertCondition(sections.size() == 2, "STD01 phải có 2 sections, thực tế: " + sections.size());
                assertCondition("SEC_A".equals(sections.get(0).getSectionId()), "Mã section đầu tiên phải là SEC_A");
            });

            // Test 2: getSections với stadiumId không tồn tại
            test("2. getSections với stadiumId không tồn tại", () -> {
                List<SeatSection> sections = controller.getSections("STD_UNKNOWN");
                assertCondition(sections != null && sections.isEmpty(), "Phải trả về danh sách rỗng");
            });

            // Test 3: getSections với stadiumId null hoặc rỗng
            test("3. getSections với stadiumId null hoặc chuỗi rỗng", () -> {
                List<SeatSection> secNull = controller.getSections(null);
                List<SeatSection> secEmpty = controller.getSections("   ");
                assertCondition(secNull != null && secNull.isEmpty(), "Null stadiumId phải trả về rỗng");
                assertCondition(secEmpty != null && secEmpty.isEmpty(), "Empty stadiumId phải trả về rỗng");
            });

            // Test 4: buildSeatMap với sectionId hợp lệ
            test("4. buildSeatMap với sectionId hợp lệ", () -> {
                String seatMap = controller.buildSeatMap("SEC_A");
                assertCondition(seatMap != null, "Sơ đồ ghế không được null");
                assertCondition(seatMap.contains("SECTION: Khan dai A"), "Bản đồ phải chứa tên khán đài");
                assertCondition(seatMap.contains("STAGE"), "Bản đồ phải có khu vực STAGE");
                assertCondition(seatMap.contains("[O]"), "Bản đồ phải có ký hiệu ghế trống [O]");
                assertCondition(seatMap.contains("[X]"), "Bản đồ phải có ký hiệu ghế đã đặt [X]");
                assertCondition(seatMap.contains("[L]"), "Bản đồ phải có ký hiệu ghế khóa [L]");
                assertCondition(seatMap.contains("Row 01:"), "Bản đồ phải có nhãn Row 01");
                assertCondition(seatMap.contains("Row 02:"), "Bản đồ phải có nhãn Row 02");
            });

            // Test 5: buildSeatMap với sectionId không tồn tại
            test("5. buildSeatMap với sectionId không tồn tại", () -> {
                String seatMap = controller.buildSeatMap("SEC_NOT_EXIST");
                assertCondition(seatMap != null && seatMap.contains("Section not found"),
                        "Phải báo lỗi không tìm thấy section");
            });

            // Test 6: buildSeatMap với sectionId null hoặc rỗng
            test("6. buildSeatMap với sectionId null hoặc chuỗi rỗng", () -> {
                String mapNull = controller.buildSeatMap(null);
                String mapEmpty = controller.buildSeatMap("   ");
                assertCondition(mapNull.contains("Invalid section ID"), "Phải báo lỗi ID không hợp lệ");
                assertCondition(mapEmpty.contains("Invalid section ID"), "Phải báo lỗi ID không hợp lệ");
            });

            // Test 7: Kiểm tra getAvailableSeats
            test("7. getAvailableSeats lọc chính xác ghế AVAILABLE", () -> {
                List<Seat> availableSeats = controller.getAvailableSeats("SEC_A");
                assertCondition(availableSeats != null, "Danh sách không được null");
                assertCondition(availableSeats.size() == 2, "SEC_A có 2 ghế AVAILABLE, thực tế: " + availableSeats.size());
            });

            // Test 8: Kiểm tra getStadium và getAllStadiums
            test("8. getStadium và getAllStadiums", () -> {
                Stadium stadium = controller.getStadium("STD01");
                assertCondition(stadium != null, "Phải tìm thấy sân STD01");
                assertCondition("My Dinh".equals(stadium.getStadiumName()), "Tên sân phải là My Dinh");

                List<Stadium> all = controller.getAllStadiums();
                assertCondition(all.size() == 1, "Tổng số sân phải là 1");
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
        System.out.printf("KẾT QUẢ KIỂM THỬ: %d/%d test cases đã vượt qua.%n", testsPassed, testsRun);
        System.out.println("-------------------------------------------");

        if (testsPassed != testsRun) {
            throw new AssertionError("Có test case thất bại!");
        }
    }

    private static void initSampleData(File stadiumFile, File sectionFile, File seatFile) throws IOException {
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

    private static void test(String name, Runnable runnable) {
        testsRun++;
        try {
            runnable.run();
            testsPassed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable t) {
            System.err.println("[FAIL] " + name + ": " + t.getMessage());
        }
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
