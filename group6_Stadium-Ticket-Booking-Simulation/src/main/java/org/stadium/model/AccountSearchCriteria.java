package org.stadium.model;

import java.util.Locale;
import org.stadium.model.enums.AccountStatus;
import org.stadium.model.enums.UserRole;

public class AccountSearchCriteria {
    private String keyword;
    private UserRole role;
    private AccountStatus status;

    public boolean matchesCriteria(Account account) {
        if (account == null) {
            return false;
        }
        if (status != null && account.getStatus() != status) {
            return false;
        }
        if (role != null) {
            boolean roleMatches;
            
            if (role == UserRole.FAN) {
                roleMatches = account instanceof Fan;
            } else if (role == UserRole.ORGANIZER) {
                roleMatches = account instanceof Organizer;
            } else if (role == UserRole.ADMIN) {
                roleMatches = account instanceof Admin;
            } else {
                roleMatches = false;
            }

            if (!roleMatches) {
                return false;
            }
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            return true;
        }
        String searchTerm = keyword.trim().toLowerCase(Locale.ROOT);
        String accountId = account.getId();
        String username = account.getUsername();

        boolean keywordMatches = 
                (accountId != null
                    && accountId.toLowerCase(Locale.ROOT).contains(searchTerm)) 
                || (username != null
                        && username.toLowerCase(Locale.ROOT).contains(searchTerm));
                
        if (account instanceof Fan) {
            String fullname = ((Fan) account).getFullName();
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
}