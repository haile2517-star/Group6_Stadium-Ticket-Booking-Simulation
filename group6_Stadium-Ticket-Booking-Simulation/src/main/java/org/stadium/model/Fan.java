package org.stadium.model;

public class Fan extends Account {
    private String fullName;
    private String fanType;

    public Fan() {
    }

    @Override
    public String getId() {
        return this.getAccountId();
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getFanType() {
        return this.fanType;
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
                this.fanType,
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
        this.fanType = parts[6].trim();
        this.setStatus(AccountStatus.valueOf(parts[7].trim()));
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setFanType(String fanType) {
        this.fanType = fanType;
    }

    @Override
    public String toString() {
        return "Fan{" +
                "fanId='" + this.getAccountId() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                ", fullName='" + this.fullName + '\'' +
                ", fanType='" + this.fanType + '\'' +
                ", email='" + this.getEmail() + '\'' +
                ", phone='" + this.getPhone() + '\'' +
                ", status=" + this.getStatus() +
                '}';
    }
}
