package org.stadium.model;

public class Organizer extends Account {

    private String orgName;

    public Organizer() {
    }

    @Override
    public String getId() {
        return this.getAccountId();
    }

    public String getOrgName() {
        return this.orgName;
    }

    @Override
    public String toCsvLine() {
        return String.join(",",
                this.getAccountId(),
                this.getUsername(),
                this.getPasswordHash(),
                this.orgName,
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
        this.orgName = parts[3].trim();
        this.setEmail(parts[4].trim());
        this.setPhone(parts[5].trim());
        this.setStatus(AccountStatus.valueOf(parts[6].trim()));
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    @Override
    public String toString() {
        return "Organizer{" +
                "organizerId='" + this.getAccountId() + '\'' +
                ", username='" + this.getUsername() + '\'' +
                ", orgName='" + this.orgName + '\'' +
                ", email='" + this.getEmail() + '\'' +
                ", phone='" + this.getPhone() + '\'' +
                ", status=" + this.getStatus() +
                '}';
    }
}
