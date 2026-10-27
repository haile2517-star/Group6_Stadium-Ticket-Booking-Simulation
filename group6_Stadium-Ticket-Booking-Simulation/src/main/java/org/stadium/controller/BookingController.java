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
import java.util.function.Supplier;

import org.stadium.model.TicketPricing;
import org.stadium.model.enums.SeatStatus;
import org.stadium.repository.TicketPricingRepository;

public class BookingController {
    private SeatRepository seatRepository;
    private TicketRepository ticketRepository;
    private TransactionRepository transactionRepository;
    private TicketPricingRepository ticketPricingRepository;

    public BookingController(SeatRepository seatRepository, TicketRepository ticketRepository, TransactionRepository transactionRepository){
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.transactionRepository = transactionRepository;
        this.ticketPricingRepository = new TicketPricingRepository(this.resolvePath("ticket_pricings.csv"));
    }

    public BookingController() {
        String pathSeats = this.resolvePath("seats.csv");
        String pathTickets = this.resolvePath("tickets.csv");
        String pathTransactions = this.resolvePath("transactions.csv");
        this.seatRepository = new SeatRepository(pathSeats);
        this.ticketRepository = new TicketRepository(pathTickets);
        this.transactionRepository = new TransactionRepository(pathTransactions);
        this.ticketPricingRepository = new TicketPricingRepository(this.resolvePath("ticket_pricings.csv"));
    }

    private String resolvePath(String fileName) {
        if (new java.io.File("data/" + fileName).exists()) {
            return "data/" + fileName;
        } else if (new java.io.File("../data/" + fileName).exists()) {
            return "../data/" + fileName;
        }
        return "data/" + fileName;
    }

    public boolean bookTickets(String fanId, String matchId, List<String> seatIds, SynchronizationMechanism mech){
        if(mech == null){
            return false;
        }

        if(fanId == null || fanId.isEmpty() || matchId == null || matchId.isEmpty() || seatIds == null || seatIds.isEmpty() || seatIds.size() > 4 || new HashSet<>(seatIds).size() != seatIds.size() ){
            return false;
        }
        if(mech == SynchronizationMechanism.OPTIMISTIC){
            int maxRetries = 5;
            List<Seat> claimedSeats = new ArrayList<>();
            boolean completed = false;
            try{
                List<Ticket> tickets = new ArrayList<>();
                for(String seatId : seatIds){
                    boolean claimed = false;
                    for(int attempt = 0; attempt < maxRetries && !claimed; attempt++){
                        Seat seat = this.seatRepository.findById(seatId);
                        if (seat == null){
                            break;
                        }
                        int expectedVersion = seat.getVersion();
                        if (!seat.isAvailable()){
                            break;
                        }
                        Seat booked = new Seat(seat.getSeatId(), seat.getSectionId(), seat.getRowNumber(), seat.getSeatNumber(), seat.getSeatType(), SeatStatus.BOOKED, expectedVersion);
                        if(this.seatRepository.updateWithOptimisticLock(booked, expectedVersion)){
                            claimed = true;
                            claimedSeats.add(booked);
                        }
                    }
                    if(!claimed){
                        return false;
                    }
                    Ticket ticket = this.ticketRepository.findBySeatAndMatch(seatId, matchId);
                    if (ticket != null && ticket.isAvailable()) {
                        // ticket is available
                    } else if (ticket != null && ticket.getStatus() == TicketStatus.CANCELLED) {
                        String nextTicketId = this.ticketRepository.generateNextTicketId();
                        Ticket newTicket = new Ticket(nextTicketId, matchId, seatId, "", "", ticket.getPrice(), "", TicketStatus.AVAILABLE);
                        this.ticketRepository.save(newTicket);
                        ticket = newTicket;
                    } else if (ticket == null) {
                        double price = 500000.0;
                        if (this.ticketPricingRepository != null) {
                            Seat currentSeat = this.seatRepository.findById(seatId);
                            if (currentSeat != null) {
                                TicketPricing tp = this.ticketPricingRepository.findByMatchAndSection(matchId, currentSeat.getSectionId());
                                if (tp != null && tp.getPrice() > 0) {
                                    price = tp.getPrice();
                                }
                            }
                        }
                        String nextTicketId = this.ticketRepository.generateNextTicketId();
                        Ticket newTicket = new Ticket(nextTicketId, matchId, seatId, "", "", price, "", TicketStatus.AVAILABLE);
                        this.ticketRepository.save(newTicket);
                        ticket = newTicket;
                    } else {
                        return false;
                    }
                    tickets.add(ticket);
                }

                synchronized(this.transactionRepository){
                    String transactionId = this.generateNextTransactionId();
                    String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    double totalAmount = 0;
                    for (Ticket ticket : tickets){
                        ticket.setFanId(fanId);
                        ticket.setTransactionId(transactionId);
                        ticket.setBookingDate(now);
                        ticket.setStatus(TicketStatus.BOOKED);
                        totalAmount += ticket.getPrice();

                    }
                    this.ticketRepository.saveToFile();
                    this.transactionRepository.save(new BookingTransaction(transactionId,fanId,matchId,now,totalAmount,tickets.size(), TransactionStatus.SUCCESS));

                }
                completed = true;
                return true;
            } finally{
                if(!completed){
                    for(Seat booked : claimedSeats){
                        int bookedVersion = booked.getVersion();
                        Seat released = new Seat(booked.getSeatId(), booked.getSectionId(), booked.getRowNumber(), booked.getSeatNumber(), booked.getSeatType(), SeatStatus.AVAILABLE,bookedVersion);
                        if (!this.seatRepository.updateWithOptimisticLock(released, bookedVersion)) {
                            throw new IllegalStateException("Could not release seat after optimistic booking failure: " + booked.getId());
                        }

                    }
                }
            }
        }

        Supplier<Boolean> criticalSection = () ->{

        List<Seat> seats = new ArrayList<>();
        List<Ticket> tickets = new ArrayList<>();
        for(String seatId : seatIds){
            Seat seat = this.seatRepository.findById(seatId);
            if (seat == null || !seat.isAvailable()) {
                return false;
            }
            Ticket ticket = this.ticketRepository.findBySeatAndMatch(seatId, matchId);
            if (ticket != null && ticket.isAvailable()) {
                // ticket is already available
            } else if (ticket != null && ticket.getStatus() == TicketStatus.CANCELLED) {
                String nextTicketId = this.ticketRepository.generateNextTicketId();
                Ticket newTicket = new Ticket(nextTicketId, matchId, seatId, "", "", ticket.getPrice(), "", TicketStatus.AVAILABLE);
                this.ticketRepository.save(newTicket);
                ticket = newTicket;
            } else if (ticket == null) {
                double price = 500000.0;
                if (this.ticketPricingRepository != null) {
                    TicketPricing tp = this.ticketPricingRepository.findByMatchAndSection(matchId, seat.getSectionId());
                    if (tp != null && tp.getPrice() > 0) {
                        price = tp.getPrice();
                    }
                }
                String nextTicketId = this.ticketRepository.generateNextTicketId();
                Ticket newTicket = new Ticket(nextTicketId, matchId, seatId, "", "", price, "", TicketStatus.AVAILABLE);
                this.ticketRepository.save(newTicket);
                ticket = newTicket;
            } else {
                return false;
            }
            seats.add(seat);
            tickets.add(ticket);
        }

        String transactionId = this.generateNextTransactionId();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        double totalAmount = 0;

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
        this.seatRepository.saveToFile();
        this.ticketRepository.saveToFile();
        this.transactionRepository.save(new BookingTransaction(transactionId, fanId, matchId, now, totalAmount, seats.size(), TransactionStatus.SUCCESS));
        return true;
    };
    if (mech == SynchronizationMechanism.SYNCHRONIZED){
        synchronized(this.seatRepository){
            return criticalSection.get();
        }
    }
    if(mech == SynchronizationMechanism.FILE_LOCK){
        this.seatRepository.acquireFileLock();
        try {
            return criticalSection.get();
        }finally{
            this.seatRepository.releaseFileLock();
        }
    }
    return criticalSection.get();
}

  public boolean cancelTicket(String ticketId, String fanId) {
        if (ticketId == null || ticketId.trim().isEmpty() || fanId == null || fanId.trim().isEmpty()) {
            return false;
        }

        Ticket ticket = this.ticketRepository.findById(ticketId.trim());
        if (ticket == null) {
            return false;
        }

        if (!fanId.trim().equals(ticket.getFanId())) {
            return false;
        }

        if (ticket.getStatus() != TicketStatus.BOOKED) {
            return false;
        }

        String originalTransactionId = ticket.getTransactionId();
        double refundedPrice = ticket.getPrice();
        ticket.setStatus(TicketStatus.CANCELLED);
        this.ticketRepository.saveToFile();

        BookingTransaction originalTransaction = this.transactionRepository.findById(originalTransactionId);
        if (originalTransaction != null) {
            int remainingTickets = Math.max(0, originalTransaction.getTicketCount() - 1);
            originalTransaction.setTicketCount(remainingTickets);
            originalTransaction.setTotalAmount(Math.max(0, originalTransaction.getTotalAmount() - refundedPrice));
            if (remainingTickets == 0) {
                originalTransaction.setStatus(TransactionStatus.CANCELLED);
            }
            this.transactionRepository.saveToFile();
        }

        Seat seat = this.seatRepository.findById(ticket.getSeatId());
        if (seat != null) {
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.incrementVersion();
            this.seatRepository.saveToFile();
        }

        return true;
    }

    public List<Ticket> getTicketDetails(String transactionId) {
        if (transactionId == null || transactionId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<Ticket> allTickets = this.ticketRepository.findAll();
        List<Ticket> result = new ArrayList<>();
        for (Ticket ticket : allTickets) {
            if (transactionId.trim().equals(ticket.getTransactionId())) {
                result.add(ticket);
            }
        }
        return result;
    }

    // Xem lịch sử đặt vé của Fan (truy vấn danh sách giao dịch từ TransactionRepository)
    public List<BookingTransaction> viewBookingHistory(String fanId) {
        return this.transactionRepository.findByFanId(fanId); // Lọc giao dịch theo fanId
    }

    public String generateNextTransactionId() {
        return this.transactionRepository.generateNextTransactionId();
    }
}
