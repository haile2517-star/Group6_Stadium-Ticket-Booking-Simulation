package org.stadium.view;

import org.stadium.controller.MainController;
import org.stadium.model.Account;

import java.util.Scanner;

public class MainView {

    private Scanner scanner;
    private MainController mainController;

    public MainView() {
        this.scanner = new Scanner(System.in);
        this.mainController = new MainController(this.scanner, this);
    }

    public MainView(Scanner scanner) {
        this.scanner = scanner != null ? scanner : new Scanner(System.in);
        this.mainController = new MainController(this.scanner, this);
    }

    public MainView(Scanner scanner, MainController mainController) {
        this.scanner = scanner != null ? scanner : new Scanner(System.in);
        this.mainController = mainController;
    }

    public void start() {
        if (this.mainController == null) {
            this.mainController = new MainController(this.scanner, this);
        }
        this.mainController.start();
    }

    public void displayWelcomeBanner() {
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                STADIUM TICKET BOOKING SIMULATION SYSTEM                 |");
        System.out.println("|                      GROUP 6 - LAB211 (SEMESTER 3)                      |");
        System.out.println("+-------------------------------------------------------------------------+");
    }

    public void displayGoodbye() {
        System.out.println("\nThank you for using Stadium Ticket Booking System! Goodbye.");
    }

    public void displayGuestMenu() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                               GUEST MENU                                |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println(" 1. View match list");
        System.out.println(" 2. View match details");
        System.out.println(" 3. Search matches by keyword");
        System.out.println(" 4. Filter matches by criteria");
        System.out.println(" 5. View stadium list");
        System.out.println(" 6. View stands & seat map");
        System.out.println(" 7. Login to system");
        System.out.println(" 8. Register Fan account");
        System.out.println(" 9. Register Organizer account");
        System.out.println(" 0. Exit program");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Please choose an option (0-9): ");
        if (!this.scanner.hasNextLine()) {
            if (this.mainController != null) {
                this.mainController.setRunning(false);
            }
            return;
        }
        String choice = this.scanner.nextLine().trim();
        if (this.mainController != null) {
            this.mainController.handleGuestChoice(choice);
        }
    }

    public void displayFanMenu() {
        Account user = this.mainController != null ? this.mainController.getCurrentUser() : null;
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                                FAN MENU                                 |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.printf("| User: %-30s | ID: %-30s |%n",
                user != null ? user.getUsername() : "",
                user != null ? user.getId() : "");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println(" 1. View match list");
        System.out.println(" 2. Search matches");
        System.out.println(" 3. Filter matches by criteria");
        System.out.println(" 4. View section seat map");
        System.out.println(" 5. Book match tickets (Max 4 tickets)");
        System.out.println(" 6. View my booking history & tickets");
        System.out.println(" 7. Cancel booked ticket");
        System.out.println(" 8. View personal profile");
        System.out.println(" 9. Update profile");
        System.out.println(" 10. Change password");
        System.out.println(" 0. Logout");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Please choose an option (0-10): ");
        if (!this.scanner.hasNextLine()) {
            if (this.mainController != null) {
                this.mainController.setRunning(false);
            }
            return;
        }
        String choice = this.scanner.nextLine().trim();
        if (this.mainController != null) {
            this.mainController.handleFanChoice(choice);
        }
    }

    public void displayOrganizerMenu() {
        Account user = this.mainController != null ? this.mainController.getCurrentUser() : null;
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                             ORGANIZER MENU                              |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.printf("| Org: %-31s | ID: %-30s |%n",
                user != null ? user.getUsername() : "",
                user != null ? user.getId() : "");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println(" 1. View match list");
        System.out.println(" 2. Create new match event");
        System.out.println(" 3. Update match details");
        System.out.println(" 4. Delete match");
        System.out.println(" 5. Configure ticket pricing by section");
        System.out.println(" 6. Automatically generate tickets for match");
        System.out.println(" 7. Cancel all tickets of match");
        System.out.println(" 8. View ticket sales report");
        System.out.println(" 9. View organization profile");
        System.out.println(" 10. Update profile");
        System.out.println(" 11. Change password");
        System.out.println(" 0. Logout");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Please choose an option (0-11): ");
        if (!this.scanner.hasNextLine()) {
            if (this.mainController != null) {
                this.mainController.setRunning(false);
            }
            return;
        }
        String choice = this.scanner.nextLine().trim();
        if (this.mainController != null) {
            this.mainController.handleOrganizerChoice(choice);
        }
    }

    public void displayAdminMenu() {
        Account user = this.mainController != null ? this.mainController.getCurrentUser() : null;
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                               ADMIN MENU                                |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.printf("| Admin: %-29s | ID: %-30s |%n",
                user != null ? user.getUsername() : "",
                user != null ? user.getId() : "");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("  1. Manage Fan (Approve, Search, Delete)");
        System.out.println("  2. Manage Organizer (Approve, Search, Delete)");
        System.out.println("  3. Manage Seat (Create, Update, Search, Delete)");
        System.out.println("  4. Benchmark (Run Concurrency Benchmark, View Reports)");
        System.out.println("  5. View Profile");
        System.out.println("  6. Change Password");
        System.out.println("  0. Logout");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Please choose an option (0-6): ");
        if (!this.scanner.hasNextLine()) {
            if (this.mainController != null) {
                this.mainController.setRunning(false);
            }
            return;
        }
        String choice = this.scanner.nextLine().trim();
        if (this.mainController != null) {
            this.mainController.handleAdminChoice(choice);
        }
    }

    public MainController getMainController() {
        return this.mainController;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public Scanner getScanner() {
        return this.scanner;
    }

    public void setScanner(Scanner scanner) {
        this.scanner = scanner;
    }
}
