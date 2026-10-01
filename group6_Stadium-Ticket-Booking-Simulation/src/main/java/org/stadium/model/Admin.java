package org.stadium.model;

public class Admin extends Account {
    private String fullName;

    public Admin() {
    }

    @Override
    public String getId() {
        return getAccountId();
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toCsvLine() {
        return String.join(",",
                getAccountId(),
                getUsername(),
                getPasswordHash(),
                fullName,
                getEmail(),
                getPhone(),
                getStatus().name());
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        setAccountId(parts[0].trim());
        setUsername(parts[1].trim());
        setPasswordHash(parts[2].trim());
        this.fullName = parts[3].trim();
        setEmail(parts[4].trim());
        setPhone(parts[5].trim());
        setStatus(AccountStatus.valueOf(parts[6].trim()));
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId='" + getAccountId() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
