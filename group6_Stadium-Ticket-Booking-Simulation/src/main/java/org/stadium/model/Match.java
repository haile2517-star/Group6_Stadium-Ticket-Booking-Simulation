package org.stadium.model;

import org.stadium.model.enums.MatchStatus;

// Thực thể biểu diễn thông tin trận đấu trong hệ thống
public class Match extends BaseEntity {
    private String matchId;
    private String organizerId;
    private String stadiumId;
    private String matchTitle;
    private String teamA;
    private String teamB;
    private String startTime;
    private String endTime;
    private MatchStatus status;

    public Match() {
    }

    // Trả về mã định danh duy nhất của trận đấu (khóa chính)
    @Override
    public String getId() {
        return this.matchId;
    }

    // Cập nhật thông tin chi tiết trận đấu (tiêu đề, thời gian bắt đầu và kết thúc)
    public void updateMatchDetails(String title, String start, String end) {
        this.matchTitle = title;
        this.startTime = start;
        this.endTime = end;
    }

    // Chuyển đổi đối tượng Match thành dòng định dạng CSV để ghi vào file matches.csv
    @Override
    public String toCsvLine() {
        return this.matchId + "," + this.organizerId + "," + this.stadiumId + ","
                + this.matchTitle + "," + this.teamA + "," + this.teamB + ","
                + this.startTime + "," + this.endTime + "," + this.status.name();
    }

    // Parse một dòng chuỗi từ file CSV và gán giá trị cho từng thuộc tính
    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",", -1); // split -1 để không bỏ sót các trường rỗng ở cuối dòng
        this.matchId = parts[0];
        this.organizerId = parts[1];
        this.stadiumId = parts[2];
        this.matchTitle = parts[3];
        this.teamA = parts[4];
        this.teamB = parts[5];
        this.startTime = parts[6];
        this.endTime = parts[7];
        this.status = MatchStatus.valueOf(parts[8]); // valueOf ép kiểu chuỗi sang Enum MatchStatus
    }

    public String getMatchId() {
        return this.matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getOrganizerId() {
        return this.organizerId;
    }

    public void setOrganizerId(String organizerId) {
        this.organizerId = organizerId;
    }

    public String getStadiumId() {
        return this.stadiumId;
    }

    public void setStadiumId(String stadiumId) {
        this.stadiumId = stadiumId;
    }

    public String getMatchTitle() {
        return this.matchTitle;
    }

    public void setMatchTitle(String matchTitle) {
        this.matchTitle = matchTitle;
    }

    public String getTeamA() {
        return this.teamA;
    }

    public void setTeamA(String teamA) {
        this.teamA = teamA;
    }

    public String getTeamB() {
        return this.teamB;
    }

    public void setTeamB(String teamB) {
        this.teamB = teamB;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public MatchStatus getStatus() {
        return this.status;
    }

    public void setStatus(MatchStatus status) {
        this.status = status;
    }
}
