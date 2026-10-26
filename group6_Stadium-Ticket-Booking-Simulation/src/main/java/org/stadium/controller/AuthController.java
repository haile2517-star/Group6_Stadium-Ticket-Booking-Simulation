package org.stadium.controller;

import org.stadium.model.Account;
import org.stadium.model.AccountStatus;
import org.stadium.model.Admin;
import org.stadium.model.Fan;
import org.stadium.model.Organizer;
import org.stadium.repository.AdminRepository;
import org.stadium.repository.FanRepository;
import org.stadium.repository.OrganizerRepository;

import java.io.File;
import java.util.List;


public class AuthController {

    private final FanRepository fanRepo;
    private final OrganizerRepository organizerRepo;
    private final AdminRepository adminRepo;
    private Account currentUser;

    public AuthController(FanRepository fanRepo, OrganizerRepository organizerRepo, AdminRepository adminRepo) {
        this.fanRepo = fanRepo;
        this.organizerRepo = organizerRepo;
        this.adminRepo = adminRepo;
    }

    public AuthController() {
        String pathFans = new File("data/fans.csv").exists() ? "data/fans.csv" : (new File("../data/fans.csv").exists() ? "../data/fans.csv" : "data/fans.csv");
        String pathOrgs = new File("data/organizers.csv").exists() ? "data/organizers.csv" : (new File("../data/organizers.csv").exists() ? "../data/organizers.csv" : "data/organizers.csv");
        String pathAdmins = new File("data/admins.csv").exists() ? "data/admins.csv" : (new File("../data/admins.csv").exists() ? "../data/admins.csv" : "data/admins.csv");
        this.fanRepo = new FanRepository(pathFans);
        this.organizerRepo = new OrganizerRepository(pathOrgs);
        this.adminRepo = new AdminRepository(pathAdmins);
    }

    public String resolvePath(String fileName) {
        if (new File("data/" + fileName).exists()) {
            return "data/" + fileName;
        } else if (new File("../data/" + fileName).exists()) {
            return "../data/" + fileName;
        }
        return "data/" + fileName;
    }

    public Account login(String username, String pass) {
        if (username == null || username.trim().isEmpty() || pass == null || pass.isEmpty()) {
            return null;
        }

        String trimmedUser = username.trim();


        Admin admin = this.adminRepo.findByUsername(trimmedUser);
        if (admin != null) {
            if (admin.getStatus() == AccountStatus.ACTIVE && admin.validatePassword(pass)) {
                this.currentUser = admin;
                return admin;
            }
            return null;
        }


        Fan fan = this.fanRepo.findByUsername(trimmedUser);
        if (fan != null) {
            if (fan.getStatus() == AccountStatus.ACTIVE && fan.validatePassword(pass)) {
                this.currentUser = fan;
                return fan;
            }
            return null;
        }


        Organizer organizer = this.organizerRepo.findByUsername(trimmedUser);
        if (organizer != null) {
            if (organizer.getStatus() == AccountStatus.ACTIVE && organizer.validatePassword(pass)) {
                this.currentUser = organizer;
                return organizer;
            }
            return null;
        }

        return null;
    }

    public void logout(String accountId) {
        if (this.currentUser != null) {
            if (accountId == null || accountId.trim().isEmpty() || this.currentUser.getId().equalsIgnoreCase(accountId.trim())) {
                this.currentUser = null;
            }
        }
    }

    public boolean registerFan(String username, String pass, String name, String email, String phone) {
        if (username == null || username.trim().isEmpty()
                || pass == null || pass.trim().isEmpty()
                || name == null || name.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()) {
            return false;
        }

        String trimmedUsername = username.trim();

        if (this.isUsernameExists(trimmedUsername)) {
            return false;
        }

        String newFanId = this.generateNextFanId();

        Fan newFan = new Fan();
        newFan.setAccountId(newFanId);
        newFan.setUsername(trimmedUsername);
        newFan.setPasswordHash("hash_pass_" + pass);
        newFan.setFullName(name.trim());
        newFan.setEmail(email.trim());
        newFan.setPhone(phone.trim());
        newFan.setFanType("STANDARD");
        newFan.setStatus(AccountStatus.ACTIVE);

        return this.fanRepo.save(newFan);
    }

    public boolean registerOrganizer(String username, String pass, String orgName, String email, String phone) {
        if (username == null || username.trim().isEmpty()
                || pass == null || pass.trim().isEmpty()
                || orgName == null || orgName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()) {
            return false;
        }

        String trimmedUsername = username.trim();

        if (this.isUsernameExists(trimmedUsername)) {
            return false;
        }

        String newOrgId = this.generateNextOrganizerId();

        Organizer newOrg = new Organizer();
        newOrg.setAccountId(newOrgId);
        newOrg.setUsername(trimmedUsername);
        newOrg.setPasswordHash("hash_pass_" + pass);
        newOrg.setOrgName(orgName.trim());
        newOrg.setEmail(email.trim());
        newOrg.setPhone(phone.trim());
        newOrg.setStatus(AccountStatus.PENDING);

        return this.organizerRepo.save(newOrg);
    }

    public Account viewProfile(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            return null;
        }
        String id = accountId.trim();

        Account acc = this.fanRepo.findById(id);
        if (acc != null) return acc;

        acc = this.organizerRepo.findById(id);
        if (acc != null) return acc;

        return this.adminRepo.findById(id);
    }

    public boolean updateProfile(String accountId, String email, String phone) {
        if (accountId == null || accountId.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()) {
            return false;
        }
        String id = accountId.trim();

        Fan fan = this.fanRepo.findById(id);
        if (fan != null) {
            fan.updateContactInfo(email.trim(), phone.trim());
            this.fanRepo.saveToFile();
            return true;
        }

        Organizer org = this.organizerRepo.findById(id);
        if (org != null) {
            org.updateContactInfo(email.trim(), phone.trim());
            this.organizerRepo.saveToFile();
            return true;
        }

        Admin admin = this.adminRepo.findById(id);
        if (admin != null) {
            admin.updateContactInfo(email.trim(), phone.trim());
            this.adminRepo.saveToFile();
            return true;
        }

        return false;
    }

    public boolean changePassword(String accountId, String oldPass, String newPass) {
        if (accountId == null || accountId.trim().isEmpty()
                || oldPass == null || oldPass.isEmpty()
                || newPass == null || newPass.trim().isEmpty()) {
            return false;
        }
        String id = accountId.trim();

        Account acc = this.viewProfile(id);
        if (acc == null) {
            return false;
        }

        if (!acc.validatePassword(oldPass)) {
            return false;
        }

        acc.changePassword("hash_pass_" + newPass);

        if (acc instanceof Fan) {
            this.fanRepo.saveToFile();
        } else if (acc instanceof Organizer) {
            this.organizerRepo.saveToFile();
        } else if (acc instanceof Admin) {
            this.adminRepo.saveToFile();
        }

        return true;
    }

    public boolean isUsernameExists(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String u = username.trim();
        return this.fanRepo.findByUsername(u) != null
                || this.organizerRepo.findByUsername(u) != null
                || this.adminRepo.findByUsername(u) != null;
    }

    public String getLoginFailureReason(String username) {
        if (username == null || username.trim().isEmpty()) {
            return "Tên đăng nhập không được để trống.";
        }
        String trimmed = username.trim();
        Account acc = this.adminRepo.findByUsername(trimmed);
        if (acc == null) {
            acc = this.fanRepo.findByUsername(trimmed);
        }
        if (acc == null) {
            acc = this.organizerRepo.findByUsername(trimmed);
        }

        if (acc == null) {
            return "Tên đăng nhập không tồn tại trong hệ thống.";
        } else if (acc.getStatus() == AccountStatus.PENDING) {
            return "Tài khoản đang ở trạng thái [PENDING - Chờ duyệt]. Vui lòng liên hệ Admin!";
        } else if (acc.getStatus() == AccountStatus.BLOCKED) {
            return "Tài khoản đã bị [BLOCKED - Khóa]. Vui lòng liên hệ hỗ trợ!";
        } else if (acc.getStatus() == AccountStatus.REJECTED) {
            return "Tài khoản đã bị [REJECTED - Từ chối phê duyệt].";
        } else {
            return "Sai mật khẩu! Vui lòng kiểm tra lại mật khẩu đã nhập.";
        }
    }

  
    private synchronized String generateNextFanId() {
        int maxId = 0;
        for (Fan fan : this.fanRepo.findAll()) {
            String id = fan.getId();
            if (id != null && id.toUpperCase().startsWith("FAN")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) {
                        maxId = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("FAN%04d", maxId + 1);
    }


    private synchronized String generateNextOrganizerId() {
        int maxId = 0;
        for (Organizer org : this.organizerRepo.findAll()) {
            String id = org.getId();
            if (id != null && id.toUpperCase().startsWith("ORG")) {
                try {
                    int num = Integer.parseInt(id.substring(3));
                    if (num > maxId) {
                        maxId = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("ORG%02d", maxId + 1);
    }

    public Account getCurrentUser() {
        return this.currentUser;
    }

    public void setCurrentUser(Account currentUser) {
        this.currentUser = currentUser;
    }

    public List<Organizer> getPendingOrganizers() {
        return this.organizerRepo.findPendingOrganizers();
    }

    public boolean approveOrganizer(String organizerId) {
        if (organizerId == null || organizerId.trim().isEmpty()) {
            return false;
        }
        Organizer org = this.organizerRepo.findById(organizerId.trim());
        if (org == null) {
            return false;
        }
        org.setStatus(AccountStatus.ACTIVE);
        this.organizerRepo.save(org);
        this.organizerRepo.saveToFile();
        return true;
    }

    public boolean rejectOrganizer(String organizerId) {
        if (organizerId == null || organizerId.trim().isEmpty()) {
            return false;
        }
        Organizer org = this.organizerRepo.findById(organizerId.trim());
        if (org == null) {
            return false;
        }
        org.setStatus(AccountStatus.REJECTED);
        this.organizerRepo.save(org);
        this.organizerRepo.saveToFile();
        return true;
    }

    public FanRepository getFanRepo() {
        return this.fanRepo;
    }

    public OrganizerRepository getOrganizerRepo() {
        return this.organizerRepo;
    }

    public AdminRepository getAdminRepo() {
        return this.adminRepo;
    }
}
