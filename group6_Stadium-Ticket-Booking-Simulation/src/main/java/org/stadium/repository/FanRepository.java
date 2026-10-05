package org.stadium.repository;

import org.stadium.model.AccountStatus;
import org.stadium.model.Fan;

import java.util.List;

public class FanRepository extends CsvRepository<Fan> {

    public FanRepository(String filePath) {
        super(filePath);
    }

    @Override
    public Fan createEntity() {
        return new Fan();
    }

    @Override
    public String getHeader() {
        return "fanId,username,passwordHash,fullName,email,phone,fanType,status";
    }

    // Tìm Fan theo username (dùng cho login)
    public Fan findByUsername(String username) {
        if (username == null) {
            return null;
        }
        List<Fan> result = findByCondition(fan -> username.equalsIgnoreCase(fan.getUsername()));
        return result.isEmpty() ? null : result.get(0);
    }

    // Tìm danh sách Fan theo trạng thái (dùng cho Admin duyệt tài khoản)
    public List<Fan> findByStatus(AccountStatus status) {
        return findByCondition(fan -> fan.getStatus() == status);
    }
}
