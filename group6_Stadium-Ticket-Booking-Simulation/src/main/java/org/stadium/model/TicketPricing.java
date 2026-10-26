package org.stadium.model;

public class TicketPricing extends BaseEntity {
    private String pricingId;
    private String matchId;
    private String sectionId;
    private double price;

    public TicketPricing() {
    }

    public TicketPricing(String pricingId, String matchId, String sectionId, double price) {
        this.pricingId = pricingId;
        this.matchId = matchId;
        this.sectionId = sectionId;
        this.price = price;
    }

    @Override
    public String getId() {
        return this.pricingId;
    }

    public double getPrice() {
        return this.price;
    }

    @Override
    public String toCsvLine() {
        return this.pricingId + "," + this.matchId + "," + this.sectionId + "," + this.price;
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        this.pricingId = parts[0].trim();
        this.matchId = parts[1].trim();
        this.sectionId = parts[2].trim();
        this.price = (parts[3] == null || parts[3].trim().isEmpty()) ? 0.0 : Double.parseDouble(parts[3].trim());
    }

    public String getPricingId() {
        return this.pricingId;
    }

    public void setPricingId(String pricingId) {
        this.pricingId = pricingId;
    }

    public String getMatchId() {
        return this.matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getSectionId() {
        return this.sectionId;
    }

    public void setSectionId(String sectionId) {
        this.sectionId = sectionId;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
