package org.stadium.repository;
import org.stadium.model.TicketPricing;

public class TicketPricingRepository extends CsvRepository<TicketPricing> {
    public TicketPricingRepository(String filePath){
        super(filePath);
    }
    @Override 
    public TicketPricing createEntity(){
        return new TicketPricing();
    }
    @Override 
    public String getHeader(){
        return"pricingId,matchId,sectionId,price";
    }
    public TicketPricing findByMatchAndSection(String matchId, String sectionId){
        return findByCondition(p -> p.getMatchId().equals(matchId) && p.getSectionId().equals(sectionId))
        .stream()
        .findFirst()
        .orElse(null);
    }

}
