package org.stadium.controller;

import org.stadium.model.Account;
import org.stadium.model.Admin;
import org.stadium.model.BookingTransaction;
import org.stadium.model.Fan;
import org.stadium.model.Match;
import org.stadium.model.MatchFilter;
import org.stadium.model.Organizer;
import org.stadium.model.Seat;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;
import org.stadium.repository.MatchRepository;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.SectionRepository;
import org.stadium.repository.SimulationResultRepository;
import org.stadium.repository.StadiumRepository;
import org.stadium.repository.TicketPricingRepository;
import org.stadium.repository.TicketRepository;
import org.stadium.repository.TransactionRepository;
import org.stadium.view.AdminView;
import org.stadium.view.AuthView;
import org.stadium.view.BookingView;
import org.stadium.view.MainView;
import org.stadium.view.MatchView;
import org.stadium.view.OrganizerView;
import org.stadium.view.SeatMapView;
import org.stadium.view.StadiumView;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainController {

    private Scanner scanner;
    private AuthController authController;
    private AdminController adminController;
    private MatchController matchController;
    private BookingController bookingController;
    private StadiumController stadiumController;
    private TicketController ticketController;

    private MainView mainView;
    private MatchView matchView;
    private BookingView bookingView;
    private OrganizerView organizerView;
    private SeatMapView seatMapView;
    private StadiumView stadiumView;
    private AdminView adminView;
    private AuthView authView;

    private Account currentUser;
    private boolean running;

    public MainController() {
        this.scanner = new Scanner(System.in);
        this.initializeDependencies();
    }

    public MainController(Scanner scanner) {
        this.scanner = scanner != null ? scanner : new Scanner(System.in);
        this.initializeDependencies();
    }

    public MainController(Scanner scanner, MainView mainView) {
        this.scanner = scanner != null ? scanner : new Scanner(System.in);
        this.mainView = mainView;
        this.initializeDependencies();
    }

    private void initializeDependencies() {
        String pathMatches = new File("data/matches.csv").exists() ? "data/matches.csv"
                : (new File("../data/matches.csv").exists() ? "../data/matches.csv" : "data/matches.csv");
        String pathStadiums = new File("data/stadiums.csv").exists() ? "data/stadiums.csv"
                : (new File("../data/stadiums.csv").exists() ? "../data/stadiums.csv" : "data/stadiums.csv");
        String pathSections = new File("data/sections.csv").exists() ? "data/sections.csv"
                : (new File("../data/sections.csv").exists() ? "../data/sections.csv" : "data/sections.csv");
        String pathSeats = new File("data/seats.csv").exists() ? "data/seats.csv"
                : (new File("../data/seats.csv").exists() ? "../data/seats.csv" : "data/seats.csv");
        String pathPricings = new File("data/ticket_pricings.csv").exists() ? "data/ticket_pricings.csv"
                : (new File("../data/ticket_pricings.csv").exists() ? "../data/ticket_pricings.csv"
                        : "data/ticket_pricings.csv");
        String pathTickets = new File("data/tickets.csv").exists() ? "data/tickets.csv"
                : (new File("../data/tickets.csv").exists() ? "../data/tickets.csv" : "data/tickets.csv");
        String pathTxns = new File("data/transactions.csv").exists() ? "data/transactions.csv"
                : (new File("../data/transactions.csv").exists() ? "../data/transactions.csv"
                        : "data/transactions.csv");
        String pathSims = new File("data/simulation_results.csv").exists() ? "data/simulation_results.csv"
                : (new File("../data/simulation_results.csv").exists() ? "../data/simulation_results.csv"
                        : "data/simulation_results.csv");

        MatchRepository matchRepo = new MatchRepository(pathMatches);
        StadiumRepository stadiumRepo = new StadiumRepository(pathStadiums);
        SectionRepository sectionRepo = new SectionRepository(pathSections);
        SeatRepository seatRepo = new SeatRepository(pathSeats);
        TicketPricingRepository pricingRepo = new TicketPricingRepository(pathPricings);
        TicketRepository ticketRepo = new TicketRepository(pathTickets);
        TransactionRepository txnRepo = new TransactionRepository(pathTxns);
        SimulationResultRepository simRepo = new SimulationResultRepository(pathSims);

        this.authController = new AuthController();
        this.adminController = new AdminController(this.authController.getFanRepo(),
                this.authController.getOrganizerRepo(), seatRepo, simRepo);
        this.matchController = new MatchController(matchRepo);
        this.stadiumController = new StadiumController(stadiumRepo, sectionRepo, seatRepo);
        this.bookingController = new BookingController(seatRepo, ticketRepo, txnRepo);
        this.ticketController = new TicketController(matchRepo, sectionRepo, seatRepo, ticketRepo, pricingRepo,
                txnRepo);

        this.matchView = new MatchView(this.scanner, this.matchController);
        this.bookingView = new BookingView(this.scanner, this.bookingController);
        this.organizerView = new OrganizerView(this.scanner, this.ticketController);
        this.seatMapView = new SeatMapView(this.stadiumController);
        this.stadiumView = new StadiumView(this.stadiumController, this.seatMapView);
        this.adminView = new AdminView(this.adminController, this.scanner);
        this.authView = new AuthView(this.authController, this.scanner);

        if (this.mainView == null) {
            this.mainView = new MainView(this.scanner, this);
        }

        this.currentUser = null;
        this.running = true;
    }

    public void start() {
        this.mainView.displayWelcomeBanner();

        while (this.running) {
            if (this.currentUser == null) {
                this.mainView.displayGuestMenu();
            } else if (this.currentUser instanceof Fan) {
                this.mainView.displayFanMenu();
            } else if (this.currentUser instanceof Organizer) {
                this.mainView.displayOrganizerMenu();
            } else if (this.currentUser instanceof Admin) {
                this.mainView.displayAdminMenu();
            } else {
                this.currentUser = null;
            }
        }
        this.mainView.displayGoodbye();
    }

    public void handleGuestChoice(String choice) {
        if ("1".equals(choice)) {
            List<Match> matches = this.matchController.viewAllMatches();
            this.matchView.displayMatches(matches);
        } else if ("2".equals(choice)) {
            System.out.print("Enter match ID to view details (e.g., MTH01): ");
            String matchId = this.scanner.nextLine().trim();
            Match match = null;
            for (Match m : this.matchController.viewAllMatches()) {
                if (m.getMatchId().equalsIgnoreCase(matchId)) {
                    match = m;
                    break;
                }
            }
            this.matchView.displayMatchDetails(match);
        } else if ("3".equals(choice)) {
            String keyword = this.matchView.promptSearchKeyword();
            List<Match> results = this.matchController.searchMatches(keyword);
            this.matchView.displayMatches(results);
        } else if ("4".equals(choice)) {
            MatchFilter filter = this.matchView.promptFilterCriteria();
            List<Match> filtered = this.matchController.filterMatches(filter);
            this.matchView.displayMatches(filtered);
        } else if ("5".equals(choice)) {
            List<Stadium> stadiums = this.stadiumController.getAllStadiums();
            this.stadiumView.displayStadiumList(stadiums);
        } else if ("6".equals(choice)) {
            System.out.print("Enter stadium ID (e.g., STD01): ");
            if (!this.scanner.hasNextLine()) {
                this.running = false;
                return;
            }
            String stadiumId = this.scanner.nextLine().trim();
            List<SeatSection> sections = this.stadiumController.getSections(stadiumId);
            this.stadiumView.displaySectionList(stadiumId, sections);
            if (!sections.isEmpty()) {
                System.out.print(
                        "Enter section ID to view ASCII seat map (e.g., " + sections.get(0).getSectionId() + "): ");
                if (!this.scanner.hasNextLine()) {
                    this.running = false;
                    return;
                }
                String secId = this.scanner.nextLine().trim();
                String map = this.stadiumController.buildSeatMap(secId);
                this.seatMapView.displaySeatMap(map);
            }
        } else if ("7".equals(choice)) {
            System.out.println("\n============= LOGIN =============");
            System.out.print("Username: ");
            String u = this.scanner.nextLine().trim();
            System.out.print("Password: ");
            String p = this.scanner.nextLine().trim();
            Account acc = this.authController.login(u, p);
            if (acc != null) {
                this.currentUser = acc;
                System.out.println("[SUCCESS] Login successful! Welcome " + acc.getUsername());
            } else {
                System.out.println("[FAILED] Login failed. Invalid username/password or account is not ACTIVE.");
            }
        } else if ("8".equals(choice)) {
            System.out.println("\n============= REGISTER FAN ACCOUNT =============");
            System.out.print("Username: ");
            String fu = this.scanner.nextLine().trim();
            System.out.print("Password: ");
            String fp = this.scanner.nextLine().trim();
            System.out.print("Full name: ");
            String fname = this.scanner.nextLine().trim();
            System.out.print("Email: ");
            String femail = this.scanner.nextLine().trim();
            System.out.print("Phone: ");
            String fphone = this.scanner.nextLine().trim();
            boolean ok = this.authController.registerFan(fu, fp, fname, femail, fphone);
            if (ok) {
                System.out.println("[SUCCESS] Fan registered successfully! You can login now.");
            } else {
                System.out.println("[FAILED] Registration failed. Username/email/phone may already exist.");
            }
        } else if ("9".equals(choice)) {
            System.out.println("\n============= REGISTER ORGANIZER ACCOUNT =============");
            System.out.print("Username: ");
            String ou = this.scanner.nextLine().trim();
            System.out.print("Password: ");
            String op = this.scanner.nextLine().trim();
            System.out.print("Organization name: ");
            String orgName = this.scanner.nextLine().trim();
            System.out.print("Email: ");
            String oemail = this.scanner.nextLine().trim();
            System.out.print("Phone: ");
            String ophone = this.scanner.nextLine().trim();
            boolean ok = this.authController.registerOrganizer(ou, op, orgName, oemail, ophone);
            if (ok) {
                System.out.println(
                        "[SUCCESS] Organizer registered successfully! Status is PENDING, awaiting Admin approval.");
            } else {
                System.out.println("[FAILED] Registration failed. Please check the entered information.");
            }
        } else if ("0".equals(choice)) {
            this.running = false;
        } else {
            System.out.println("[WARNING] Invalid option, please try again.");
        }
    }

    public void handleFanChoice(String choice) {
        if ("1".equals(choice)) {
            this.matchView.displayMatches(this.matchController.viewAllMatches());
        } else if ("2".equals(choice)) {
            this.matchView.displayMatches(this.matchController.searchMatches(this.matchView.promptSearchKeyword()));
        } else if ("3".equals(choice)) {
            this.matchView.displayMatches(this.matchController.filterMatches(this.matchView.promptFilterCriteria()));
        } else if ("4".equals(choice)) {
            System.out.print("Enter section ID to view seat map (e.g., SEC_STD01_A): ");
            if (!this.scanner.hasNextLine()) {
                this.running = false;
                return;
            }
            String secId = this.scanner.nextLine().trim();
            String map = this.stadiumController.buildSeatMap(secId);
            this.seatMapView.displaySeatMap(map);
        } else if ("5".equals(choice)) {
            this.handleFanBookingFlow();
        } else if ("6".equals(choice)) {
            List<BookingTransaction> txns = this.bookingController.viewBookingHistory(this.currentUser.getId());
            this.bookingView.displayBookingHistory(txns);
        } else if ("7".equals(choice)) {
            this.bookingView.displayCancelTicketForm();
        } else if ("8".equals(choice)) {
            Account acc = this.authController.viewProfile(this.currentUser.getId());
            this.authView.displayProfile(acc);
        } else if ("9".equals(choice)) {
            this.authView.displayUpdateProfileForm();
        } else if ("10".equals(choice)) {
            System.out.println("\n============= CHANGE PASSWORD =============");
            System.out.print("Current password: ");
            String oldPass = this.scanner.nextLine().trim();
            System.out.print("New password    : ");
            String newPass = this.scanner.nextLine().trim();
            boolean ok = this.authController.changePassword(this.currentUser.getId(), oldPass, newPass);
            if (ok) {
                System.out.println("[SUCCESS] Password changed successfully!");
            } else {
                System.out.println(
                        "[FAILED] Password change failed. Current password incorrect or invalid new password.");
            }
        } else if ("0".equals(choice)) {
            this.authController.logout(this.currentUser.getId());
            this.currentUser = null;
            System.out.println("[INFO] Logged out successfully.");
        } else {
            System.out.println("[WARNING] Invalid option, please try again.");
        }
    }

    public void handleFanBookingFlow() {
        this.matchView.displayMatches(this.matchController.viewAllMatches());
        System.out.print("Enter match ID to book tickets (e.g., MTH01): ");
        if (!this.scanner.hasNextLine()) {
            this.running = false;
            return;
        }
        String requestedMatchId = this.scanner.nextLine().trim();
        Match selectedMatch = null;
        for (Match match : this.matchController.viewAllMatches()) {
            if (match.getMatchId().equalsIgnoreCase(requestedMatchId)) {
                selectedMatch = match;
                break;
            }
        }
        if (selectedMatch == null) {
            System.out.println("[FAILED] Match not found with the entered ID.");
            return;
        }

        this.ticketController.generateTickets(selectedMatch.getMatchId());
        List<SeatSection> sections = this.stadiumController.getSections(selectedMatch.getStadiumId());
        this.stadiumView.displaySectionList(selectedMatch.getStadiumId(), sections);
        System.out.print("Enter section ID to select seats (e.g., SEC_STD01_A): ");
        if (!this.scanner.hasNextLine()) {
            this.running = false;
            return;
        }
        String sectionId = this.scanner.nextLine().trim();
        List<Seat> availableSeats = this.ticketController.getAvailableSeatsForMatch(selectedMatch.getMatchId(),
                sectionId);
        if (availableSeats.isEmpty()) {
            System.out.println("[FAILED] No available tickets for the selected match and section. "
                    + "Please check that ticket pricing is configured.");
            return;
        }
        this.bookingView.displayBookingForm(availableSeats, this.currentUser.getId(), selectedMatch.getMatchId());
    }

    public void handleOrganizerChoice(String choice) {
        if ("1".equals(choice)) {
            this.matchView.displayMatches(this.matchController.viewAllMatches());
        } else if ("2".equals(choice)) {
            this.matchView.displayCreateMatchForm();
        } else if ("3".equals(choice)) {
            this.matchView.displayUpdateMatchForm();
        } else if ("4".equals(choice)) {
            System.out.print("Enter match ID to delete: ");
            String matchId = this.scanner.nextLine().trim();
            boolean ok = this.matchController.deleteMatch(matchId);
            if (ok) {
                System.out.println("[SUCCESS] Match " + matchId + " deleted successfully!");
            } else {
                System.out.println("[FAILED] Failed to delete match. Match ID does not exist.");
            }
        } else if ("5".equals(choice)) {
            this.organizerView.displayPricingConfigForm();
        } else if ("6".equals(choice)) {
            this.organizerView.displayGenerateTicketsForm();
        } else if ("7".equals(choice)) {
            this.organizerView.displayCancelTicketsForm();
        } else if ("8".equals(choice)) {
            System.out.print("Enter match ID to view sales report (e.g., MTH01): ");
            String matchId = this.scanner.nextLine().trim();
            Map<String, Object> sales = this.ticketController.viewTicketSalesReport(matchId);
            this.organizerView.displaySalesReport(sales);
        } else if ("9".equals(choice)) {
            Account acc = this.authController.viewProfile(this.currentUser.getId());
            this.authView.displayProfile(acc);
        } else if ("10".equals(choice)) {
            this.authView.displayUpdateProfileForm();
        } else if ("11".equals(choice)) {
            System.out.println("\n============= CHANGE PASSWORD =============");
            System.out.print("Current password: ");
            String oldPass = this.scanner.nextLine().trim();
            System.out.print("New password    : ");
            String newPass = this.scanner.nextLine().trim();
            boolean ok = this.authController.changePassword(this.currentUser.getId(), oldPass, newPass);
            if (ok) {
                System.out.println("[SUCCESS] Password changed successfully!");
            } else {
                System.out.println("[FAILED] Password change failed.");
            }
        } else if ("0".equals(choice)) {
            this.authController.logout(this.currentUser.getId());
            this.currentUser = null;
            System.out.println("[INFO] Logged out successfully.");
        } else {
            System.out.println("[WARNING] Invalid option, please try again.");
        }
    }

    public void handleAdminChoice(String choice) {
        if ("1".equals(choice)) {
            this.adminView.displayFanManagementMenu();
        } else if ("2".equals(choice)) {
            this.adminView.displayOrganizerManagementMenu();
        } else if ("3".equals(choice)) {
            this.adminView.displaySeatManagementMenu();
        } else if ("4".equals(choice)) {
            this.adminView.displayBenchmarkMenu();
        } else if ("5".equals(choice)) {
            Account acc = this.authController.viewProfile(this.currentUser.getId());
            if (acc != null) {
                String border = "+-------------------------------------------------------------------------+";
                System.out.println();
                System.out.println(border);
                System.out.println("|                              ADMIN PROFILE                              |");
                System.out.println(border);
                System.out.printf("| Admin ID     : %-56s |%n", acc.getId());
                System.out.printf("| Username     : %-56s |%n", acc.getUsername());
                System.out.printf("| Email        : %-56s |%n", (acc.getEmail() != null ? acc.getEmail() : "N/A"));
                System.out.printf("| Phone        : %-56s |%n", (acc.getPhone() != null ? acc.getPhone() : "N/A"));
                System.out.println(border);
            }
        } else if ("6".equals(choice)) {
            System.out.println("\n============= CHANGE PASSWORD =============");
            System.out.print("Current password: ");
            String oldPass = this.scanner.nextLine().trim();
            System.out.print("New password    : ");
            String newPass = this.scanner.nextLine().trim();
            boolean ok = this.authController.changePassword(this.currentUser.getId(), oldPass, newPass);
            if (ok) {
                System.out.println("[SUCCESS] Password changed successfully!");
            } else {
                System.out.println("[FAILED] Password change failed.");
            }
        } else if ("0".equals(choice)) {
            this.authController.logout(this.currentUser.getId());
            this.currentUser = null;
            System.out.println("[INFO] Logged out successfully.");
        } else {
            System.out.println("[WARNING] Invalid option, please try again.");
        }
    }

    public Account getCurrentUser() {
        return this.currentUser;
    }

    public void setCurrentUser(Account currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public AuthController getAuthController() {
        return this.authController;
    }

    public AdminController getAdminController() {
        return this.adminController;
    }

    public MatchController getMatchController() {
        return this.matchController;
    }

    public BookingController getBookingController() {
        return this.bookingController;
    }

    public StadiumController getStadiumController() {
        return this.stadiumController;
    }

    public TicketController getTicketController() {
        return this.ticketController;
    }

    public MainView getMainView() {
        return this.mainView;
    }

    public MatchView getMatchView() {
        return this.matchView;
    }

    public BookingView getBookingView() {
        return this.bookingView;
    }

    public OrganizerView getOrganizerView() {
        return this.organizerView;
    }

    public SeatMapView getSeatMapView() {
        return this.seatMapView;
    }

    public StadiumView getStadiumView() {
        return this.stadiumView;
    }

    public AdminView getAdminView() {
        return this.adminView;
    }

    public AuthView getAuthView() {
        return this.authView;
    }

    public Scanner getScanner() {
        return this.scanner;
    }
}

