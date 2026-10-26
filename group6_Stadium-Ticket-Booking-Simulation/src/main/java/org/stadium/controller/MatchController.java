package org.stadium.controller;

import org.stadium.model.Match;
import org.stadium.model.MatchFilter;
import org.stadium.model.enums.MatchStatus;
import org.stadium.repository.MatchRepository;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class MatchController {

  private MatchRepository matchRepository;

  // Khởi tạo MatchController tiêm MatchRepository vào (Dependency Injection)
  public MatchController(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
  }

  // Constructor mặc định tự xác định đường dẫn và tạo MatchRepository
  public MatchController() {
    String pathMatches = new File("data/matches.csv").exists() ? "data/matches.csv"
        : (new File("../data/matches.csv").exists() ? "../data/matches.csv" : "data/matches.csv");
    this.matchRepository = new MatchRepository(pathMatches);
  }

  // Lấy toàn bộ danh sách trận đấu từ Repository
  public List<Match> viewAllMatches() {
    if (this.matchRepository == null) {
      return Collections.emptyList();
    }
    return this.matchRepository.findAll(); // findAll() đọc toàn bộ items từ CsvRepository
  }

  // Tìm kiếm trận đấu theo từ khóa trong tiêu đề, đội A hoặc đội B
  public List<Match> searchMatches(String keyword) {
    if (this.matchRepository == null) {
      return Collections.emptyList();
    }
    // Nếu từ khóa rỗng thì trả về toàn bộ danh sách
    if (keyword == null || keyword.trim().isEmpty()) {
      return this.matchRepository.findAll();
    }
    String kw = keyword.trim().toLowerCase();
    // Lọc theo điều kiện chứa từ khóa (không phân biệt hoa thường)
    return this.matchRepository
        .findByCondition(m -> (m.getMatchTitle() != null && m.getMatchTitle().toLowerCase().contains(kw)) ||
            (m.getTeamA() != null && m.getTeamA().toLowerCase().contains(kw)) ||
            (m.getTeamB() != null && m.getTeamB().toLowerCase().contains(kw))); // Dùng Predicate Lambda để lọc
  }

  // Lọc trận đấu dựa trên nhiều tiêu chí được đóng gói trong đối tượng MatchFilter
  public List<Match> filterMatches(MatchFilter filter) {
    if (this.matchRepository == null) {
      return Collections.emptyList();
    }
    if (filter == null) {
      return this.matchRepository.findAll();
    }
    // Ủy quyền kiểm tra khớp tiêu chí cho hàm filter.matchesCriteria()
    return this.matchRepository.findByCondition(m -> filter.matchesCriteria(m)); // Gọi Predicate qua matchesCriteria
  }

  // Tạo trận đấu mới với mã tự sinh MTHxx và lưu vào repository
  public boolean createMatch(String title, String teamA, String teamB, String stadiumId, String start, String end,
      String orgId) {
    if (this.matchRepository == null) {
      return false;
    }
    // Kiểm tra tính toàn vẹn của dữ liệu đầu vào
    if (title == null || title.trim().isEmpty() ||
        teamA == null || teamA.trim().isEmpty() ||
        teamB == null || teamB.trim().isEmpty() ||
        stadiumId == null || stadiumId.trim().isEmpty() ||
        start == null || start.trim().isEmpty() ||
        end == null || end.trim().isEmpty() ||
        orgId == null || orgId.trim().isEmpty()) {
      return false;
    }

    // Tự động sinh mã trận đấu kế tiếp MTH01, MTH02,... tránh trùng lặp
    int nextIdNum = this.matchRepository.findAll().size() + 1;
    String matchId = String.format("MTH%02d", nextIdNum); // Format mã với 2 chữ số
    while (this.matchRepository.findById(matchId) != null) {
      nextIdNum++;
      matchId = String.format("MTH%02d", nextIdNum);
    }

    // Khởi tạo đối tượng Match mới và gán giá trị
    Match newMatch = new Match();
    newMatch.setMatchId(matchId);
    newMatch.setOrganizerId(orgId.trim());
    newMatch.setStadiumId(stadiumId.trim());
    newMatch.setMatchTitle(title.trim());
    newMatch.setTeamA(teamA.trim());
    newMatch.setTeamB(teamB.trim());
    newMatch.setStartTime(start.trim());
    newMatch.setEndTime(end.trim());
    newMatch.setStatus(MatchStatus.SCHEDULED); // Trạng thái mặc định khi tạo mới là SCHEDULED

    return this.matchRepository.save(newMatch); // Lưu vào RAM và tự động ghi ra matches.csv
  }

  // Cập nhật thông tin chi tiết và trạng thái trận đấu
  public boolean updateMatch(String matchId, String title, String start, String end, MatchStatus status) {
    if (this.matchRepository == null || matchId == null || matchId.trim().isEmpty()) {
      return false;
    }
    Match match = this.matchRepository.findById(matchId.trim()); // Tìm trận đấu theo mã
    if (match == null) {
      return false;
    }

    // Cập nhật tiêu đề và thời gian nếu có truyền vào
    if (title != null && !title.trim().isEmpty() &&
        start != null && !start.trim().isEmpty() &&
        end != null && !end.trim().isEmpty()) {
      match.updateMatchDetails(title.trim(), start.trim(), end.trim());
    }

    // Cập nhật trạng thái trận đấu (SCHEDULED, LIVE, FINISHED, CANCELLED)
    if (status != null) {
      match.setStatus(status);
    }

    this.matchRepository.saveToFile(); // Ghi đè toàn bộ dữ liệu đã cập nhật vào file CSV
    return true;
  }

  // Xóa trận đấu theo mã ID
  public boolean deleteMatch(String matchId) {
    if (this.matchRepository == null || matchId == null || matchId.trim().isEmpty()) {
      return false;
    }
    return this.matchRepository.deleteById(matchId.trim()); // Xóa khỏi danh sách và cập nhật file CSV
  }
}
