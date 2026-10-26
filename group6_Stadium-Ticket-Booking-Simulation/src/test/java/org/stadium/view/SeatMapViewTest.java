package org.stadium.view;

import org.stadium.controller.StadiumController;
import org.stadium.model.Seat;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;
import org.stadium.model.enums.SeatStatus;
import org.stadium.model.enums.SeatType;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.SectionRepository;
import org.stadium.repository.StadiumRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SeatMapViewTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private static PrintStream createPrintStream(ByteArrayOutputStream out) {
        try {
            return new PrintStream(out, true, StandardCharsets.UTF_8.name());
        } catch (Exception e) {
            return new PrintStream(out, true);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== BẮT ĐẦU KIỂM THỬ SEATMAPVIEW & STADIUMVIEW (TUẦN 6 - NAM) ===\n");

        File tempDir = new File("target/test-data-seatmapview");
        tempDir.mkdirs();

        File tempStadiums = new File(tempDir, "stadiums_view_test.csv");
        File tempSections = new File(tempDir, "sections_view_test.csv");
        File tempSeats = new File(tempDir, "seats_view_test.csv");

        try {
            initSampleData(tempStadiums, tempSections, tempSeats);

            StadiumRepository stadiumRepo = new StadiumRepository(tempStadiums.getAbsolutePath());
            SectionRepository sectionRepo = new SectionRepository(tempSections.getAbsolutePath());
            SeatRepository seatRepo = new SeatRepository(tempSeats.getAbsolutePath());

            StadiumController controller = new StadiumController(stadiumRepo, sectionRepo, seatRepo);
            SeatMapView seatMapView = new SeatMapView(controller);
            StadiumView stadiumView = new StadiumView(controller, seatMapView);

            test("1. Test displaySeatMap với ký hiệu chuẩn [O], [X], [L]", () -> {
                String sampleAscii = controller.buildSeatMap("SEC_A");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(out));

                try {
                    seatMapView.displaySeatMap(sampleAscii);
                } finally {
                    System.setOut(originalOut);
                }

                String output = new String(out.toByteArray(), StandardCharsets.UTF_8);
                assertCondition(output.contains("SƠ ĐỒ GHẾ NGỒI (ASCII MAP)"), "Phải có tiêu đề sơ đồ ghế");
                assertCondition(output.contains("[O]"), "Phải có ký hiệu ghế trống [O]");
                assertCondition(output.contains("[X]"), "Phải có ký hiệu ghế đã đặt [X]");
                assertCondition(output.contains("[L]"), "Phải có ký hiệu ghế khóa [L]");
                assertCondition(output.contains("Ký hiệu chuẩn:"), "Phải có chú giải ký hiệu chuẩn");
            });

            test("2. Test displaySeatMap với dữ liệu null hoặc rỗng", () -> {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(out));

                try {
                    seatMapView.displaySeatMap(null);
                    seatMapView.displaySeatMap("   ");
                } finally {
                    System.setOut(originalOut);
                }

                String output = new String(out.toByteArray(), StandardCharsets.UTF_8);
                assertCondition(output.contains("Không có dữ liệu sơ đồ ghế"), "Phải thông báo khi sơ đồ null/rỗng");
            });

            test("3. Test displayAvailableSeats với danh sách ghế hợp lệ", () -> {
                List<Seat> seats = controller.getAvailableSeats("SEC_A");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(out));

                try {
                    seatMapView.displayAvailableSeats(seats);
                } finally {
                    System.setOut(originalOut);
                }

                String output = new String(out.toByteArray(), StandardCharsets.UTF_8);
                assertCondition(output.contains("DANH SÁCH GHẾ CÒN TRỐNG"), "Phải có tiêu đề danh sách ghế trống");
                assertCondition(output.contains("SEC_A_R01_S01"), "Phải hiển thị mã ghế SEC_A_R01_S01");
                assertCondition(output.contains("Tổng số ghế trống khả dụng:"), "Phải có tổng kết số ghế khả dụng");
            });

            test("4. Test displayAvailableSeats với danh sách rỗng", () -> {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(out));

                try {
                    seatMapView.displayAvailableSeats(Collections.emptyList());
                } finally {
                    System.setOut(originalOut);
                }

                String output = new String(out.toByteArray(), StandardCharsets.UTF_8);
                assertCondition(output.contains("Không có ghế trống nào khả dụng"), "Phải thông báo khi không có ghế trống");
            });

            test("5. Test StadiumView.displayStadiumList", () -> {
                List<Stadium> stadiums = controller.getAllStadiums();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(out));

                try {
                    stadiumView.displayStadiumList(stadiums);
                } finally {
                    System.setOut(originalOut);
                }

                String output = new String(out.toByteArray(), StandardCharsets.UTF_8);
                assertCondition(output.contains("DANH SÁCH SÂN VẬN ĐỘNG"), "Phải có tiêu đề danh sách sân");
                assertCondition(output.contains("STD01"), "Phải hiển thị mã sân STD01");
                assertCondition(output.contains("San Quoc Gia My Dinh"), "Phải hiển thị tên sân");
            });

            test("6. Test StadiumView.displaySectionList", () -> {
                List<SeatSection> sections = controller.getSections("STD01");
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(out));

                try {
                    stadiumView.displaySectionList("STD01", sections);
                } finally {
                    System.setOut(originalOut);
                }

                String output = new String(out.toByteArray(), StandardCharsets.UTF_8);
                assertCondition(output.contains("DANH SÁCH KHÁN ĐÀI (STD01)"), "Phải có tiêu đề danh sách khán đài");
                assertCondition(output.contains("SEC_A"), "Phải có mã khán đài SEC_A");
                assertCondition(output.contains("VIP"), "Phải có loại ghế VIP");
            });

            test("7. Test StadiumView.displaySectionDetails(sec, count)", () -> {
                SeatSection sec = controller.getSection("SEC_A");
                int availableCount = controller.getAvailableSeats("SEC_A").size();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(out));

                try {
                    stadiumView.displaySectionDetails(sec, availableCount);
                } finally {
                    System.setOut(originalOut);
                }

                String output = new String(out.toByteArray(), StandardCharsets.UTF_8);
                assertCondition(output.contains("THÔNG TIN CHI TIẾT KHÁN ĐÀI"), "Phải có tiêu đề thông tin khán đài");
                assertCondition(output.contains("Mã khán đài  : SEC_A"), "Phải hiển thị mã khán đài SEC_A");
                assertCondition(output.contains("Ghế còn trống:"), "Phải hiển thị ghế còn trống");
            });

            test("8. Test Kết nối trực tiếp giữa View và StadiumController", () -> {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(out));

                try {
                    seatMapView.displaySeatMapForSection("SEC_A");
                    seatMapView.displayAvailableSeatsForSection("SEC_A");
                    stadiumView.displayAllStadiums();
                    stadiumView.displaySectionsByStadium("STD01");
                    stadiumView.displaySectionFullDetails("SEC_A");
                } finally {
                    System.setOut(originalOut);
                }

                String output = new String(out.toByteArray(), StandardCharsets.UTF_8);
                assertCondition(output.contains("[O]"), "displaySeatMapForSection phải xuất ra [O]");
                assertCondition(output.contains("SEC_A_R01_S01"), "displayAvailableSeatsForSection phải xuất mã ghế");
                assertCondition(output.contains("STD01"), "displayAllStadiums phải hiển thị STD01");
            });

            System.out.printf("%n>>> KẾT QUẢ KIỂM THỬ: %d/%d PASSED! <<<%n", testsPassed, testsRun);

        } catch (Exception e) {
            System.err.println("Lỗi trong quá trình kiểm thử: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initSampleData(File stadiums, File sections, File seats) throws IOException {
        try (FileWriter w = new FileWriter(stadiums)) {
            w.write("stadiumId,stadiumName,address,capacity\n");
            w.write("STD01,San Quoc Gia My Dinh,Le Duc Tho - Ha Noi,40000\n");
            w.write("STD02,San Hang Day,Trinh Hoai Duc - Ha Noi,20000\n");
        }

        try (FileWriter w = new FileWriter(sections)) {
            w.write("sectionId,stadiumId,sectionName,sectionType,totalRows,seatsPerRow\n");
            w.write("SEC_A,STD01,Khan dai A,VIP,2,3\n");
            w.write("SEC_B,STD01,Khan dai B,STANDARD,2,3\n");
        }

        try (FileWriter w = new FileWriter(seats)) {
            w.write("seatId,sectionId,rowNumber,seatNumber,seatType,status,version\n");

            w.write("SEC_A_R01_S01,SEC_A,1,R01-S01,VIP,AVAILABLE,0\n");
            w.write("SEC_A_R01_S02,SEC_A,1,R01-S02,VIP,BOOKED,0\n");
            w.write("SEC_A_R01_S03,SEC_A,1,R01-S03,VIP,LOCKED,0\n");

            w.write("SEC_A_R02_S01,SEC_A,2,R02-S01,VIP,AVAILABLE,0\n");
            w.write("SEC_A_R02_S02,SEC_A,2,R02-S02,VIP,AVAILABLE,0\n");
            w.write("SEC_A_R02_S03,SEC_A,2,R02-S03,VIP,BOOKED,0\n");
        }
    }

    private static void test(String name, Runnable runnable) {
        testsRun++;
        try {
            runnable.run();
            testsPassed++;
            System.out.println("  [PASS] " + name);
        } catch (Throwable t) {
            System.out.println("  [FAIL] " + name + ": " + t.getMessage());
        }
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
