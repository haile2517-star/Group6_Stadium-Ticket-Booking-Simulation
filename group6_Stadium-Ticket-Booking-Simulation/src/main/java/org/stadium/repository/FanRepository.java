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

    public Fan findByUsername(String username) {
        if (username == null) {
            return null;
        }
        List<Fan> result = this.findByCondition(fan -> username.equalsIgnoreCase(fan.getUsername()));
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Fan> findByStatus(AccountStatus status) {
        return this.findByCondition(fan -> fan.getStatus() == status);
    }
}
