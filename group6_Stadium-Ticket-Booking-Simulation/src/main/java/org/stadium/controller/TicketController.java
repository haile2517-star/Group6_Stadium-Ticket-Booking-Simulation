package org.stadium.controller;
 
import org.stadium.model.BookingTransaction;
import org.stadium.model.Match;
import org.stadium.model.Seat;
import org.stadium.model.SeatSection;
import org.stadium.model.Ticket;
import org.stadium.model.TicketPricing;
import org.stadium.model.enums.SeatStatus;
import org.stadium.model.enums.TicketStatus;
import org.stadium.model.enums.TransactionStatus;
import org.stadium.repository.MatchRepository;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.SectionRepository;
import org.stadium.repository.TicketPricingRepository;
import org.stadium.repository.TicketRepository;
import org.stadium.repository.TransactionRepository;
 
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
 
public class TicketController {
 
    private MatchRepository matchRepository;
    private SectionRepository sectionRepository;
    private SeatRepository seatRepository;
    private TicketRepository ticketRepository;
    private TicketPricingRepository ticketPricingRepository;
    private TransactionRepository transactionRepository;
 
    public TicketController(MatchRepository matchRepository,
            SectionRepository sectionRepository,
            SeatRepository seatRepository,
            TicketRepository ticketRepository,
            TicketPricingRepository ticketPricingRepository,
            TransactionRepository transactionRepository) {
        this.matchRepository = matchRepository;
        this.sectionRepository = sectionRepository;
        this.seatRepository = seatRepository;
        this.ticketRepository = ticketRepository;
        this.ticketPricingRepository = ticketPricingRepository;
        this.transactionRepository = transactionRepository;
    }

    public TicketController() {
        String pathMatches = this.resolvePath("matches.csv");
        String pathSections = this.resolvePath("sections.csv");
        String pathSeats = this.resolvePath("seats.csv");
        String pathTickets = this.resolvePath("tickets.csv");
        String pathPricings = this.resolvePath("ticket_pricings.csv");
        String pathTransactions = this.resolvePath("transactions.csv");

        this.matchRepository = new MatchRepository(pathMatches);
        this.sectionRepository = new SectionRepository(pathSections);
        this.seatRepository = new SeatRepository(pathSeats);
        this.ticketRepository = new TicketRepository(pathTickets);
        this.ticketPricingRepository = new TicketPricingRepository(pathPricings);
        this.transactionRepository = new TransactionRepository(pathTransactions);
    }

    public String resolvePath(String fileName) {
        if (new java.io.File("data/" + fileName).exists()) {
            return "data/" + fileName;
        } else if (new java.io.File("../data/" + fileName).exists()) {
            return "../data/" + fileName;
        }
        return "data/" + fileName;
    }
 
    // Dat/sua gia ve cho 1 khu vuc trong 1 tran. Gia ap dung cho cac ve sinh SAU khi cau hinh.
    public boolean configureTicketPricing(String matchId, String sectionId, double price) {
        if (matchId == null || matchId.isEmpty() || sectionId == null || sectionId.isEmpty() || price <= 0) {
            return false;
        }
 
        Match match = this.matchRepository.findById(matchId);
        SeatSection section = this.sectionRepository.findById(sectionId);
        // Khu vuc phai thuoc dung san van dong cua tran
        if (match == null || section == null || !match.getStadiumId().equals(section.getStadiumId())) {
            return false;
        }
 
        TicketPricing pricing = this.ticketPricingRepository.findByMatchAndSection(matchId, sectionId);
        if (pricing != null) {
            pricing.setPrice(price);
            this.ticketPricingRepository.saveToFile();
            return true;
        }
        return this.ticketPricingRepository.save(
                new TicketPricing("PRC_" + matchId + "_" + sectionId, matchId, sectionId, price));
    }
 
    // Sinh ve AVAILABLE cho moi ghe cua cac khu vuc DA cau hinh gia. Tra ve so ve moi tao.
    // Khu vuc chua co gia bi bo qua; ghe da co ve trong tran nay khong bi tao lai.
    public int generateTickets(String matchId) {
        if (matchId == null || matchId.isEmpty()) {
            return 0;
        }
        Match match = this.matchRepository.findById(matchId);
        if (match == null) {
            return 0;
        }
 
        Set<String> ticketedSeatIds = new HashSet<>();
        for (Ticket existing : this.ticketRepository.findByMatchId(matchId)) {
            ticketedSeatIds.add(existing.getSeatId());
        }
 
        // Tuần 7 - Kiệt: Sử dụng generateNextTicketId() thread-safe từ TicketRepository
        // thay vì tính nextNumber từ size (dễ trùng ID khi đa luồng)
        List<Ticket> newTickets = new ArrayList<>();
        for (SeatSection section : this.sectionRepository.findByStadiumId(match.getStadiumId())) {
            TicketPricing pricing = this.ticketPricingRepository.findByMatchAndSection(matchId,
                    section.getSectionId());
            if (pricing == null) {
                continue;
            }
            for (Seat seat : this.seatRepository.findBySectionId(section.getSectionId())) {
                if (ticketedSeatIds.contains(seat.getSeatId())) {
                    continue;
                }
                String nextTicketId = this.ticketRepository.generateNextTicketId();
                Ticket ticket = new Ticket(nextTicketId, matchId, seat.getSeatId(),
                        "", "", pricing.getPrice(), "", TicketStatus.AVAILABLE);
                newTickets.add(ticket);
            }
        }
 
        // Them hang loat roi ghi file 1 lan qua saveAll() thread-safe
        if (!newTickets.isEmpty()) {
            this.ticketRepository.saveAll(newTickets);
        }
        return newTickets.size();
    }
 
    // Organizer huy toan bo ve cua 1 tran: ve -> CANCELLED, ghe da ban -> AVAILABLE,
    // giao dich lien quan -> CANCELLED. Tra ve false neu khong co ve nao de huy.
    public boolean cancelTickets(String matchId) {
        if (matchId == null || matchId.isEmpty()) {
            return false;
        }
 
        Set<String> transactionIds = new HashSet<>();
        int cancelled = 0;
        for (Ticket ticket : this.ticketRepository.findByMatchId(matchId)) {
            if (ticket.getStatus() == TicketStatus.CANCELLED) {
                continue;
            }
            if (ticket.getStatus() == TicketStatus.BOOKED) {
                Seat seat = this.seatRepository.findById(ticket.getSeatId());
                if (seat != null) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                    seat.incrementVersion();
                }
                transactionIds.add(ticket.getTransactionId());
            }
            ticket.setStatus(TicketStatus.CANCELLED);
            cancelled++;
        }
        if (cancelled == 0) {
            return false;
        }
 
        for (String transactionId : transactionIds) {
            BookingTransaction txn = this.transactionRepository.findById(transactionId);
            if (txn != null) {
                txn.setStatus(TransactionStatus.CANCELLED);
            }
        }
 
        this.ticketRepository.saveToFile();
        this.seatRepository.saveToFile();
        this.transactionRepository.saveToFile();
        return true;
    }
 
    // Xem báo cáo thống kê doanh thu bán vé của trận đấu (tổng vé, vé đã bán, doanh thu, tỷ lệ lấp đầy)
    public Map<String, Object> viewTicketSalesReport(String matchId) {
        Map<String, Object> report = new LinkedHashMap<>(); // Dùng LinkedHashMap để bảo toàn thứ tự hiển thị các trường thống kê
        if (matchId == null || matchId.isEmpty()) {
            return report;
        }
        Match match = this.matchRepository.findById(matchId);
        if (match == null) {
            return report;
        }
 
        List<Ticket> tickets = this.ticketRepository.findByMatchId(matchId);
        int sold = 0;
        int available = 0;
        int cancelled = 0;
        double revenue = 0;
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == TicketStatus.BOOKED) {
                sold++;
                revenue += ticket.getPrice();
            } else if (ticket.getStatus() == TicketStatus.AVAILABLE) {
                available++;
            } else if (ticket.getStatus() == TicketStatus.CANCELLED) {
                cancelled++;
            }
        }
 
        report.put("Match ID", matchId);
        report.put("Match title", match.getMatchTitle());
        report.put("Total tickets", tickets.size());
        report.put("Tickets sold", sold);
        report.put("Tickets available", available);
        report.put("Tickets cancelled", cancelled);
        report.put("Total revenue", revenue);
        report.put("Sold rate (%)", tickets.isEmpty() ? 0.0 : sold * 100.0 / tickets.size());
        return report;
    }
}