package org.stadium.repository;

import org.stadium.model.Ticket;

import java.util.List;

public class TicketRepository extends CsvRepository<Ticket> {
    public TicketRepository(String filePath) {
        super(filePath);
    }

    @Override
    public Ticket createEntity() {

        return new Ticket();

    }
    @Override 
    public String getHeader(){
        return "ticketId,matchId,seatId,transactionId,fanId,price,bookingDate,status";
    }
    public List<Ticket> findByMatchId(String matchId){
        return findByCondition(t -> t.getFanId().equals(matchId));

    }
     public List<Ticket> findByFanId(String fanId){
        return findByCondition(t -> t.getFanId().equals(fanId));

    }
    public Ticket findBySeatAndMatch(String seatId,String matchId ){
        return findByCondition(t -> t.getSeatId().equals(seatId) && t.getMatchId().equals(matchId)).stream().findFirst().orElse(null);
    }

}