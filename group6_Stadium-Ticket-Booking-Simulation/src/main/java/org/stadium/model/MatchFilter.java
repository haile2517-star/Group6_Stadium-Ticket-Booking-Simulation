package org.stadium.model;

import org.stadium.model.enums.MatchStatus;

public class MatchFilter {

    private String keyword;
    private String dateFrom;
    private String dateTo;
    private String stadiumId;
    private MatchStatus status;

    public MatchFilter() {}

    public boolean matchesCriteria(Match match) {
        if (keyword != null && !keyword.isEmpty()) {
            boolean keywordMatch = match.getMatchTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    match.getTeamA().toLowerCase().contains(keyword.toLowerCase()) ||
                    match.getTeamB().toLowerCase().contains(keyword.toLowerCase());
            if (!keywordMatch) {
                return false; 
            }
        } // Đã thêm dấu } này để đóng khối lệnh if keyword

        if (stadiumId != null && !stadiumId.isEmpty()) {
            if (!match.getStadiumId().equals(stadiumId)) {
                return false;
            }
        }
        
        if (status != null) {
            if (match.getStatus() != status) {
                return false;
            }
        }
        return true;
    }

    // Toàn bộ Getter/Setter đã nằm gọn gàng bên trong class
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
    
    public String getDateFrom() { return dateFrom; }
    public void setDateFrom(String dateFrom) { this.dateFrom = dateFrom; }
    
    public String getDateTo() { return dateTo; }
    public void setDateTo(String dateTo) { this.dateTo = dateTo; }
    
    public String getStadiumId() { return stadiumId; }
    public void setStadiumId(String stadiumId) { this.stadiumId = stadiumId; }
    
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }

}
