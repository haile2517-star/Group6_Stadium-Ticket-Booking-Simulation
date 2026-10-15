package org.stadium.view;

import org.stadium.model.Seat;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;

import java.util.List;

/**
 * Lớp hiển thị (View) phục vụ module Sân vận động & Khán đài / Ghế ngồi.
 * Chuẩn mô hình MVC: View nhận dữ liệu từ Controller và hiển thị trực quan ra console.
 */
public class StadiumView {

    /**
     * Hiển thị danh sách các sân vận động.
     */
    public void displayStadiumList(List<Stadium> stadiums) {
        System.out.println("\n============= DANH SÁCH SÂN VẬN ĐỘNG =============");
        if (stadiums == null || stadiums.isEmpty()) {
            System.out.println("Không có dữ liệu sân vận động nào.");
            return;
        }

        System.out.printf("%-10s | %-35s | %-12s | %s%n", "Mã Sân", "Tên Sân Vận Động", "Sức Chứa", "Địa Chỉ");
        System.out.println("----------------------------------------------------------------------------------");
        for (Stadium stadium : stadiums) {
            System.out.printf("%-10s | %-35s | %-12d | %s%n",
                    stadium.getStadiumId(),
                    stadium.getStadiumName(),
                    stadium.getCapacity(),
                    stadium.getAddress());
        }
        System.out.println("==================================================");
    }

    /**
     * Hiển thị danh sách các khu vực / khán đài của sân.
     */
    public void displaySectionList(String stadiumId, List<SeatSection> sections) {
        System.out.println("\n============= DANH SÁCH KHÁN ĐÀI (" + stadiumId + ") =============");
        if (sections == null || sections.isEmpty()) {
            System.out.println("Không tìm thấy khu vực nào cho sân: " + stadiumId);
            return;
        }

        System.out.printf("%-15s | %-25s | %-12s | %-10s | %-10s | %s%n",
                "Mã Khán Đài", "Tên Khán Đài", "Loại Ghế", "Số Hàng", "Ghế/Hàng", "Tổng Số Ghế");
        System.out.println("---------------------------------------------------------------------------------------------");
        for (SeatSection section : sections) {
            System.out.printf("%-15s | %-25s | %-12s | %-10d | %-10d | %d%n",
                    section.getSectionId(),
                    section.getSectionName(),
                    section.getSectionType() != null ? section.getSectionType().name() : "N/A",
                    section.getTotalRows(),
                    section.getSeatsPerRow(),
                    section.getTotalSeats());
        }
        System.out.println("=============================================================================================");
    }

    /**
     * Hiển thị sơ đồ ghế ASCII map ra console.
     */
    public void displaySeatMap(String seatMapAscii) {
        if (seatMapAscii == null || seatMapAscii.isEmpty()) {
            System.out.println("Không có sơ đồ ghế để hiển thị.");
            return;
        }
        System.out.println(seatMapAscii);
    }

    /**
     * Hiển thị thông báo chi tiết về một khán đài.
     */
    public void displaySectionDetails(SeatSection section, int availableSeatsCount) {
        if (section == null) {
            System.out.println("Thông tin khán đài không tồn tại.");
            return;
        }
        System.out.println("\n--- THÔNG TIN CHI TIẾT KHÁN ĐÀI ---");
        System.out.println("Mã khán đài : " + section.getSectionId());
        System.out.println("Tên khán đài: " + section.getSectionName());
        System.out.println("Mã sân      : " + section.getStadiumId());
        System.out.println("Loại ghế    : " + (section.getSectionType() != null ? section.getSectionType().name() : "N/A"));
        System.out.println("Tổng số ghế : " + section.getTotalSeats());
        System.out.println("Ghế còn trống: " + availableSeatsCount);
        System.out.println("------------------------------------");
    }
}
