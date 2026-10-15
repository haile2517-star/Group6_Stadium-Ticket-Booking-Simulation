package org.stadium;

import org.stadium.controller.StadiumController;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;

import java.util.List;

/**
 * Điểm khởi chạy chính của hệ thống mô phỏng đặt vé sân vận động.
 * Kiểm thử hoạt động của StadiumController và sơ đồ ghế ASCII map.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("   HỆ THỐNG MÔ PHỎNG ĐẶT VÉ SÂN VẬN ĐỘNG (STADIUM TICKET BOOKING)");
        System.out.println("                   MODULE: SÂN VẬN ĐỘNG & GHẾ NGỒI");
        System.out.println("======================================================================\n");

        StadiumController controller = new StadiumController();

        // 1. Hiển thị toàn bộ sân vận động trong hệ thống
        List<Stadium> stadiums = controller.getAllStadiums();
        System.out.println("============= DANH SÁCH SÂN VẬN ĐỘNG =============");
        for (Stadium stadium : stadiums) {
            System.out.printf("%-10s | %-35s | %-12d | %s%n",
                    stadium.getStadiumId(),
                    stadium.getStadiumName(),
                    stadium.getCapacity(),
                    stadium.getAddress());
        }

        // 2. Lấy danh sách các khán đài của sân Mỹ Đình (STD01) bằng hàm getSections(stadiumId)
        String sampleStadiumId = "STD01";
        System.out.println("\n[Action] Gọi controller.getSections(\"" + sampleStadiumId + "\"):");
        List<SeatSection> sections = controller.getSections(sampleStadiumId);
        System.out.println("============= DANH SÁCH KHÁN ĐÀI (" + sampleStadiumId + ") =============");
        for (SeatSection section : sections) {
            System.out.printf("%-15s | %-25s | %-12s | %-10d | %-10d | %d%n",
                    section.getSectionId(),
                    section.getSectionName(),
                    section.getSectionType() != null ? section.getSectionType().name() : "N/A",
                    section.getTotalRows(),
                    section.getSeatsPerRow(),
                    section.getTotalSeats());
        }

        // 3. Render sơ đồ ghế ASCII map của khán đài VIP (SEC_STD01_A) bằng hàm buildSeatMap(sectionId)
        if (!sections.isEmpty()) {
            String sampleSectionId = sections.get(0).getSectionId();
            System.out.println("\n[Action] Gọi controller.buildSeatMap(\"" + sampleSectionId + "\"):");
            String asciiSeatMap = controller.buildSeatMap(sampleSectionId);
            System.out.println(asciiSeatMap);

            int availableSeats = controller.getAvailableSeats(sampleSectionId).size();
            SeatSection sec = sections.get(0);
            System.out.println("\n--- THÔNG TIN CHI TIẾT KHÁN ĐÀI ---");
            System.out.println("Mã khán đài : " + sec.getSectionId());
            System.out.println("Tên khán đài: " + sec.getSectionName());
            System.out.println("Mã sân      : " + sec.getStadiumId());
            System.out.println("Loại ghế    : " + (sec.getSectionType() != null ? sec.getSectionType().name() : "N/A"));
            System.out.println("Tổng số ghế : " + sec.getTotalSeats());
            System.out.println("Ghế còn trống: " + availableSeats);
            System.out.println("------------------------------------");
        }

        System.out.println("\n Hoàn thành chạy thử nghiệm StadiumController & Sơ đồ ghế ASCII!");
    }
}
