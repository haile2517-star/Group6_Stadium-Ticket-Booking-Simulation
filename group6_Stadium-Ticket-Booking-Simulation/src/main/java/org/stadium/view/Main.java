package org.stadium;

import org.stadium.controller.StadiumController;
import org.stadium.model.Seat;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;
import org.stadium.view.AuthView;
import org.stadium.view.SeatMapView;
import org.stadium.view.StadiumView;

import java.util.List;
import java.util.Scanner;

/**
 * Điểm khởi chạy chính của Hệ thống mô phỏng đặt vé sân vận động - Group 6 (LAB211).
 *
 * Đồng bộ hóa toàn diện giữa:
 * - Controller : StadiumController, AuthController, BookingController, TicketController
 * - Model      : Stadium, SeatSection, Seat, Account, Match, Ticket...
 * - Repository : StadiumRepository, SectionRepository, SeatRepository...
 * - View       : StadiumView, SeatMapView, AuthView, BookingView, OrganizerView
 *
 * Phân hệ trọng tâm của Nam:
 * - Module: Sân & Ghế (Stadium & Seat)
 * - View:
 *     + StadiumView:
 *         • displayStadiumList(stadiums)
 *         • displaySectionList(stadiumId, sections)
 *         • displaySectionDetails(sec, count)
 *     + SeatMapView:
 *         • displaySeatMap(asciiMap)
 *         • displayAvailableSeats(seats)
 * - Deliverable: Sơ đồ ghế hiển thị chuẩn ký hiệu [O], [X], [L].
 */
public class Main {

    public static void main(String[] args) {
        StadiumController stadiumController = new StadiumController();
        SeatMapView seatMapView = new SeatMapView(stadiumController);
        StadiumView stadiumView = new StadiumView(stadiumController, seatMapView);

        // Mặc định thực hiện chạy Demo tự động kiểm thử toàn bộ tính năng phân hệ Sân & Ghế
        runAutomatedStadiumDemo(stadiumController, stadiumView, seatMapView);

        // Chạy Menu điều hướng tương tác
        runInteractiveMenu(stadiumController, stadiumView, seatMapView);
    }

    /**
     * Chạy kịch bản Demo tự động đồng bộ hóa Controller và View cho phân hệ Sân & Ghế (Nam).
     */
    public static void runAutomatedStadiumDemo(StadiumController controller,
                                               StadiumView stadiumView,
                                               SeatMapView seatMapView) {
        System.out.println("================================================================================");
        System.out.println("    HỆ THỐNG MÔ PHỎNG ĐẶT VÉ SÂN VẬN ĐỘNG (STADIUM TICKET BOOKING SIMULATION)");
        System.out.println("                        GROUP 6 - LAB211 PROJECT");
        System.out.println("             PHÂN HỆ: SÂN VẬN ĐỘNG & SƠ ĐỒ GHẾ NGỒI (VIEW LAYER - NAM)");
        System.out.println("================================================================================\n");

        // 1. Hiển thị toàn bộ sân vận động qua StadiumView.displayStadiumList(stadiums)
        System.out.println("[BƯỚC 1] StadiumController.getAllStadiums() -> StadiumView.displayStadiumList(stadiums)");
        List<Stadium> stadiums = controller.getAllStadiums();
        stadiumView.displayStadiumList(stadiums);

        if (stadiums.isEmpty()) {
            System.out.println("[!] Chưa tìm thấy dữ liệu sân vận động trong hệ thống.");
            return;
        }

        // 2. Lấy danh sách khán đài của sân đầu tiên qua StadiumView.displaySectionList(stadiumId, sections)
        Stadium defaultStadium = stadiums.get(0);
        String targetStadiumId = defaultStadium.getStadiumId();
        System.out.println("\n[BƯỚC 2] StadiumController.getSections(\"" + targetStadiumId + "\") -> StadiumView.displaySectionList(stadiumId, sections)");
        List<SeatSection> sections = controller.getSections(targetStadiumId);
        stadiumView.displaySectionList(targetStadiumId, sections);

        if (!sections.isEmpty()) {
            SeatSection targetSection = sections.get(0);
            String targetSectionId = targetSection.getSectionId();

            // 3. Hiển thị thông tin chi tiết khán đài qua StadiumView.displaySectionDetails(sec, count)
            System.out.println("\n[BƯỚC 3] StadiumController.getAvailableSeats(\"" + targetSectionId + "\") -> StadiumView.displaySectionDetails(sec, count)");
            List<Seat> availableSeats = controller.getAvailableSeats(targetSectionId);
            stadiumView.displaySectionDetails(targetSection, availableSeats.size());

            // 4. Render sơ đồ ghế chuẩn ký hiệu [O], [X], [L] qua SeatMapView.displaySeatMap(asciiMap)
            System.out.println("\n[BƯỚC 4] StadiumController.buildSeatMap(\"" + targetSectionId + "\") -> SeatMapView.displaySeatMap(asciiMap)");
            String asciiMap = controller.buildSeatMap(targetSectionId);
            seatMapView.displaySeatMap(asciiMap);

            // 5. Hiển thị danh sách ghế trống qua SeatMapView.displayAvailableSeats(seats)
            System.out.println("\n[BƯỚC 5] Hiển thị danh sách ghế trống mẫu qua SeatMapView.displayAvailableSeats(seats)");
            // Hiển thị tối đa 10 ghế trống tiêu biểu để giao diện trực quan, gọn gàng
            List<Seat> sampleSeats = availableSeats.size() > 10 ? availableSeats.subList(0, 10) : availableSeats;
            seatMapView.displayAvailableSeats(sampleSeats);
        }

        System.out.println("\n[✓] Hoàn tất luồng Demo đồng bộ hóa giữa Controller, Model, Repository và View!\n");
    }

    /**
     * Menu tương tác dòng lệnh cho người dùng trực tiếp trải nghiệm và kiểm thử.
     */
    public static void runInteractiveMenu(StadiumController stadiumController,
                                          StadiumView stadiumView,
                                          SeatMapView seatMapView) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("============================== TRÌNH ĐIỀU HƯỚNG ==============================");
            System.out.println("  1. Xem danh sách toàn bộ Sân vận động");
            System.out.println("  2. Xem danh sách Khán đài theo Mã Sân vận động");
            System.out.println("  3. Xem Sơ đồ ghế (ASCII Map) và Chi tiết Khán đài (Ký hiệu [O], [X], [L])");
            System.out.println("  4. Xem danh sách Ghế trống của Khán đài");
            System.out.println("  5. Mở Cổng xác thực & Tài khoản (Auth Portal - Kiệt)");
            System.out.println("  6. Chạy lại Demo phân hệ Sân & Ghế (Nam)");
            System.out.println("  0. Thoát chương trình");
            System.out.println("==============================================================================");
            System.out.print("Vui lòng chọn chức năng (0-6): ");

            if (!scanner.hasNextLine()) {
                break;
            }
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    List<Stadium> stadiums = stadiumController.getAllStadiums();
                    stadiumView.displayStadiumList(stadiums);
                    break;

                case "2":
                    System.out.print("Nhập mã sân vận động (ví dụ: STD01, STD02): ");
                    String stdId = scanner.nextLine().trim();
                    if (!stdId.isEmpty()) {
                        List<SeatSection> sections = stadiumController.getSections(stdId);
                        stadiumView.displaySectionList(stdId, sections);
                    } else {
                        System.out.println("[!] Mã sân không được để trống.");
                    }
                    break;

                case "3":
                    System.out.print("Nhập mã khán đài (ví dụ: SEC_STD01_A, SEC_STD01_B): ");
                    String secId = scanner.nextLine().trim();
                    if (!secId.isEmpty()) {
                        SeatSection sec = stadiumController.getSection(secId);
                        if (sec != null) {
                            int availableCount = stadiumController.getAvailableSeats(secId).size();
                            stadiumView.displaySectionDetails(sec, availableCount);
                            String asciiMap = stadiumController.buildSeatMap(secId);
                            seatMapView.displaySeatMap(asciiMap);
                        } else {
                            System.out.println("[!] Không tìm thấy khán đài: " + secId);
                        }
                    } else {
                        System.out.println("[!] Mã khán đài không được để trống.");
                    }
                    break;

                case "4":
                    System.out.print("Nhập mã khán đài để lọc ghế trống (ví dụ: SEC_STD01_A): ");
                    String secFilterId = scanner.nextLine().trim();
                    if (!secFilterId.isEmpty()) {
                        List<Seat> availableList = stadiumController.getAvailableSeats(secFilterId);
                        seatMapView.displayAvailableSeats(availableList);
                    } else {
                        System.out.println("[!] Mã khán đài không được để trống.");
                    }
                    break;

                case "5":
                    System.out.println("\n[!] Chuyển hướng tới Cổng xác thực & Tài khoản...");
                    AuthView authView = new AuthView();
                    authView.start();
                    break;

                case "6":
                    runAutomatedStadiumDemo(stadiumController, stadiumView, seatMapView);
                    break;

                case "0":
                    System.out.println("\nCảm ơn bạn đã sử dụng hệ thống! Tạm biệt.");
                    return;

                default:
                    System.out.println("[!] Lựa chọn không hợp lệ, vui lòng chọn từ 0 đến 6.");
            }

            System.out.println();
        }
    }
}
