package org.stadium.repository;

import org.stadium.model.Stadium;

import java.util.List;

public class StadiumRepository extends CsvRepository<Stadium> {

    public StadiumRepository(String filePath) {
        super(filePath);
    }

    @Override
    public Stadium createEntity() {
        return new Stadium();
    }

    @Override
    public String getHeader() {
        return "stadiumId,stadiumName,address,capacity";
    }

    public Stadium findByName(String stadiumName) {
        if (stadiumName == null) {
            return null;
        }
        List<Stadium> result = findByCondition(s -> stadiumName.equalsIgnoreCase(s.getStadiumName()));
        return result.isEmpty() ? null : result.get(0);
    }
}
