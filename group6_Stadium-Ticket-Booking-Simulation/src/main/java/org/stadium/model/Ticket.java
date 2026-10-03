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

    public Ticket(String ticketId, String matchId, String seatId, String TransactionId, String fanId, double price,
            String bookingDate, TicketStatus status) {
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
    public String getId() {
        return this.ticketId;
    }

    public boolean isAvailable() {
        return this.status == TicketStatus.AVAILABLE;

    }

    @Override
    public String toCsvLine() {
        return ticketId + "," + matchId + "," + seatId + "," + (TransactionId != null ? TransactionId : "") + ","
                + (fanId != null ? fanId : "") + "," + price + "," + (bookingDate != null ? bookingDate : "") + ","
                + (status != null ? status.name() : "");
    }

    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        this.ticketId = parts[0].trim();
        this.matchId = parts[1].trim();
        this.seatId = parts[2].trim();
        this.TransactionId = parts[3].trim();
        this.fanId = parts[4].trim();
        this.price = (parts[5] == null || parts[5].trim().isEmpty() ? 0.0 : Double.parseDouble(parts[5].trim()));
        this.bookingDate = parts[6].trim();
        this.status = (parts[7] == null || parts[7].trim().isEmpty() ? null : TicketStatus.valueOf(parts[7].trim()));
    }


    public String getTicketId(){
        return this.ticketId;
    }
     

     public void setTicketId(String ticketId) {
         this.ticketId = ticketId;
     }

     public String getMatchId() {
         return matchId;
     }

     public void setMatchId(String matchId) {
         this.matchId = matchId;
     }

     public String getSeatId() {
         return seatId;
     }

     public void setSeatId(String seatId) {
         this.seatId = seatId;
     }

     public String getTransactionId() {
         return TransactionId;
     }

     public void setTransactionId(String transactionId) {
         TransactionId = transactionId;
     }

     public String getFanId() {
         return fanId;
     }

     public void setFanId(String fanId) {
         this.fanId = fanId;
     }

     public double getPrice() {
         return price;
     }

     public void setPrice(double price) {
         this.price = price;
     }

     public String getBookingDate() {
         return bookingDate;
     }

     public void setBookingDate(String bookingDate) {
         this.bookingDate = bookingDate;
     }

     public TicketStatus getStatus() {
         return status;
     }

     public void setStatus(TicketStatus status) {
         this.status = status;
     }




}