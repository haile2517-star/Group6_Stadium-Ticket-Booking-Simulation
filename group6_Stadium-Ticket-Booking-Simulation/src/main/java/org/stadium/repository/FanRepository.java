package org.stadium.repository;

import org.stadium.model.AccountStatus;
import org.stadium.model.Fan;

import java.util.ArrayList;
import java.util.List;

public class FanRepository extends CsvRepository<Fan> {

    public FanRepository(String filePath) {
        super(filePath);
    }

    public FanRepository() {
        super("data/fans.csv");
    }

    @Override
    protected Fan createEntity() {
        return new Fan();
    }

    @Override
    protected String getHeader() {
        return "fanId,username,passwordHash,fullName,email,phone,fanType,status";
    }

    // Tìm Fan theo username (dùng cho login)
    public Fan findByUsername(String username) {
        if (username == null) {
            return null;
        }
        for (Fan fan : items) {
            if (username.equalsIgnoreCase(fan.getUsername())) {
                return fan;
            }
        }
        return null;
    }

    // Tìm danh sách Fan theo trạng thái (dùng cho Admin duyệt tài khoản)
    public List<Fan> findByStatus(AccountStatus status) {
        List<Fan> result = new ArrayList<>();
        for (Fan fan : items) {
            if (fan.getStatus() == status) {
                result.add(fan);
            }
        }
        return result;
    }
}
