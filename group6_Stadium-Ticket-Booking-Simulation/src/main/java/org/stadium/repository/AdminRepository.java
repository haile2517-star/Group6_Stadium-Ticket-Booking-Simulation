package org.stadium.repository;

import org.stadium.model.Admin;

import java.util.List;

public class AdminRepository extends CsvRepository<Admin> {

    public AdminRepository(String filePath) {
        super(filePath);
    }

    @Override
    public Admin createEntity() {
        return new Admin();
    }

    @Override
    public String getHeader() {
        return "adminId,username,passwordHash,fullName,email,phone,status";
    }

    public Admin findByUsername(String username) {
        if (username == null) {
            return null;
        }
        List<Admin> result = findByCondition(admin -> username.equalsIgnoreCase(admin.getUsername()));
        return result.isEmpty() ? null : result.get(0);
    }
}
