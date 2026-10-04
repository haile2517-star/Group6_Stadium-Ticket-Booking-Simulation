package org.stadium.repository;

import org.stadium.model.AccountStatus;
import org.stadium.model.Organizer;

import java.util.ArrayList;
import java.util.List;

public class OrganizerRepository extends CsvRepository<Organizer> {

    public OrganizerRepository(String filePath) {
        super(filePath);
    }

    public OrganizerRepository() {
        super("data/organizers.csv");
    }

    @Override
    protected Organizer createEntity() {
        return new Organizer();
    }

    @Override
    protected String getHeader() {
        return "organizerId,username,passwordHash,orgName,email,phone,status";
    }

    public Organizer findByUsername(String username) {
        if (username == null) {
            return null;
        }
        for (Organizer org : items) {
            if (username.equalsIgnoreCase(org.getUsername())) {
                return org;
            }
        }
        return null;
    }

    public List<Organizer> findPendingOrganizers() {
        List<Organizer> pendingList = new ArrayList<>();
        for (Organizer org : items) {
            if (org.getStatus() == AccountStatus.PENDING) {
                pendingList.add(org);
            }
        }
        return pendingList;
    }
}
