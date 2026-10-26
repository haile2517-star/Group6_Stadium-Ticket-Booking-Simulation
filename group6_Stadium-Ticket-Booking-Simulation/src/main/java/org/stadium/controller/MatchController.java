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

  public MatchController(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
  }

  public MatchController() {
    String pathMatches = new File("data/matches.csv").exists() ? "data/matches.csv"
        : (new File("../data/matches.csv").exists() ? "../data/matches.csv" : "data/matches.csv");
    this.matchRepository = new MatchRepository(pathMatches);
  }

  public List<Match> viewAllMatches() {
    if (this.matchRepository == null) {
      return Collections.emptyList();
    }
    return this.matchRepository.findAll();
  }

  public List<Match> searchMatches(String keyword) {
    if (this.matchRepository == null) {
      return Collections.emptyList();
    }
    if (keyword == null || keyword.trim().isEmpty()) {
      return this.matchRepository.findAll();
    }
    String kw = keyword.trim().toLowerCase();
    return this.matchRepository
        .findByCondition(m -> (m.getMatchTitle() != null && m.getMatchTitle().toLowerCase().contains(kw)) ||
            (m.getTeamA() != null && m.getTeamA().toLowerCase().contains(kw)) ||
            (m.getTeamB() != null && m.getTeamB().toLowerCase().contains(kw)));
  }

  public List<Match> filterMatches(MatchFilter filter) {
    if (this.matchRepository == null) {
      return Collections.emptyList();
    }
    if (filter == null) {
      return this.matchRepository.findAll();
    }
    return this.matchRepository.findByCondition(m -> filter.matchesCriteria(m));
  }

  public boolean createMatch(String title, String teamA, String teamB, String stadiumId, String start, String end,
      String orgId) {
    if (this.matchRepository == null) {
      return false;
    }
    if (title == null || title.trim().isEmpty() ||
        teamA == null || teamA.trim().isEmpty() ||
        teamB == null || teamB.trim().isEmpty() ||
        stadiumId == null || stadiumId.trim().isEmpty() ||
        start == null || start.trim().isEmpty() ||
        end == null || end.trim().isEmpty() ||
        orgId == null || orgId.trim().isEmpty()) {
      return false;
    }

    int nextIdNum = this.matchRepository.findAll().size() + 1;
    String matchId = String.format("MTH%02d", nextIdNum);
    while (this.matchRepository.findById(matchId) != null) {
      nextIdNum++;
      matchId = String.format("MTH%02d", nextIdNum);
    }

    Match newMatch = new Match();
    newMatch.setMatchId(matchId);
    newMatch.setOrganizerId(orgId.trim());
    newMatch.setStadiumId(stadiumId.trim());
    newMatch.setMatchTitle(title.trim());
    newMatch.setTeamA(teamA.trim());
    newMatch.setTeamB(teamB.trim());
    newMatch.setStartTime(start.trim());
    newMatch.setEndTime(end.trim());
    newMatch.setStatus(MatchStatus.SCHEDULED);

    return this.matchRepository.save(newMatch);
  }

  public boolean updateMatch(String matchId, String title, String start, String end, MatchStatus status) {
    if (this.matchRepository == null || matchId == null || matchId.trim().isEmpty()) {
      return false;
    }
    Match match = this.matchRepository.findById(matchId.trim());
    if (match == null) {
      return false;
    }

    if (title != null && !title.trim().isEmpty() &&
        start != null && !start.trim().isEmpty() &&
        end != null && !end.trim().isEmpty()) {
      match.updateMatchDetails(title.trim(), start.trim(), end.trim());
    }

    if (status != null) {
      match.setStatus(status);
    }

    this.matchRepository.saveToFile();
    return true;
  }

  public boolean deleteMatch(String matchId) {
    if (this.matchRepository == null || matchId == null || matchId.trim().isEmpty()) {
      return false;
    }
    return this.matchRepository.deleteById(matchId.trim());
  }
}
