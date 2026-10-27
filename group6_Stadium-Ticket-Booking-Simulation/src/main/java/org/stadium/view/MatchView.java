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

    // Khởi tạo MatchView với Scanner và MatchController được truyền vào (Dependency Injection)
    public MatchView(Scanner scanner, MatchController matchController) {
        this.scanner = scanner;
        this.matchController = matchController;
    }

    // Constructor mặc định tự khởi tạo Scanner và MatchController
    public MatchView() {
        this.scanner = new Scanner(System.in);
        this.matchController = new MatchController();
    }

    // Hiển thị danh sách các trận đấu dạng bảng có định dạng cột
    public void displayMatches(List<Match> matches) {
        String border = "+---------------------------------------------------------------------------+";
        String gridBorder = "+-------+----------------------------+------------------+-------+-----------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                                MATCH LIST                                 |");
        System.out.println(border);
        if (matches == null || matches.isEmpty()) {
            System.out.println("| [INFO] No matches found.                                                  |");
            System.out.println(border);
            return;
        }

        System.out.println(gridBorder);
        System.out.println("| ID    | Match / Event              | Date & Time      | Std   | Status    |");
        System.out.println(gridBorder);

        for (Match match : matches) {
            String id = this.padOrTruncate(match.getMatchId(), 5);
            String title = match.getMatchTitle();
            if (title == null || title.isEmpty()) {
                title = match.getTeamA() + " vs " + match.getTeamB();
            } else if (title.contains(":")) {
                String[] parts = title.split(":", 2);
                if (parts.length > 1 && !parts[1].trim().isEmpty()) {
                    title = parts[1].trim();
                }
            }
            if (title != null && title.length() > 26 && title.contains("Han Quoc")) {
                title = title.replace("Han Quoc", "Korea");
            }
            String matchStr = this.padOrTruncate(title, 26);
            String rawDate = match.getStartTime();
            String dateStr = (rawDate != null && rawDate.length() >= 16) ? rawDate.substring(0, 16) : rawDate;
            String timeStr = this.padOrTruncate(dateStr, 16);
            String std = this.padOrTruncate(match.getStadiumId(), 5);
            String status = this.padOrTruncate(match.getStatus() != null ? match.getStatus().name() : "N/A", 9);

            System.out.printf("| %s | %s | %s | %s | %s |%n", id, matchStr, timeStr, std, status);
        }
        System.out.println(gridBorder);
        System.out.printf("| Total: %-66s |%n", matches.size() + " matches listed");
        System.out.println(border);
    }

    // Hiển thị thông tin chi tiết của một trận đấu
    public void displayMatchDetails(Match match) {
        System.out.println();
        String border = "+-------------------------------------------------------------------------+";
        if (match == null) {
            System.out.println(border);
            System.out.println("| [INFO] Match details not found.                                         |");
            System.out.println(border);
            return;
        }

        System.out.println(border);
        System.out.println("|                              MATCH DETAILS                              |");
        System.out.println(border);
        System.out.printf("| Match ID     : %-56s |%n", this.padOrTruncate(match.getMatchId(), 56));
        System.out.printf("| Match Title  : %-56s |%n", this.padOrTruncate(match.getMatchTitle(), 56));
        System.out.printf("| Teams        : %-56s |%n", this.padOrTruncate(match.getTeamA() + " vs " + match.getTeamB(), 56));
        System.out.printf("| Stadium ID   : %-56s |%n", this.padOrTruncate(match.getStadiumId(), 56));
        System.out.printf("| Start Time   : %-56s |%n", this.padOrTruncate(match.getStartTime(), 56));
        System.out.printf("| End Time     : %-56s |%n", this.padOrTruncate(match.getEndTime(), 56));
        System.out.printf("| Organizer ID : %-56s |%n", this.padOrTruncate(match.getOrganizerId(), 56));
        System.out.printf("| Status       : %-56s |%n", this.padOrTruncate(match.getStatus() != null ? match.getStatus().name() : "N/A", 56));
        System.out.println(border);
    }

    // Hiển thị form nhập thông tin để tạo trận đấu mới
    public void displayCreateMatchForm() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                            CREATE NEW MATCH                             |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter match title: ");
        String title = this.scanner.nextLine().trim();
        System.out.print("Enter Team A name: ");
        String teamA = this.scanner.nextLine().trim();
        System.out.print("Enter Team B name: ");
        String teamB = this.scanner.nextLine().trim();
        System.out.print("Enter stadium ID (e.g., STD01): ");
        String stadiumId = this.scanner.nextLine().trim();
        System.out.print("Enter start time (yyyy-MM-dd HH:mm:ss): ");
        String start = this.scanner.nextLine().trim();
        System.out.print("Enter end time (yyyy-MM-dd HH:mm:ss): ");
        String end = this.scanner.nextLine().trim();
        System.out.print("Enter Organizer ID (e.g., ORG01): ");
        String orgId = this.scanner.nextLine().trim();

        // Kiểm tra hợp lệ dữ liệu đầu vào không được để trống
        if (title.isEmpty() || teamA.isEmpty() || teamB.isEmpty() || stadiumId.isEmpty()
                || start.isEmpty() || end.isEmpty() || orgId.isEmpty()) {
            System.out.println("[FAILED] Error: All fields are required!");
            return;
        }

        // Gọi Controller thực hiện tạo và lưu trận đấu mới
        boolean success = this.matchController.createMatch(title, teamA, teamB, stadiumId, start, end, orgId);
        if (success) {
            System.out.println("[SUCCESS] New match created successfully!");
        } else {
            System.out.println("[FAILED] Failed to create match. Please verify input data.");
        }
    }

    // Hiển thị form cập nhật thông tin và trạng thái trận đấu
    public void displayUpdateMatchForm() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                              UPDATE MATCH                               |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter match ID to update (e.g., MTH01): ");
        String matchId = this.scanner.nextLine().trim();
        if (matchId.isEmpty()) {
            System.out.println("[FAILED] Match ID cannot be empty!");
            return;
        }

        System.out.print("Enter new title: ");
        String title = this.scanner.nextLine().trim();
        System.out.print("Enter new start time (yyyy-MM-dd HH:mm:ss): ");
        String start = this.scanner.nextLine().trim();
        System.out.print("Enter new end time (yyyy-MM-dd HH:mm:ss): ");
        String end = this.scanner.nextLine().trim();
        System.out.println("Select new match status:");
        System.out.println("1. SCHEDULED");
        System.out.println("2. LIVE");
        System.out.println("3. FINISHED");
        System.out.println("4. CANCELLED");
        System.out.print("Enter choice (1-4): ");
        String statusChoice = this.scanner.nextLine().trim();

        // Xử lý chuyển đổi lựa chọn người dùng thành Enum MatchStatus
        MatchStatus status = MatchStatus.SCHEDULED;
        if ("2".equals(statusChoice)) {
            status = MatchStatus.LIVE;
        } else if ("3".equals(statusChoice)) {
            status = MatchStatus.FINISHED;
        } else if ("4".equals(statusChoice)) {
            status = MatchStatus.CANCELLED;
        } else if (!"1".equals(statusChoice)) {
            try {
                status = MatchStatus.valueOf(statusChoice.toUpperCase()); // Ép kiểu chuỗi sang Enum an toàn
            } catch (IllegalArgumentException e) {
                System.out.println("[FAILED] Invalid status!");
                return;
            }
        }

        // Gọi Controller cập nhật thông tin trận đấu vào matches.csv
        boolean success = this.matchController.updateMatch(matchId, title, start, end, status);
        if (success) {
            System.out.println("[SUCCESS] Match " + matchId + " updated successfully!");
        } else {
            System.out.println("[FAILED] Update failed. Check match ID.");
        }
    }

    // Nhận từ khóa tìm kiếm trận đấu từ console
    public String promptSearchKeyword() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                             SEARCH MATCHES                              |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter search keyword (title or team name): ");
        return this.scanner.nextLine().trim();
    }

    // Nhận các tiêu chí lọc đa điều kiện và đóng gói vào đối tượng MatchFilter
    public MatchFilter promptFilterCriteria() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                             FILTER MATCHES                              |");
        System.out.println("+-------------------------------------------------------------------------+");
        MatchFilter filter = new MatchFilter(); // Tạo đối tượng chứa các tiêu chí lọc

        System.out.print("Enter keyword (Press Enter to skip): ");
        String keyword = this.scanner.nextLine().trim();
        if (!keyword.isEmpty()) {
            filter.setKeyword(keyword);
        }

        System.out.print("Enter stadium ID (e.g., STD01, Press Enter to skip): ");
        String stadiumId = this.scanner.nextLine().trim();
        if (!stadiumId.isEmpty()) {
            filter.setStadiumId(stadiumId);
        }

        System.out.println("Select match status (Press Enter to skip):");
        System.out.println("1. SCHEDULED | 2. LIVE | 3. FINISHED | 4. CANCELLED");
        System.out.print("Choice: ");
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
                filter.setStatus(MatchStatus.valueOf(statusInput.toUpperCase())); // valueOf parse Enum
            } catch (IllegalArgumentException e) {

            }
        }

        return filter;
    }

    private String padOrTruncate(String text, int width) {
        if (text == null) {
            text = "N/A";
        }
        text = text.trim();
        if (text.length() > width) {
            if (width <= 3) {
                return text.substring(0, width);
            }
            return text.substring(0, width - 3) + "...";
        }
        return String.format("%-" + width + "s", text);
    }
}
