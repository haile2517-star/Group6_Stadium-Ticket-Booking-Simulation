package org.stadium.view;

import org.stadium.controller.StadiumController;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;

import java.util.List;

public class StadiumView {

    private StadiumController stadiumController;
    private SeatMapView seatMapView;

    public StadiumView() {
        this.seatMapView = new SeatMapView();
    }

    public StadiumView(StadiumController stadiumController) {
        this.stadiumController = stadiumController;
        this.seatMapView = new SeatMapView(this.stadiumController);
    }

    public StadiumView(StadiumController stadiumController, SeatMapView seatMapView) {
        this.stadiumController = stadiumController;
        this.seatMapView = seatMapView != null ? seatMapView : new SeatMapView(this.stadiumController);
    }

    public void displayStadiumList(List<Stadium> stadiums) {
        System.out.println("\n=========================== DANH SÁCH SÂN VẬN ĐỘNG ===========================");
        if (stadiums == null || stadiums.isEmpty()) {
            System.out.println("[!] Không có dữ liệu sân vận động nào để hiển thị.");
            System.out.println("==============================================================================");
            return;
        }

        System.out.printf("%-10s | %-35s | %-12s | %s%n", "Mã Sân", "Tên Sân Vận Động", "Sức Chứa", "Địa Chỉ");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (Stadium stadium : stadiums) {
            System.out.printf("%-10s | %-35s | %-12d | %s%n",
                    stadium.getStadiumId() != null ? stadium.getStadiumId() : "N/A",
                    stadium.getStadiumName() != null ? stadium.getStadiumName() : "N/A",
                    stadium.getCapacity(),
                    stadium.getAddress() != null ? stadium.getAddress() : "N/A");
        }
        System.out.println("==================================================================================================");
    }

    public void displaySectionList(String stadiumId, List<SeatSection> sections) {
        System.out.println("\n======================== DANH SÁCH KHÁN ĐÀI (" + stadiumId + ") ========================");
        if (sections == null || sections.isEmpty()) {
            System.out.println("[!] Không tìm thấy khu vực / khán đài nào cho sân: " + stadiumId);
            System.out.println("=============================================================================================");
            return;
        }

        System.out.printf("%-15s | %-25s | %-12s | %-10s | %-10s | %s%n",
                "Mã Khán Đài", "Tên Khán Đài", "Loại Ghế", "Số Hàng", "Ghế/Hàng", "Tổng Số Ghế");
        System.out.println("---------------------------------------------------------------------------------------------");
        for (SeatSection section : sections) {
            System.out.printf("%-15s | %-25s | %-12s | %-10d | %-10d | %d%n",
                    section.getSectionId() != null ? section.getSectionId() : "N/A",
                    section.getSectionName() != null ? section.getSectionName() : "N/A",
                    section.getSectionType() != null ? section.getSectionType().name() : "N/A",
                    section.getTotalRows(),
                    section.getSeatsPerRow(),
                    section.getTotalSeats());
        }
        System.out.println("=============================================================================================");
    }

    public void displaySectionDetails(SeatSection sec, int count) {
        if (sec == null) {
            System.out.println("[!] Thông tin khán đài không tồn tại.");
            return;
        }
        System.out.println("\n----------------- THÔNG TIN CHI TIẾT KHÁN ĐÀI -----------------");
        System.out.printf("  Mã khán đài  : %s%n", sec.getSectionId());
        System.out.printf("  Tên khán đài : %s%n", sec.getSectionName());
        System.out.printf("  Mã sân       : %s%n", sec.getStadiumId());
        System.out.printf("  Loại ghế     : %s%n", sec.getSectionType() != null ? sec.getSectionType().name() : "N/A");
        System.out.printf("  Tổng số hàng : %d hàng%n", sec.getTotalRows());
        System.out.printf("  Số ghế/hàng  : %d ghế%n", sec.getSeatsPerRow());
        System.out.printf("  Tổng số ghế  : %d ghế%n", sec.getTotalSeats());
        System.out.printf("  Ghế còn trống: %d ghế [O]%n", count);
        System.out.println("---------------------------------------------------------------");
    }

    public void displaySeatMap(String seatMapAscii) {
        if (this.seatMapView != null) {
            this.seatMapView.displaySeatMap(seatMapAscii);
        } else {
            if (seatMapAscii == null || seatMapAscii.isEmpty()) {
                System.out.println("[!] Không có sơ đồ ghế để hiển thị.");
                return;
            }
            System.out.println(seatMapAscii);
        }
    }

    public void displayAllStadiums() {
        if (this.stadiumController == null) {
            System.out.println("[!] Chưa cấu hình StadiumController trong StadiumView.");
            return;
        }
        List<Stadium> stadiums = this.stadiumController.getAllStadiums();
        this.displayStadiumList(stadiums);
    }

    public void displaySectionsByStadium(String stadiumId) {
        if (this.stadiumController == null) {
            System.out.println("[!] Chưa cấu hình StadiumController trong StadiumView.");
            return;
        }
        List<SeatSection> sections = this.stadiumController.getSections(stadiumId);
        this.displaySectionList(stadiumId, sections);
    }

    public void displaySectionFullDetails(String sectionId) {
        if (this.stadiumController == null) {
            System.out.println("[!] Chưa cấu hình StadiumController trong StadiumView.");
            return;
        }
        SeatSection section = this.stadiumController.getSection(sectionId);
        if (section == null) {
            System.out.println("[!] Không tìm thấy khán đài với mã: " + sectionId);
            return;
        }
        int availableCount = this.stadiumController.getAvailableSeats(sectionId).size();
        this.displaySectionDetails(section, availableCount);
    }

    public StadiumController getStadiumController() {
        return this.stadiumController;
    }

    public void setStadiumController(StadiumController stadiumController) {
        this.stadiumController = stadiumController;
        if (this.seatMapView != null) {
            this.seatMapView.setStadiumController(stadiumController);
        }
    }

    public SeatMapView getSeatMapView() {
        return this.seatMapView;
    }

    public void setSeatMapView(SeatMapView seatMapView) {
        this.seatMapView = seatMapView;
    }
}
