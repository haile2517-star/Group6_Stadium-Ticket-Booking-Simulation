package org.stadium.model;

public class Admin extends Account {
    private String fullName;

    public Admin() {
    }

    @Override
    public String getId() {
        return this.getAccountId();
    }

    public String getFullName() {
        return this.fullName;
    }

    @Override
    public String toCsvLine() {
        return String.join(",",
                this.getAccountId(),
                this.getUsername(),
                this.getPasswordHash(),
                this.fullName,
                this.getEmail(),
                this.getPhone(),
                this.getStatus().name());
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        this.setAccountId(parts[0].trim());
        this.setUsername(parts[1].trim());
        this.setPasswordHash(parts[2].trim());
        this.fullName = parts[3].trim();
        this.setEmail(parts[4].trim());
        this.setPhone(parts[5].trim());
        this.setStatus(AccountStatus.valueOf(parts[6].trim()));
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + this.getAccountId() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                ", fullName='" + this.fullName + '\'' +
                ", email='" + this.getEmail() + '\'' +
                ", phone='" + this.getPhone() + '\'' +
                ", status=" + this.getStatus() +
                '}';
    }
}
