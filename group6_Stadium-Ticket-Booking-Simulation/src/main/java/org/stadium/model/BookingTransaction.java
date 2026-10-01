package org.stadium.model;

import org.stadium.model.enums.TransactionStatus;

public class BookingTransaction extends BaseEntity {

    private String transactionId;
    private String fanId;
    private String matchId;
    private String transactionDate;
    private double totalAmount;
    private int ticketCount;
    private TransactionStatus status;

    public BookingTransaction() {

    }

    public BookingTransaction(String transactionId, String fanId, String matchId, String transactionDate,
            double totalAmount, int ticketCount, TransactionStatus status) {
        this.transactionId = transactionId;
        this.fanId = fanId;
        this.matchId = matchId;
        this.transactionDate = transactionDate;
        this.totalAmount = totalAmount;
        this.ticketCount = ticketCount;
        this.status = status;

    }
    @Override 
    public String getId(){
        return this.transactionId;
    }
    public void markAsSuccess(){
        this.status = TransactionStatus.SUCCESS;
    }
    public void markAsFailed(){
        this.status = TransactionStatus.FAILED;
    }
    public void markAsCancelled(){
        this.status = TransactionStatus.CANCELLED;
    }
    @Override 
    public String toCsvLine(){
        return transactionId + "," + fanId + "," + matchId + "," + totalAmount + "," + ticketCount + "," + (transactionDate != null ? transactionDate : "") + "," + (status != null ? status.name() : "");
    }
    @Override
    public void fromCsvLine(String line) {
        String[] parts = line.split(",", -1);
        this.transactionId = parts[0].trim();
        this.fanId = parts[1].trim();
        this.matchId = parts[2].trim();
        this.totalAmount = (parts[3] == null || parts[3].trim().isEmpty()) ? 0.0 : Double.parseDouble(parts[3].trim());
        this.ticketCount = (parts[4] == null || parts[4].trim().isEmpty()) ? 0 : Integer.parseInt(parts[4].trim());
        this.transactionDate = parts[5].trim();
        this.status = (parts[6] == null || parts[6].trim().isEmpty()) ? null : TransactionStatus.valueOf(parts[6].trim());
    }


}