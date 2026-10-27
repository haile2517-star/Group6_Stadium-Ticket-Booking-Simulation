package org.stadium.view;

import org.stadium.model.BookingTransaction;
import org.stadium.model.Seat;
import org.stadium.model.Ticket;
import org.stadium.controller.BookingController;
import org.stadium.model.enums.SynchronizationMechanism;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

public class BookingView {
    private Scanner scanner;
    private BookingController bookingController;

    public BookingView(Scanner scanner, BookingController bookingController) {
        this.scanner = scanner;
        this.bookingController = bookingController;
    }

    public void displayBookingForm(List<Seat> seats, String fanId, String matchId) {
        String border = "+-------------------------------------------------------------------------+";
        String seatBorder = "+----------------------+------------------+-----+---------+-----------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                              BOOK TICKETS                               |");
        System.out.println(border);
        if (seats == null || seats.isEmpty()) {
            System.out.println("| No available seats to book in this section.                             |");
            System.out.println(border);
            return;
        }
        if (fanId == null || fanId.trim().isEmpty() || matchId == null || matchId.trim().isEmpty()) {
            this.showBookingResult(false, "Fan ID and Match ID are required.");
            return;
        }

        System.out.println(seatBorder);
        System.out.println("| Seat ID              | Section          | Row | Seat No | Type      |");
        System.out.println(seatBorder);

        int limit = Math.min(seats.size(), 20);
        for (int i = 0; i < limit; i++) {
            Seat seat = seats.get(i);
            String id = this.padOrTruncate(seat.getSeatId(), 20);
            String sec = this.padOrTruncate(seat.getSectionId(), 16);
            String row = this.padOrTruncate(String.valueOf(seat.getRowNumber()), 3);
            String num = this.padOrTruncate(seat.getSeatNumber(), 7);
            String type = this.padOrTruncate(seat.getSeatType() != null ? seat.getSeatType().name() : "N/A", 9);
            System.out.printf("| %s | %s | %s | %s | %s |%n", id, sec, row, num, type);
        }
        System.out.println(seatBorder);
        if (seats.size() > limit) {
            System.out.printf("| Showing first %-2d of %-3d seats. Tip: view seat map for full layout.  |%n", limit,
                    seats.size());
            System.out.println(border);
        }
        System.out.printf("| Total: %-64s |%n", seats.size() + " available seats in section");
        System.out.println(border);

        System.out.print("Enter seat IDs separated by commas (max 4): ");
        String seatInput = this.scanner.nextLine().trim();
        List<String> seatIds = new ArrayList<>();
        for (String part : seatInput.split(",")) {
            if (!part.trim().isEmpty()) {
                seatIds.add(part.trim());
            }
        }
        if (fanId.isEmpty() || matchId.isEmpty() || seatIds.isEmpty()) {
            this.showBookingResult(false, "Fan ID, Match ID and at least one seat are required.");
            return;
        }
        if (seatIds.size() > 4) {
            this.showBookingResult(false, "You can book at most 4 tickets per booking.");
            return;
        }
        if (new HashSet<>(seatIds).size() != seatIds.size()) {
            this.showBookingResult(false, "Duplicate seat IDs are not allowed.");
            return;
        }
        boolean success = this.bookingController.bookTickets(fanId.trim(), matchId.trim(), seatIds,
                SynchronizationMechanism.NO_LOCK);
        if (!success) {
            this.showBookingResult(false,
                    "Booking failed. A selected seat may have just been booked or the request is invalid.");
            return;
        }
        List<BookingTransaction> history = this.bookingController.viewBookingHistory(fanId.trim());
        BookingTransaction latest = history.get(history.size() - 1);
        List<Ticket> tickets = this.bookingController.getTicketDetails(latest.getId());
        StringBuilder message = new StringBuilder();
        message.append("Booking completed successfully!\n");
        message.append(String.format("Transaction: %s | Match: %s | Date: %s%n", latest.getId(),
                latest.getMatchId(), latest.getTransactionDate()));
        message.append("Your tickets:\n");
        for (Ticket ticket : tickets) {
            message.append(String.format("  - Ticket %s | Seat %s | Price: %,.0f VND%n", ticket.getTicketId(),
                    ticket.getSeatId(), ticket.getPrice()));
        }
        message.append(String.format("Total amount: %,.0f VND", latest.getTotalAmount()));
        this.showBookingResult(true, message.toString());
        this.displayBookingHistory(history);
    }

    public void showBookingResult(boolean success, String msg) {
        System.out.println();
        System.out.println(success ? "[SUCCESS]" : "[FAILED]");
        System.out.println(msg);
    }

    public void displayBookingHistory(List<BookingTransaction> txns) {
        String border = "+-------------------------------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                          BOOKING TRANSACTIONS                           |");
        System.out.println(border);
        if (txns == null || txns.isEmpty()) {
            System.out.println("| [INFO] No booking transactions found.                                   |");
            System.out.println(border);
            return;
        }

        String txnBorder = "+----------+-------+------------------+---------+------------+-----------+";
        System.out.println(txnBorder);
        System.out.println("| Txn ID   | Match | Date & Time      | Tickets | Total(VND) | Status    |");
        System.out.println(txnBorder);
        for (BookingTransaction txn : txns) {
            String tid = this.padOrTruncate(txn.getId(), 8);
            String mid = this.padOrTruncate(txn.getMatchId(), 5);
            String rawDate = txn.getTransactionDate();
            String dateStr = (rawDate != null && rawDate.length() >= 16) ? rawDate.substring(0, 16) : rawDate;
            String time = this.padOrTruncate(dateStr, 16);
            String count = this.padOrTruncate(String.valueOf(txn.getTicketCount()), 7);
            String total = this.padOrTruncate(String.format("%,.0f", txn.getTotalAmount()), 10);
            String status = this.padOrTruncate(txn.getStatus() != null ? txn.getStatus().name() : "N/A", 9);

            System.out.printf("| %s | %s | %s | %s | %s | %s |%n", tid, mid, time, count, total, status);
        }
        System.out.println(txnBorder);
        System.out.printf("| Total: %-64s |%n", txns.size() + " transactions completed");
        System.out.println(border);

        System.out.println();
        System.out.println(border);
        System.out.println("|                               MY TICKETS                                |");
        System.out.println(border);
        String tckBorder = "+------------+-------+-----------------------+---------------+-------------+";
        System.out.println(tckBorder);
        System.out.println("| Ticket ID  | Match | Seat ID               | Price (VND)   | Status      |");
        System.out.println(tckBorder);
        int ticketCount = 0;
        for (BookingTransaction txn : txns) {
            List<Ticket> tickets = this.bookingController.getTicketDetails(txn.getId());
            for (Ticket ticket : tickets) {
                ticketCount++;
                String tid = this.padOrTruncate(ticket.getTicketId(), 10);
                String mid = this.padOrTruncate(ticket.getMatchId(), 5);
                String sid = this.padOrTruncate(ticket.getSeatId(), 21);
                String price = this.padOrTruncate(String.format("%,.0f", ticket.getPrice()), 13);
                String status = this.padOrTruncate(ticket.getStatus() != null ? ticket.getStatus().name() : "N/A", 11);
                System.out.printf("| %s | %s | %s | %s | %s |%n", tid, mid, sid, price, status);
            }
        }
        if (ticketCount == 0) {
            System.out.println("| No individual tickets found.                                            |");
        }
        System.out.println(tckBorder);
        System.out.printf("| Total: %-64s |%n", ticketCount + " tickets owned");
        System.out.println(border);
    }

    public void displayCancelTicketForm() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                              CANCEL TICKET                              |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter your Fan ID: ");
        String fanId = this.scanner.nextLine().trim();
        System.out.print("Enter Ticket ID to cancel (e.g., TCK000153): ");
        String ticketId = this.scanner.nextLine().trim();

        if (fanId.isEmpty() || ticketId.isEmpty()) {
            this.showBookingResult(false, "Fan ID and Ticket ID are required.");
            return;
        }
        boolean success = this.bookingController.cancelTicket(ticketId, fanId);
        if (success) {
            this.showBookingResult(true,
                    "Ticket " + ticketId + " has been cancelled. The seat is now AVAILABLE again.");
        } else {
            this.showBookingResult(false,
                    "Could not cancel ticket. Verify that Ticket ID exists, belongs to you, and is currently BOOKED.");
        }
    }

    private String padOrTruncate(String text, int width) {
        if (text == null) {
            text = "N/A";
        }
        text = text.trim();
        if (text.length() > width) {
            if (width <= 3) {
                return text.substring(0, width);
            }
            return text.substring(0, width - 3) + "...";
        }
        return String.format("%-" + width + "s", text);
    }
}
