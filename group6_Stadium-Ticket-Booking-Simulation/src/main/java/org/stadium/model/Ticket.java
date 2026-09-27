package org.stadium.model;

import org.stadium.model.enums.TicketStatus;


public class Ticket extends BaseEntity {

    private String ticketId;
    private String matchId;
    private String seatId;
    private String TransactionId;
    private String fanId;
    private double price;
    private String bookingDate;
    private TicketStatus status;

    public Ticket() {
    }

    public Ticket(String ticketId, String matchId, String seatId, String TransactionId,String fanId, double price, String bookingDate, TicketStatus status ){
        this.ticketId = ticketId;
        this.matchId = matchId;
        this.seatId = seatId;
        this.TransactionId = TransactionId;
        this.fanId = fanId;
        this.price = price;
        this.bookingDate = bookingDate;
        this.status = status;





    }
    @Override
    public String getId(){
        return this.ticketId;
    }
    public boolean isAvailable(){
        return this.status == TicketStatus.AVAILABLE;

    }
    @Override 
    public String toCsvLine() {
        return ticketId + "," + matchId + "," + seatId + "," + (TransactionId != null ? TransactionId:"") + "," +  (fanId != null ? fanId : "") + "," + price + "," + (bookingDate != null ? bookingDate : "") + "," + (status != null ? status : "");
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        this.ticketId = parts[0];
        this.matchId = parts[1];
        this.seatId = parts[2];
        this.TransactionId = parts[3];
        this.fanId = parts[4];
        this.price = (parts[5].isEmpty() ? 0.0 : Double.parseDouble(parts[5]));
        this.bookingDate = parts[6];
        this.status = (parts[7].isEmpty() ? null : TicketStatus.valueOf(parts[7]));
    }



}