package org.stadium.view;

import org.stadium.controller.TicketController;

import java.util.Map;
import java.util.Scanner;

public class OrganizerView {

    private Scanner scanner;
    private TicketController ticketController;

    public OrganizerView(Scanner scanner, TicketController ticketController) {
        this.scanner = scanner;
        this.ticketController = ticketController;
    }

    public void displayPricingConfigForm() {
        String border = "+-------------------------------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                        CONFIGURE TICKET PRICING                         |");
        System.out.println(border);
        System.out.print("Enter Match ID: ");
        String matchId = this.scanner.nextLine().trim();
        System.out.print("Enter Section ID: ");
        String sectionId = this.scanner.nextLine().trim();
        System.out.print("Enter ticket price: ");
        String priceInput = this.scanner.nextLine().trim();
        if (matchId.isEmpty() || sectionId.isEmpty()) {
            System.out.println("[FAILED] Match ID and Section ID are required.");
            return;
        }
        double price;
        try {
            price = Double.parseDouble(priceInput);
        } catch (NumberFormatException e) {
            System.out.println("[FAILED] Price must be a valid number.");
            return;
        }
        if (price <= 0) {
            System.out.println("[FAILED] Price must be greater than 0.");
            return;
        }
        boolean success = this.ticketController.configureTicketPricing(matchId, sectionId, price);
        if (success) {
            System.out.printf("[SUCCESS] Price of %,.0f VND set for section %s in match %s.%n", price, sectionId, matchId);
        } else {
            System.out.println("[FAILED] Could not configure pricing. Check Match ID and Section ID.");
        }
    }

    public void displayGenerateTicketsForm() {
        String border = "+-------------------------------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                            GENERATE TICKETS                             |");
        System.out.println(border);
        System.out.print("Enter Match ID: ");
        String matchId = this.scanner.nextLine().trim();
        if (matchId.isEmpty()) {
            System.out.println("[FAILED] Match ID is required.");
            return;
        }
        int count = this.ticketController.generateTickets(matchId);
        if (count > 0) {
            System.out.println("[SUCCESS] Generated " + count + " tickets for match " + matchId + ".");
        } else {
            System.out.println("[FAILED] No tickets were generated for match " + matchId + ". Ensure pricing is configured.");
        }
    }

    public void displayCancelTicketsForm() {
        String border = "+-------------------------------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                        CANCEL ALL MATCH TICKETS                         |");
        System.out.println(border);
        System.out.print("Enter Match ID: ");
        String matchId = this.scanner.nextLine().trim();
        if (matchId.isEmpty()) {
            System.out.println("[FAILED] Match ID is required.");
            return;
        }
        boolean success = this.ticketController.cancelTickets(matchId);
        if (success) {
            System.out.println("[SUCCESS] Cancelled all tickets for match " + matchId + ".");
        } else {
            System.out.println("[FAILED] Could not cancel tickets for match " + matchId + ".");
        }
    }

    public void displaySalesReport(Map<String, Object> salesData) {
        String border = "+-------------------------------------------------------------------------+";
        String gridBorder = "+----------------------+--------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                           TICKET SALES REPORT                           |");
        System.out.println(border);
        if (salesData == null || salesData.isEmpty()) {
            System.out.println("| [INFO] No sales data available for this match.                          |");
            System.out.println(border);
            return;
        }

        System.out.println(gridBorder);
        System.out.println("| Metric / Category    | Value                                            |");
        System.out.println(gridBorder);
        for (Map.Entry<String, Object> entry : salesData.entrySet()) {
            String key = this.padOrTruncate(entry.getKey(), 20);
            Object value = entry.getValue();
            String text;
            if (entry.getKey().toLowerCase().contains("revenue") && value instanceof Number) {
                text = String.format("%,.0f VND", ((Number) value).doubleValue());
            } else if (entry.getKey().toLowerCase().contains("rate") && value instanceof Number) {
                text = String.format("%.2f %%", ((Number) value).doubleValue());
            } else if (value instanceof Number) {
                text = String.format("%,d", ((Number) value).longValue());
            } else {
                text = String.valueOf(value);
            }
            String valStr = this.padOrTruncate(text, 48);
            System.out.printf("| %s | %s |%n", key, valStr);
        }
        System.out.println(gridBorder);
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
