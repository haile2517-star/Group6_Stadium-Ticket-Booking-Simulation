package org.stadium.controller;

import org.stadium.model.BookingTransaction;
import org.stadium.model.Seat;
import org.stadium.model.Ticket;
import org.stadium.model.enums.SynchronizationMechanism;
import org.stadium.model.enums.TicketStatus;
import org.stadium.model.enums.TransactionStatus;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.TicketRepository;
import org.stadium.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.stadium.model.enums.SeatStatus;

public class BookingController {
    private SeatRepository seatRepository;
    private TicketRepository ticketRepository;
    private TransactionRepository transactionRepository;
    
    public BookingController (SeatRepository seatRepository,TicketRepository ticketRepository, TransactionRepository transactionRepository){
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.transactionRepository = transactionRepository;
    }

    public boolean bookTickets(String fanId, String matchId, List<String> seatIds, SynchronizationMechanism mech){
        if(mech != SynchronizationMechanism.NO_LOCK){
            throw new UnsupportedOperationException("No Support "+ mech);
        } //ktra dau vao + toi da 4 ve / 1 lan mua
        if(fanId == null || fanId.isEmpty() || matchId == null || matchId.isEmpty() || seatIds == null || seatIds.isEmpty() || seatIds.size() > 4 || new HashSet<>(seatIds).size() != seatIds.size() ){
            return false;
        }

        // ktra ve con trong
        List<Seat> seats = new ArrayList<>();
        List<Ticket> tickets = new ArrayList<>();
        for(String seatId : seatIds){
            Seat seat = seatRepository.findById(seatId);
            if (seat == null || !seat.isAvailable()) {
                return false;
                
            }Ticket ticket = ticketRepository.findBySeatAndMatch(seatId, matchId);
            if (ticket == null || !ticket.isAvailable()) {
                return false;
            }
            seats.add(seat);
            tickets.add(ticket);

        }
        //tao giao dich
        String transactionId = String.format("TXN%05d", transactionRepository.findAll().size()+1);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        double totalAmount = 0;

        //update ghe + ve
        for (int i = 0; i < seats.size(); i++){
            Seat seat = seats.get(i);
            Ticket ticket = tickets.get(i);
            seat.setStatus(SeatStatus.BOOKED);
            seat.incrementVersion();
            ticket.setFanId(fanId);
            ticket.setTransactionId(transactionId);
            ticket.setBookingDate(now);
            ticket.setStatus(TicketStatus.BOOKED);
            totalAmount += ticket.getPrice();
        }
        seatRepository.saveToFile();
        ticketRepository.saveToFile();
        transactionRepository.save(new BookingTransaction(transactionId, fanId, matchId,now,totalAmount,seats.size(), TransactionStatus.SUCCESS));
        return true;


    }
    public List<BookingTransaction> viewBookingHistory(String fanId){
        return transactionRepository.findByFanId(fanId);
    }




}
