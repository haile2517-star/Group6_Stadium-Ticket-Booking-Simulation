package org.stadium.model;

public class Fan extends Account {
    private String fullName;
    private String fanType;

    public Fan() {
    }

    @Override
    public String getId() {
        return getAccountId();
    }

    public String getFullName() {
        return fullName;
    }

    public String getFanType() {
        return fanType;
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
                fanType,
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
        this.fanType = parts[6].trim();
        setStatus(AccountStatus.valueOf(parts[7].trim()));
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
                "fanId='" + getAccountId() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", fullName='" + fullName + '\'' +
                ", fanType='" + fanType + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
