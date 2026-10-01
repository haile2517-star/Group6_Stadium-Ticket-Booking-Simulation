package org.stadium.model;

public class Organizer extends Account {

    private String orgName;

    public Organizer() {
    }

    @Override
    public String getId() {
        return getAccountId();
    }

    public String getOrgName() {
        return orgName;
    }

    @Override
    public String toCsvLine() {
        return String.join(",",
                getAccountId(),
                getUsername(),
                getPasswordHash(),
                orgName,
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
        this.orgName = parts[3].trim();
        setEmail(parts[4].trim());
        setPhone(parts[5].trim());
        setStatus(AccountStatus.valueOf(parts[6].trim()));
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String toString() {
        return "Organizer{" +
                "organizerId='" + getAccountId() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", orgName='" + orgName + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", status=" + getStatus() +
                '}';
    }
}
