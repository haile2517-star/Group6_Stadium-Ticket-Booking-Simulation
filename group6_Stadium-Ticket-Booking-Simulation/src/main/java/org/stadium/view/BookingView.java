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

    public BookingView(Scanner scanner, BookingController bookingController){
        this.scanner = scanner;
        this.bookingController = bookingController;
    }

    public void displayBookingForm(List<Seat> seats, String fanId, String matchId){
        System.out.println();
        System.out.println("============ BOOK TICKETS ============");
        if (seats == null || seats.isEmpty()){
            System.out.println("No available seats to book");
            return;
        }
        if (fanId == null || fanId.trim().isEmpty() || matchId == null || matchId.trim().isEmpty()) {
            this.showBookingResult(false, "Fan ID and Match ID are required.");
            return;
        }
        System.out.println("Available seats:");
        System.out.printf("%-12s %-14s %-5s %-6s %-10s%n", "Seat ID", "Section", "Row", "Seat", "Type");
        System.out.println("---------------------------------------------------");

    for(Seat seat : seats){
        System.out.printf("%-12s %-14s %-5d %-6s %-10s%n", seat.getSeatId(), seat.getSectionId(), seat.getRowNumber(), seat.getSeatNumber(), seat.getSeatType());
    }
    System.out.println("---------------------------------------------------");
    System.out.println("Enter seat IDs separated by commas(max 4): ");
    String seatInput = this.scanner.nextLine().trim();
    List<String> seatIds = new ArrayList<>();
    for (String part : seatInput.split(",")){
        if(!part.trim().isEmpty()){
            seatIds.add(part.trim());
        }
    }
    if (fanId.isEmpty() || matchId.isEmpty() || seatIds.isEmpty()){
        this.showBookingResult(false, "Fan ID, Match ID and at least one seat are required.");
        return;

    }
    if(seatIds.size() > 4){
        this.showBookingResult(false, "You can book at most 4 tickets per booking");
        return;
    }
    if(new HashSet<>(seatIds).size() != seatIds.size()){
        this.showBookingResult(false, "Duplicate seat IDs are not allowed. ");
        return;
    }
    boolean success = this.bookingController.bookTickets(fanId.trim(), matchId.trim(), seatIds, SynchronizationMechanism.NO_LOCK);
    if(!success){
        this.showBookingResult(false, "Booking failed. A selected seat may have just been booked or the request is invalid.");
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
                for(Ticket ticket : tickets){
                    message.append(String.format("  - Ticket %s | Seat %s | Price: %,.0f%n", ticket.getTicketId(),
                    ticket.getSeatId(), ticket.getPrice()));

                }
                message.append(String.format("Total amount: %,.0f", latest.getTotalAmount()));
                this.showBookingResult(true, message.toString());
                this.displayBookingHistory(history);

    }
    public void showBookingResult(boolean success, String msg){
        System.out.println();
        System.out.println(success ? "[SUCCESS]" : "[FAILED]");
        System.out.println(msg);

    }
    public void displayBookingHistory(List<BookingTransaction> txns){
        System.out.println();
        System.out.println("========== BOOKING HISTORY ==========");
        if(txns == null || txns.isEmpty()){
            System.out.println("No transactions found.");
            return;
        }
          System.out.printf("%-12s %-10s %-20s %-8s %-14s %-10s%n", "Transaction", "Match", "Date", "Tickets",
                "Total", "Status");
                 System.out.println("-------------------------------------------------------------------------------");
                 for (BookingTransaction txn : txns){
                    System.out.printf("%-12s %-10s %-20s %-8d %,-14.0f %-10s%n", txn.getId(), txn.getMatchId(),
                    txn.getTransactionDate(), txn.getTicketCount(), txn.getTotalAmount(), txn.getStatus());
                 }
                 System.out.println("-------------------------------------------------------------------------------");
    }
    public void displayCancelTicketForm(){
        System.out.println();
        System.out.println("=========== CANCEL TICKET ===========");
        System.out.print("Enter your Fan ID: ");
        String fanId = this.scanner.nextLine().trim();
        System.out.print("Enter your Ticket ID to cancel: ");
        String ticketId = this.scanner.nextLine().trim();

        if(fanId.isEmpty() || ticketId.isEmpty()){
            this.showBookingResult(false, "Fan ID and Ticket ID are required.");
            return;
        }
        boolean success = this.bookingController.cancelTicket(ticketId, fanId);
        if(success){
            this.showBookingResult(true, "Ticket " + ticketId + " has been cancelled. The seat is available again.");

        }else{
            this.showBookingResult(false, "could not cancel the ticket. Check that it exists, belongs to you and is still booked.");

        }

    }

}
