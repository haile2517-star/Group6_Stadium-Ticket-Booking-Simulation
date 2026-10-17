package org.stadium.model;

import org.stadium.model.enums.MatchStatus;

public class MatchFilter {

    private String keyword;
    private String dateFrom;
    private String dateTo;
    private String stadiumId;
    private MatchStatus status;

    public MatchFilter() {
    }

    public boolean matchesCriteria(Match match) {
        if (this.keyword != null && !this.keyword.isEmpty()) {
            boolean keywordMatch = match.getMatchTitle().toLowerCase().contains(this.keyword.toLowerCase()) ||
                    match.getTeamA().toLowerCase().contains(this.keyword.toLowerCase()) ||
                    match.getTeamB().toLowerCase().contains(this.keyword.toLowerCase());
            if (!keywordMatch) {
                return false;
            }
        }

        if (this.stadiumId != null && !this.stadiumId.isEmpty()) {
            if (!match.getStadiumId().equals(this.stadiumId)) {
                return false;
            }
        }

        if (this.status != null) {
            if (match.getStatus() != this.status) {
                return false;
            }
        }
        return true;
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
