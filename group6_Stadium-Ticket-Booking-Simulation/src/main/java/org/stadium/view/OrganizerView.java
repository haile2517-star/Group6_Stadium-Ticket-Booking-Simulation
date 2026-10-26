package org.stadium.view;
import org.stadium.controller.TicketController;

import java.util.Map;
import java.util.Scanner;

public class OrganizerView {
    private Scanner scanner;
    private TicketController ticketController;

    public OrganizerView(Scanner scanner, TicketController ticketController){
        this.scanner = scanner;
        this.ticketController = ticketController;
    }

    public void displayPricingConfigForm(){
        System.out.println();
        System.out.println("======== CONFIGURE TICKET PRICING ========");
        System.out.print("Enter Match ID: ");
        String matchId = this.scanner.nextLine().trim();
         System.out.print("Enter Section ID: ");
        String sectionId = this.scanner.nextLine().trim();
         System.out.print("Enter ticket price: ");
        String priceInput = this.scanner.nextLine().trim();
        if(matchId.isEmpty() || sectionId.isEmpty()){
            System.out.println("[FAILED] Match ID and Section ID are required.");
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceInput);

        } catch (NumberFormatException e) {
            System.out.println("[FAILED] Price must be a valid number");
            return;
        }
        if(price <= 0){
            System.out.println("[FAILED] Price must be greater than 0");
            return;
        }
        boolean success = this.ticketController.configureTicketPricing(matchId, sectionId, price);
        if(success){
            System.out.printf("[SUCCESS] Price of %,.0f set for section %s in match %s.%n", price, sectionId,
                    matchId);

        }else{
            System.out.println("[FAILED] Could not configure pricing. Check the Match ID and Section ID.");
        }
    }
    public void displayGenerateTicketsForm(){
        System.out.println();
        System.out.println("======== GENERATE TICKETS ========");
        System.out.print("Enter Match ID: ");
        String matchId = this.scanner.nextLine().trim();
        if(matchId.isEmpty()){
            System.out.println("[FAILED] Match ID is required.");
            return;
        }
        int count = this.ticketController.generateTickets(matchId);
        if (count > 0){
            System.out.println("[SUCCESS] Generate "+ count + " Tickets for match " + matchId +".");
        }else{
            System.out.println("[FAILED] No tickets were generated for match " + matchId + ".");
        }
    }

    public void displayCancelTicketsForm() {
        System.out.println();
        System.out.println("======== HỦY TOÀN BỘ VÉ TRẬN ĐẤU (CANCEL ALL TICKETS) ========");
        System.out.print("Enter Match ID: ");
        String matchId = this.scanner.nextLine().trim();
        if (matchId.isEmpty()) {
            System.out.println("[FAILED] Match ID is required.");
            return;
        }
        boolean success = this.ticketController.cancelTickets(matchId);
        if (success) {
            System.out.println("[SUCCESS] Cancelled all tickets for match " + matchId + " (Đã hủy toàn bộ vé thành công).");
        } else {
            System.out.println("[FAILED] Could not cancel tickets for match " + matchId + " (Không tìm thấy vé hoặc vé đã bị hủy).");
        }
    }
    public void displaySalesReport(Map<String, Object> salesData){
        System.out.println();
        System.out.println("=========== TICKET SALES REPORT ===========");
        if(salesData == null || salesData.isEmpty()){
            System.out.println("No sales data available");
            return;
        }
        for(Map.Entry<String, Object> entry : salesData.entrySet()){
            Object value = entry.getValue();
            String text = (value instanceof Double) ? String.format("%,.2f", value) : String.valueOf(value);
            System.out.printf("%-25s : %s%n", entry.getKey(), text);
        }
       System.out.println("===========================================");;
    }

}
