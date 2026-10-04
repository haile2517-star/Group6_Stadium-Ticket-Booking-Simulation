package org.stadium.repository;

import org.stadium.model.Admin;

public class AdminRepository extends CsvRepository<Admin> {

    public AdminRepository(String filePath) {
        super(filePath);
    }

    public AdminRepository() {
        super("data/admins.csv");
    }

    @Override
    protected Admin createEntity() {
        return new Admin();
    }

    @Override
    protected String getHeader() {
        return "adminId,username,passwordHash,fullName,email,phone,status";
    }

    public Admin findByUsername(String username) {
        if (username == null) {
            return null;
        }
        for (Admin admin : items) {
            if (username.equalsIgnoreCase(admin.getUsername())) {
                return admin;
            }
        }
        return null;
    }
}
