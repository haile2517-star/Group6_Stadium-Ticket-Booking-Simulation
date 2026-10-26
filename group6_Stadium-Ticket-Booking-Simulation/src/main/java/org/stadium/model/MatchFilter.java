package org.stadium.model;

import org.stadium.model.enums.MatchStatus;

// Lớp đóng gói các tiêu chí lọc dữ liệu trận đấu đa điều kiện
public class MatchFilter {

    private String keyword;
    private String dateFrom;
    private String dateTo;
    private String stadiumId;
    private MatchStatus status;

    public MatchFilter() {
    }

    // Kiểm tra xem đối tượng Match truyền vào có thỏa mãn tất cả tiêu chí lọc hay không
    public boolean matchesCriteria(Match match) {
        // 1. Kiểm tra tiêu chí từ khóa (tìm trong tiêu đề, tên đội A hoặc đội B)
        if (this.keyword != null && !this.keyword.isEmpty()) {
            boolean keywordMatch = match.getMatchTitle().toLowerCase().contains(this.keyword.toLowerCase()) ||
                    match.getTeamA().toLowerCase().contains(this.keyword.toLowerCase()) ||
                    match.getTeamB().toLowerCase().contains(this.keyword.toLowerCase()); // Không phân biệt hoa thường
            if (!keywordMatch) {
                return false;
            }
        }

        // 2. Kiểm tra tiêu chí mã sân vận động (stadiumId)
        if (this.stadiumId != null && !this.stadiumId.isEmpty()) {
            if (!match.getStadiumId().equals(this.stadiumId)) {
                return false;
            }
        }

        // 3. Kiểm tra tiêu chí trạng thái trận đấu (SCHEDULED, LIVE, FINISHED, CANCELLED)
        if (this.status != null) {
            if (match.getStatus() != this.status) {
                return false;
            }
        }
        return true; // Thỏa mãn tất cả các điều kiện lọc
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getDateFrom() {
        return this.dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return this.dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getStadiumId() {
        return this.stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public MatchStatus getStatus() {
        return this.status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }
}
