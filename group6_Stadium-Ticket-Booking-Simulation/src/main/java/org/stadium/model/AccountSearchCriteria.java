package org.stadium.model;

import java.util.Locale;

// Lớp đóng gói tiêu chí tìm kiếm và lọc tài khoản người dùng cho Quản trị viên
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

    // Kiểm tra xem đối tượng Account có khớp với tiêu chí tìm kiếm không
    public boolean matchesCriteria(Account account) {
        if (account == null) {
            return false;
        }
        // 1. Kiểm tra trạng thái tài khoản (ACTIVE, PENDING, REJECTED, BLOCKED)
        if (this.status != null && account.getStatus() != this.status) {
            return false;
        }
        // 2. Kiểm tra vai trò tài khoản (FAN, ORGANIZER, ADMIN) dùng instanceof
        if (this.role != null) {
            boolean roleMatches;

            if (this.role == UserRole.FAN) {
                roleMatches = account instanceof Fan; // instanceof kiểm tra lớp Fan
            } else if (this.role == UserRole.ORGANIZER) {
                roleMatches = account instanceof Organizer; // instanceof kiểm tra lớp Organizer
            } else if (this.role == UserRole.ADMIN) {
                roleMatches = account instanceof Admin; // instanceof kiểm tra lớp Admin
            } else {
                roleMatches = false;
            }

            if (!roleMatches) {
                return false;
            }
        }

        // Nếu không có từ khóa tìm kiếm thì mặc định khớp
        if (this.keyword == null || this.keyword.trim().isEmpty()) {
            return true;
        }
        String searchTerm = this.keyword.trim().toLowerCase(Locale.ROOT);
        String accountId = account.getId();
        String username = account.getUsername();

        // 3. So khớp từ khóa với accountId hoặc username
        boolean keywordMatches = (accountId != null
                && accountId.toLowerCase(Locale.ROOT).contains(searchTerm))
                || (username != null
                        && username.toLowerCase(Locale.ROOT).contains(searchTerm));

        // So khớp từ khóa với tên hiển thị theo từng loại tài khoản cụ thể
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