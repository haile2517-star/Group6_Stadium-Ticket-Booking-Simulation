package org.stadium.repository;

import org.stadium.model.Match;
import org.stadium.model.enums.MatchStatus;
import java.util.List;

public class MatchRepository extends CsvRepository<Match> {
    // Khởi tạo MatchRepository với đường dẫn file CSV và gọi loadFromFile() từ lớp cha
    public MatchRepository(String filePath) {
        super(filePath);
    }

    // Factory method tạo thể hiện Match rỗng để nạp dữ liệu từ từng dòng CSV
    @Override
    public Match createEntity() {
        return new Match();
    }

    // Trả về dòng header chuẩn chứa thứ tự các cột của file matches.csv
    @Override
    public String getHeader() {
        return "matchId,organizerId,stadiumId,matchTitle,teamA,teamB,startTime,endTime,status";
    }

    // Lọc danh sách các trận đấu được tổ chức tại một sân vận động theo stadiumId
    public List<Match> findByStadiumId(String stadiumId) {
        return this.findByCondition(m -> m.getStadiumId().equals(stadiumId)); // Dùng Predicate Lambda so sánh stadiumId
    }

    // Lọc danh sách các trận đấu theo trạng thái (SCHEDULED, LIVE, FINISHED, CANCELLED)
    public List<Match> findByStatus(MatchStatus status) {
        return this.findByCondition(m -> m.getStatus() == status); // So sánh enum MatchStatus
    }

    // Lấy danh sách các trận đấu sắp diễn ra trong tương lai (SCHEDULED)
    public List<Match> findUpcomingMatches() {
        return this.findByCondition(m -> m.getStatus() == MatchStatus.SCHEDULED); // Lọc các trận có trạng thái SCHEDULED
    }
}
