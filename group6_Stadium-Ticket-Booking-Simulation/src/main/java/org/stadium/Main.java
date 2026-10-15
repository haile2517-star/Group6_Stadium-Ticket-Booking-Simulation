package org.stadium;

import org.stadium.controller.StadiumController;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;
import org.stadium.view.StadiumView;

import java.util.List;

/**
 * Điểm khởi chạy chính của hệ thống mô phỏng đặt vé sân vận động.
 * Trực quan hóa và kiểm thử hoạt động của StadiumController & StadiumView.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("   HỆ THỐNG MÔ PHỎNG ĐẶT VÉ SÂN VẬN ĐỘNG (STADIUM TICKET BOOKING)");
        System.out.println("                   MODULE: SÂN VẬN ĐỘNG & GHẾ NGỒI");
        System.out.println("======================================================================\n");

        StadiumController controller = new StadiumController();
        StadiumView view = new StadiumView();

        // 1. Hiển thị toàn bộ sân vận động trong hệ thống
        List<Stadium> stadiums = controller.getAllStadiums();
        view.displayStadiumList(stadiums);

        // 2. Lấy danh sách các khán đài của sân Mỹ Đình (STD01) bằng hàm getSections(stadiumId)
        String sampleStadiumId = "STD01";
        System.out.println("\n[Action] Gọi controller.getSections(\"" + sampleStadiumId + "\"):");
        List<SeatSection> sections = controller.getSections(sampleStadiumId);
        view.displaySectionList(sampleStadiumId, sections);

        // 3. Render sơ đồ ghế ASCII map của khán đài VIP (SEC_STD01_A) bằng hàm buildSeatMap(sectionId)
        if (!sections.isEmpty()) {
            String sampleSectionId = sections.get(0).getSectionId();
            System.out.println("\n[Action] Gọi controller.buildSeatMap(\"" + sampleSectionId + "\"):");
            String asciiSeatMap = controller.buildSeatMap(sampleSectionId);
            view.displaySeatMap(asciiSeatMap);

            int availableSeats = controller.getAvailableSeats(sampleSectionId).size();
            view.displaySectionDetails(sections.get(0), availableSeats);
        }

        System.out.println("\n Hoàn thành chạy thử nghiệm StadiumController & Sơ đồ ghế ASCII!");
    }
}
