package org.stadium.repository;

import org.stadium.model.SeatSection;

import java.util.List;

public class SectionRepository extends CsvRepository<SeatSection> {

    public SectionRepository(String filePath) {
        super(filePath);
    }

    @Override
    public SeatSection createEntity() {
        return new SeatSection();
    }

    @Override
    public String getHeader() {
        return "sectionId,stadiumId,sectionName,sectionType,totalRows,seatsPerRow";
    }

    public List<SeatSection> findByStadiumId(String stadiumId) {
        return this.findByCondition(s -> s.getStadiumId().equals(stadiumId));
    }
}
