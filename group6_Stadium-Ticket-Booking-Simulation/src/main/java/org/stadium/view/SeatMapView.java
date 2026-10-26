package org.stadium.view;

import org.stadium.controller.StadiumController;
import org.stadium.model.Seat;

import java.util.List;

public class SeatMapView {

    private StadiumController stadiumController;

    public SeatMapView() {
    }

    public SeatMapView(StadiumController stadiumController) {
        this.stadiumController = stadiumController;
    }

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

    public void displaySeatMapForSection(String sectionId) {
        if (this.stadiumController == null) {
            System.out.println("[!] Chưa cấu hình StadiumController trong SeatMapView.");
            return;
        }
        String asciiMap = this.stadiumController.buildSeatMap(sectionId);
        this.displaySeatMap(asciiMap);
    }

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
