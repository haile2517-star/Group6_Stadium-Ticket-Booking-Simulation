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

import org.stadium.model.enums.SeatStatus;

public class BookingController {
    private SeatRepository seatRepository;
    private TicketRepository ticketRepository;
    private TransactionRepository transactionRepository;

    public BookingController(SeatRepository seatRepository, TicketRepository ticketRepository, TransactionRepository transactionRepository){
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.transactionRepository = transactionRepository;
    }

    public BookingController() {
        String pathSeats = this.resolvePath("seats.csv");
        String pathTickets = this.resolvePath("tickets.csv");
        String pathTransactions = this.resolvePath("transactions.csv");
        this.seatRepository = new SeatRepository(pathSeats);
        this.ticketRepository = new TicketRepository(pathTickets);
        this.transactionRepository = new TransactionRepository(pathTransactions);
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
                    if (ticket == null || !ticket.isAvailable()) {
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
            if (ticket == null || !ticket.isAvailable()) {
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
        ticket.setStatus(TicketStatus.AVAILABLE);
        ticket.setFanId("");
        ticket.setTransactionId("");
        ticket.setBookingDate("");
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

<<<<<<< HEAD
    public String generateNextTransactionId() {
=======
    /**
     * Tự động sinh ID giao dịch kế tiếp (TXN00001, TXN00002, ...) tránh trùng lặp.
     * Ủy quyền cho TransactionRepository.generateNextTransactionId() — đã được
     * bảo vệ thread-safe bằng synchronized trên instance monitor của repository.
     * <p>
     * Tuần 7 - Kiệt: Chuyển logic sinh ID về repository để có 1 nguồn duy nhất (single source of truth),
     * tránh race condition khi nhiều thread trong BookingController cùng gọi đồng thời.
     * </p>
     */
    private String generateNextTransactionId() {
>>>>>>> 1b0a3cb1b1da0b5859ecff4abbf20e243a6a6e92
        return this.transactionRepository.generateNextTransactionId();
    }
}
