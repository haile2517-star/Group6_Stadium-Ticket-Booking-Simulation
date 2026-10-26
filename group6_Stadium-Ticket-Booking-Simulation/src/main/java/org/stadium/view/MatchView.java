package org.stadium.view;

import org.stadium.controller.MatchController;
import org.stadium.model.Match;
import org.stadium.model.MatchFilter;
import org.stadium.model.enums.MatchStatus;

import java.util.List;
import java.util.Scanner;

public class MatchView {

    private Scanner scanner;
    private MatchController matchController;

    public MatchView(Scanner scanner, MatchController matchController) {
        this.scanner = scanner;
        this.matchController = matchController;
    }

    public MatchView() {
        this.scanner = new Scanner(System.in);
        this.matchController = new MatchController();
    }

    public void displayMatches(List<Match> matches) {
        System.out.println();
        System.out.println("======================================== DANH SÁCH TRẬN ĐẤU ========================================");
        if (matches == null || matches.isEmpty()) {
            System.out.println("Không có trận đấu nào trong danh sách.");
            System.out.println("=====================================================================================================");
            return;
        }

        System.out.printf("%-8s | %-40s | %-15s | %-15s | %-8s | %-19s | %-10s%n",
                "Mã trận", "Tên trận đấu", "Đội A", "Đội B", "Mã sân", "Thời gian bắt đầu", "Trạng thái");
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
        for (Match match : matches) {
            System.out.printf("%-8s | %-40s | %-15s | %-15s | %-8s | %-19s | %-10s%n",
                    match.getMatchId(),
                    match.getMatchTitle(),
                    match.getTeamA(),
                    match.getTeamB(),
                    match.getStadiumId(),
                    match.getStartTime(),
                    match.getStatus() != null ? match.getStatus().name() : "N/A");
        }
        System.out.println("==================================================================================================================================");
    }

    public void displayMatchDetails(Match match) {
        System.out.println();
        System.out.println("======================= CHI TIẾT TRẬN ĐẤU =======================");
        if (match == null) {
            System.out.println("Thông tin trận đấu không tồn tại.");
            System.out.println("=================================================================");
            return;
        }

        System.out.println("Mã trận đấu       : " + match.getMatchId());
        System.out.println("Tiêu đề trận đấu  : " + match.getMatchTitle());
        System.out.println("Đội A             : " + match.getTeamA());
        System.out.println("Đội B             : " + match.getTeamB());
        System.out.println("Mã ban tổ chức    : " + match.getOrganizerId());
        System.out.println("Mã sân vận động   : " + match.getStadiumId());
        System.out.println("Thời gian bắt đầu : " + match.getStartTime());
        System.out.println("Thời gian kết thúc: " + match.getEndTime());
        System.out.println("Trạng thái        : " + (match.getStatus() != null ? match.getStatus().name() : "N/A"));
        System.out.println("=================================================================");
    }

    public void displayCreateMatchForm() {
        System.out.println();
        System.out.println("======================== TẠO TRẬN ĐẤU MỚI ========================");
        System.out.print("Nhập tiêu đề trận đấu: ");
        String title = this.scanner.nextLine().trim();
        System.out.print("Nhập tên đội A: ");
        String teamA = this.scanner.nextLine().trim();
        System.out.print("Nhập tên đội B: ");
        String teamB = this.scanner.nextLine().trim();
        System.out.print("Nhập mã sân vận động (VD: STD01): ");
        String stadiumId = this.scanner.nextLine().trim();
        System.out.print("Nhập thời gian bắt đầu (yyyy-MM-dd HH:mm:ss): ");
        String start = this.scanner.nextLine().trim();
        System.out.print("Nhập thời gian kết thúc (yyyy-MM-dd HH:mm:ss): ");
        String end = this.scanner.nextLine().trim();
        System.out.print("Nhập mã Ban tổ chức (VD: ORG01): ");
        String orgId = this.scanner.nextLine().trim();

        if (title.isEmpty() || teamA.isEmpty() || teamB.isEmpty() || stadiumId.isEmpty()
                || start.isEmpty() || end.isEmpty() || orgId.isEmpty()) {
            System.out.println("[FAILED] Lỗi: Vui lòng nhập đầy đủ tất cả các trường thông tin!");
            return;
        }

        boolean success = this.matchController.createMatch(title, teamA, teamB, stadiumId, start, end, orgId);
        if (success) {
            System.out.println("[SUCCESS] Tạo trận đấu mới thành công!");
        } else {
            System.out.println("[FAILED] Tạo trận đấu thất bại. Vui lòng kiểm tra lại dữ liệu đầu vào.");
        }
    }

    public void displayUpdateMatchForm() {
        System.out.println();
        System.out.println("====================== CẬP NHẬT TRẬN ĐẤU ======================");
        System.out.print("Nhập mã trận đấu cần cập nhật (VD: MTH01): ");
        String matchId = this.scanner.nextLine().trim();
        if (matchId.isEmpty()) {
            System.out.println("[FAILED] Mã trận đấu không được để trống!");
            return;
        }

        System.out.print("Nhập tiêu đề mới: ");
        String title = this.scanner.nextLine().trim();
        System.out.print("Nhập thời gian bắt đầu mới (yyyy-MM-dd HH:mm:ss): ");
        String start = this.scanner.nextLine().trim();
        System.out.print("Nhập thời gian kết thúc mới (yyyy-MM-dd HH:mm:ss): ");
        String end = this.scanner.nextLine().trim();
        System.out.println("Chọn trạng thái trận đấu mới:");
        System.out.println("1. SCHEDULED");
        System.out.println("2. LIVE");
        System.out.println("3. FINISHED");
        System.out.println("4. CANCELLED");
        System.out.print("Nhập lựa chọn (1-4): ");
        String statusChoice = this.scanner.nextLine().trim();

        MatchStatus status = MatchStatus.SCHEDULED;
        if ("2".equals(statusChoice)) {
            status = MatchStatus.LIVE;
        } else if ("3".equals(statusChoice)) {
            status = MatchStatus.FINISHED;
        } else if ("4".equals(statusChoice)) {
            status = MatchStatus.CANCELLED;
        } else if (!"1".equals(statusChoice)) {
            try {
                status = MatchStatus.valueOf(statusChoice.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("[FAILED] Trạng thái không hợp lệ!");
                return;
            }
        }

        boolean success = this.matchController.updateMatch(matchId, title, start, end, status);
        if (success) {
            System.out.println("[SUCCESS] Cập nhật thông tin trận đấu " + matchId + " thành công!");
        } else {
            System.out.println("[FAILED] Cập nhật thất bại. Kiểm tra lại mã trận đấu.");
        }
    }

    public String promptSearchKeyword() {
        System.out.println();
        System.out.println("======================= TÌM KIẾM TRẬN ĐẤU =======================");
        System.out.print("Nhập từ khóa tìm kiếm (tiêu đề hoặc tên đội bóng): ");
        return this.scanner.nextLine().trim();
    }

    public MatchFilter promptFilterCriteria() {
        System.out.println();
        System.out.println("======================== LỌC TRẬN ĐẤU ========================");
        MatchFilter filter = new MatchFilter();

        System.out.print("Nhập từ khóa (Nhấn Enter để bỏ qua): ");
        String keyword = this.scanner.nextLine().trim();
        if (!keyword.isEmpty()) {
            filter.setKeyword(keyword);
        }

        System.out.print("Nhập mã sân vận động (VD: STD01, Enter để bỏ qua): ");
        String stadiumId = this.scanner.nextLine().trim();
        if (!stadiumId.isEmpty()) {
            filter.setStadiumId(stadiumId);
        }

        System.out.println("Chọn trạng thái cần lọc (Enter để bỏ qua):");
        System.out.println("1. SCHEDULED | 2. LIVE | 3. FINISHED | 4. CANCELLED");
        System.out.print("Lựa chọn: ");
        String statusInput = this.scanner.nextLine().trim();
        if ("1".equals(statusInput)) {
            filter.setStatus(MatchStatus.SCHEDULED);
        } else if ("2".equals(statusInput)) {
            filter.setStatus(MatchStatus.LIVE);
        } else if ("3".equals(statusInput)) {
            filter.setStatus(MatchStatus.FINISHED);
        } else if ("4".equals(statusInput)) {
            filter.setStatus(MatchStatus.CANCELLED);
        } else if (!statusInput.isEmpty()) {
            try {
                filter.setStatus(MatchStatus.valueOf(statusInput.toUpperCase()));
            } catch (IllegalArgumentException e) {

            }
        }

        return filter;
    }
}
