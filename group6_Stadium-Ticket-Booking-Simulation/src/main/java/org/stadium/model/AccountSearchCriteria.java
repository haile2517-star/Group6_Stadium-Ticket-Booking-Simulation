package org.stadium.model;

import java.util.Locale;

public class AccountSearchCriteria {
    private String keyword;
    private UserRole role;
    private AccountStatus status;

    public AccountSearchCriteria() {
    }

    public AccountSearchCriteria(String keyword, UserRole role, AccountStatus status) {
        this.keyword = keyword;
        this.role = role;
        this.status = status;
    }

    public boolean matchesCriteria(Account account) {
        if (account == null) {
            return false;
        }
        if (this.status != null && account.getStatus() != this.status) {
            return false;
        }
        if (this.role != null) {
            boolean roleMatches;

            if (this.role == UserRole.FAN) {
                roleMatches = account instanceof Fan;
            } else if (this.role == UserRole.ORGANIZER) {
                roleMatches = account instanceof Organizer;
            } else if (this.role == UserRole.ADMIN) {
                roleMatches = account instanceof Admin;
            } else {
                roleMatches = false;
            }

            if (!roleMatches) {
                return false;
            }
        }

        if (this.keyword == null || this.keyword.trim().isEmpty()) {
            return true;
        }
        String searchTerm = this.keyword.trim().toLowerCase(Locale.ROOT);
        String accountId = account.getId();
        String username = account.getUsername();

        boolean keywordMatches = (accountId != null
                && accountId.toLowerCase(Locale.ROOT).contains(searchTerm))
                || (username != null
                        && username.toLowerCase(Locale.ROOT).contains(searchTerm));

        if (account instanceof Fan) {
            String fullName = ((Fan) account).getFullName();
            keywordMatches = keywordMatches
                    || (fullName != null
                            && fullName.toLowerCase(Locale.ROOT).contains(searchTerm));
        } else if (account instanceof Organizer) {
            String orgName = ((Organizer) account).getOrgName();
            keywordMatches = keywordMatches
                    || (orgName != null
                            && orgName.toLowerCase(Locale.ROOT).contains(searchTerm));
        } else if (account instanceof Admin) {
            String fullName = ((Admin) account).getFullName();
            keywordMatches = keywordMatches
                    || (fullName != null
                            && fullName.toLowerCase(Locale.ROOT).contains(searchTerm));
        }
        return keywordMatches;
    }

    public String getKeyword() {
        return this.keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public UserRole getRole() {
        return this.role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public AccountStatus getStatus() {
        return this.status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
