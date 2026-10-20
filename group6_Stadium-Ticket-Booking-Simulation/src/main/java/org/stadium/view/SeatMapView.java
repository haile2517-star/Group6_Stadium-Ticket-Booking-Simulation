package org.stadium.view;

import org.stadium.controller.StadiumController;
import org.stadium.model.Seat;

import java.util.List;

/**
 * Lớp hiển thị (View) sơ đồ ghế và danh sách ghế ngồi.
 * Thành viên phụ trách: Nam (Tuần 6 - View Layer: Sân & Ghế).
 *
 * Chức năng chính:
 * 1. displaySeatMap(String asciiMap): Hiển thị sơ đồ ghế ASCII map trực quan từ StadiumController.buildSeatMap.
 * 2. displayAvailableSeats(List<Seat> seats): Hiển thị danh sách ghế trống định dạng ngay ngắn.
 * 3. Kết nối View với StadiumController để nạp và hiển thị trực tiếp dữ liệu theo mã khán đài.
 *
 * Deliverable: Sơ đồ ghế hiển thị chuẩn ký hiệu:
 *   [O] : Ghế trống (AVAILABLE)
 *   [X] : Ghế đã được đặt (BOOKED)
 *   [L] : Ghế đang khóa (LOCKED)
 */
public class SeatMapView {

    private StadiumController stadiumController;

    /**
     * Khởi tạo SeatMapView mặc định.
     */
    public SeatMapView() {
    }

    /**
     * Khởi tạo SeatMapView có kết nối trực tiếp với StadiumController.
     *
     * @param stadiumController controller điều khiển dữ liệu sân và ghế
     */
    public SeatMapView(StadiumController stadiumController) {
        this.stadiumController = stadiumController;
    }

    /**
     * Hiển thị sơ đồ ghế ASCII map trực quan ra console.
     * Khớp chính xác phương thức trong phân công: +displaySeatMap(asciiMap): void
     *
     * @param asciiMap chuỗi biểu diễn sơ đồ ghế định dạng ASCII từ StadiumController
     */
    public void displaySeatMap(String asciiMap) {
        System.out.println("\n======================= SƠ ĐỒ GHẾ NGỒI (ASCII MAP) =======================");
        if (asciiMap == null || asciiMap.trim().isEmpty()) {
            System.out.println("[!] Không có dữ liệu sơ đồ ghế để hiển thị.");
            return;
        }

        System.out.println(asciiMap);
        System.out.println("Ký hiệu chuẩn: [O] Ghế trống (AVAILABLE) | [X] Đã đặt (BOOKED) | [L] Đang khóa (LOCKED)");
        System.out.println("==========================================================================");
    }

    /**
     * Hiển thị danh sách các ghế còn trống (AVAILABLE) theo định dạng bảng ngay ngắn.
     * Khớp chính xác phương thức trong phân công: +displayAvailableSeats(seats): void
     *
     * @param seats danh sách ghế cần hiển thị
     */
    public void displayAvailableSeats(List<Seat> seats) {
        System.out.println("\n======================== DANH SÁCH GHẾ CÒN TRỐNG ========================");
        if (seats == null || seats.isEmpty()) {
            System.out.println("[!] Không có ghế trống nào khả dụng trong khu vực này.");
            System.out.println("==========================================================================");
            return;
        }

        System.out.printf("%-24s | %-16s | %-6s | %-10s | %-12s | %-12s%n",
                "Mã Ghế (Seat ID)", "Khán Đài", "Hàng", "Số Ghế", "Loại Ghế", "Trạng Thái");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (Seat seat : seats) {
            System.out.printf("%-24s | %-16s | %-6d | %-10s | %-12s | %-12s%n",
                    seat.getSeatId() != null ? seat.getSeatId() : "N/A",
                    seat.getSectionId() != null ? seat.getSectionId() : "N/A",
                    seat.getRowNumber(),
                    seat.getSeatNumber() != null ? seat.getSeatNumber() : "N/A",
                    seat.getSeatType() != null ? seat.getSeatType().name() : "N/A",
                    seat.getStatus() != null ? seat.getStatus().name() : "N/A");
        }
        System.out.println("--------------------------------------------------------------------------------------------------");
        System.out.printf(">> Tổng số ghế trống khả dụng: %d ghế.%n", seats.size());
        System.out.println("==========================================================================");
    }

    /**
     * Kết nối với StadiumController: Lấy sơ đồ ghế theo mã khán đài và hiển thị.
     *
     * @param sectionId mã khán đài (ví dụ: SEC_STD01_A)
     */
    public void displaySeatMapForSection(String sectionId) {
        if (this.stadiumController == null) {
            System.out.println("[!] Chưa cấu hình StadiumController trong SeatMapView.");
            return;
        }
        String asciiMap = this.stadiumController.buildSeatMap(sectionId);
        this.displaySeatMap(asciiMap);
    }

    /**
     * Kết nối với StadiumController: Lấy danh sách ghế trống theo mã khán đài và hiển thị.
     *
     * @param sectionId mã khán đài (ví dụ: SEC_STD01_A)
     */
    public void displayAvailableSeatsForSection(String sectionId) {
        if (this.stadiumController == null) {
            System.out.println("[!] Chưa cấu hình StadiumController trong SeatMapView.");
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
}
