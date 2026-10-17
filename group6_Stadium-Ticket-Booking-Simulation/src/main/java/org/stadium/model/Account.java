package org.stadium.model;

public abstract class Account extends BaseEntity {

    private String accountId;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private AccountStatus status;

    @Override
    public String getId() {
        return this.accountId;
    }

    public String getUsername() {
        return this.username;
    }

    public AccountStatus getStatus() {
        return this.status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public boolean validatePassword(String rawPass) {
        if (rawPass == null || this.passwordHash == null) {
            return false;
<<<<<<< HEAD
        return passwordHash.equals(rawPass)
                || passwordHash.equals("hash_pass_" + rawPass)
                || passwordHash.equals("hash_" + rawPass);
=======
        }
        return this.passwordHash.equals(rawPass) || this.passwordHash.equals("hash_pass_" + rawPass);
>>>>>>> 51e156ef998c7ebdc23ae92f7e19f70cc6d4d255
    }

    public void changePassword(String newHash) {
        this.passwordHash = newHash;
    }

    public void updateContactInfo(String email, String phone) {
        this.email = email;
        this.phone = phone;
    }

    public String getAccountId() {
        return this.accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return this.passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
