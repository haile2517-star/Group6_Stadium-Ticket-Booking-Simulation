package org.stadium.model;

public class TicketPricing extends BaseEntity {
    private String pricingId;
    private String matchId;
    private String sectionId;
    private double basePrice;

    public TicketPricing(){

    }
    public TicketPricing(String pricingId, String matchId, String sectionId, double basePrice){
        this.pricingId = pricingId;
        this.matchId = matchId;
        this.sectionId = sectionId;
        this.basePrice = basePrice;
    }

    @Override
    public String getId(){
        return this.pricingId;

    } 
    public void updatePrice(double newPrice){
        this.basePrice = newPrice;
    }
    @Override 
    public String toCsvLine(){
        return pricingId + "," + matchId + "," + sectionId + "," + basePrice;
    }
@Override 
    public void fromCsvLine(String line){
          String[] parts = line.split(",", -1);
        this.pricingId = parts[0];
        this.matchId = parts[1];
        this.sectionId = parts[2];
        this.basePrice = (parts[3] == null ||  parts[3].trim().isEmpty()) ? 0.0 : Double.parseDouble(parts[3].trim());

    }
public String getPricingId() {
    return pricingId;
}
public void setPricingId(String pricingId) {
    this.pricingId = pricingId;
}
public String getMatchId() {
    return matchId;
}
public void setMatchId(String matchId) {
    this.matchId = matchId;
}
public String getSectionId() {
    return sectionId;
}
public void setSectionId(String sectionId) {
    this.sectionId = sectionId;
}
public double getBasePrice() {
    return basePrice;
}
public void setBasePrice(double basePrice) {
    this.basePrice = basePrice;
}

}