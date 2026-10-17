package org.stadium.repository;

import org.stadium.model.AccountStatus;
import org.stadium.model.Organizer;

import java.util.List;

public class OrganizerRepository extends CsvRepository<Organizer> {

    public OrganizerRepository(String filePath) {
        super(filePath);
    }

    @Override
    public Organizer createEntity() {
        return new Organizer();
    }

    @Override
    public String getHeader() {
        return "organizerId,username,passwordHash,orgName,email,phone,status";
    }

    public Organizer findByUsername(String username) {
        if (username == null) {
            return null;
        }
        List<Organizer> result = this.findByCondition(org -> username.equalsIgnoreCase(org.getUsername()));
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Organizer> findPendingOrganizers() {
        return this.findByCondition(org -> org.getStatus() == AccountStatus.PENDING);
    }
}
