package org.stadium.view;

import org.stadium.controller.AdminController;
import org.stadium.model.AccountSearchCriteria;
import org.stadium.model.AccountStatus;
import org.stadium.model.Fan;
import org.stadium.model.Organizer;
import org.stadium.model.Seat;
import org.stadium.model.UserRole;
import org.stadium.model.enums.SeatStatus;
import org.stadium.model.enums.SeatType;

import java.util.List;
import java.util.Scanner;

public class AdminView {

    private final AdminController adminController;
    private final Scanner scanner;

    public AdminView(AdminController adminController, Scanner scanner) {
        this.adminController = adminController;
        this.scanner = scanner;
    }

    public AdminView(AdminController adminController) {
        this(adminController, new Scanner(System.in));
    }

    public AdminView() {
        this(new AdminController(), new Scanner(System.in));
    }

    public void displayFanManagementMenu() {
        boolean inFanMenu = true;
        while (inFanMenu) {
            System.out.println();
            System.out.println("+-------------------------------------------------------------------------+");
            System.out.println("|                         MANAGE FAN ACCOUNTS                             |");
            System.out.println("+-------------------------------------------------------------------------+");
            System.out.println("  1. Approve / Reject Fan Account");
            System.out.println("  2. Search Fan Accounts");
            System.out.println("  3. Delete Fan Account");
            System.out.println("  0. Back to Admin Menu");
            System.out.println("+-------------------------------------------------------------------------+");
            System.out.print("Please choose an option (0-3): ");

            if (!this.scanner.hasNextLine()) {
                break;
            }
            String choice = this.scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    this.handleApproveOrRejectFan();
                    break;
                case "2":
                    this.handleSearchFans();
                    break;
                case "3":
                    this.handleDeleteFan();
                    break;
                case "0":
                    inFanMenu = false;
                    break;
                default:
                    System.out.println("[FAILED] Invalid choice! Please enter 0-3.");
            }
        }
    }

    public void displayOrganizerManagementMenu() {
        boolean inOrgMenu = true;
        while (inOrgMenu) {
            System.out.println();
            System.out.println("+-------------------------------------------------------------------------+");
            System.out.println("|                      MANAGE ORGANIZER ACCOUNTS                          |");
            System.out.println("+-------------------------------------------------------------------------+");
            System.out.println("  1. Approve / Reject Organizer Account");
            System.out.println("  2. Search Organizer Accounts");
            System.out.println("  3. Delete Organizer Account");
            System.out.println("  0. Back to Admin Menu");
            System.out.println("+-------------------------------------------------------------------------+");
            System.out.print("Please choose an option (0-3): ");

            if (!this.scanner.hasNextLine()) {
                break;
            }
            String choice = this.scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    this.handleApproveOrRejectOrganizer();
                    break;
                case "2":
                    this.handleSearchOrganizers();
                    break;
                case "3":
                    this.handleDeleteOrganizer();
                    break;
                case "0":
                    inOrgMenu = false;
                    break;
                default:
                    System.out.println("[FAILED] Invalid choice! Please enter 0-3.");
            }
        }
    }

    public void displaySeatManagementMenu() {
        boolean inSeatMenu = true;
        while (inSeatMenu) {
            System.out.println();
            System.out.println("+-------------------------------------------------------------------------+");
            System.out.println("|                         MANAGE STADIUM SEATS                            |");
            System.out.println("+-------------------------------------------------------------------------+");
            System.out.println("  1. Create Seat");
            System.out.println("  2. Update Seat");
            System.out.println("  3. Search Seats");
            System.out.println("  4. Delete Seat");
            System.out.println("  0. Back to Admin Menu");
            System.out.println("+-------------------------------------------------------------------------+");
            System.out.print("Please choose an option (0-4): ");

            if (!this.scanner.hasNextLine()) {
                break;
            }
            String choice = this.scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    this.handleCreateSeat();
                    break;
                case "2":
                    this.handleUpdateSeat();
                    break;
                case "3":
                    this.handleSearchSeats();
                    break;
                case "4":
                    this.handleDeleteSeat();
                    break;
                case "0":
                    inSeatMenu = false;
                    break;
                default:
                    System.out.println("[FAILED] Invalid choice! Please enter 0-4.");
            }
        }
    }

    public void displayBenchmarkMenu() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|               CONCURRENCY BENCHMARK & SIMULATION SYSTEM                 |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("[INFO] Benchmark feature is scheduled for Week 8 milestone (Coming Soon).");
        System.out.println("+-------------------------------------------------------------------------+");
    }

    public void displayPendingAccounts(List<Organizer> orgs, List<Fan> fans) {
        this.displayPendingOrganizers(orgs);
        this.displayPendingFans(fans);
    }

    // =========================================================================
    // FAN HANDLERS
    // =========================================================================

    public void handleApproveOrRejectFan() {
        List<Fan> pendingList = this.adminController.getPendingFans();
        this.displayPendingFans(pendingList);

        if (pendingList == null || pendingList.isEmpty()) {
            return;
        }

        System.out.print("Enter Fan ID to process (or '0' to cancel): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String fanId = this.scanner.nextLine().trim();
        if (fanId.isEmpty() || "0".equals(fanId)) {
            return;
        }

        System.out.print("Do you want to (A)pprove or (R)eject this account? [A/R]: ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String decision = this.scanner.nextLine().trim().toUpperCase();

        if ("A".equals(decision)) {
            boolean ok = this.adminController.approveFanAccount(fanId);
            if (ok) {
                System.out.println("[SUCCESS] Fan account " + fanId + " has been approved (ACTIVE).");
            } else {
                System.out.println("[FAILED] Fan account not found or not in PENDING status: " + fanId);
            }
        } else if ("R".equals(decision)) {
            boolean ok = this.adminController.rejectFanAccount(fanId);
            if (ok) {
                System.out.println("[SUCCESS] Fan account " + fanId + " has been rejected (REJECTED).");
            } else {
                System.out.println("[FAILED] Fan account not found or not in PENDING status: " + fanId);
            }
        } else {
            System.out.println("[FAILED] Invalid choice! Please enter 'A' or 'R'.");
        }
    }

    public void handleSearchFans() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                           SEARCH FAN ACCOUNTS                           |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter keyword (Username, ID, or Full Name - Enter to skip): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String keyword = this.scanner.nextLine().trim();

        System.out.println("Filter by status:");
        System.out.println("  1. ALL");
        System.out.println("  2. PENDING");
        System.out.println("  3. ACTIVE");
        System.out.println("  4. REJECTED");
        System.out.println("  5. BLOCKED");
        System.out.print("Choose status (1-5) [Default 1]: ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String statusChoice = this.scanner.nextLine().trim();
        AccountStatus status = null;
        if ("2".equals(statusChoice)) {
            status = AccountStatus.PENDING;
        } else if ("3".equals(statusChoice)) {
            status = AccountStatus.ACTIVE;
        } else if ("4".equals(statusChoice)) {
            status = AccountStatus.REJECTED;
        } else if ("5".equals(statusChoice)) {
            status = AccountStatus.BLOCKED;
        }

        AccountSearchCriteria criteria = new AccountSearchCriteria(keyword, UserRole.FAN, status);
        List<Fan> results = this.adminController.searchFanAccounts(criteria);
        this.displayFansTable(results);
    }

    public void handleDeleteFan() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                           DELETE FAN ACCOUNT                            |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter Fan ID to delete (e.g., FAN0001): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String fanId = this.scanner.nextLine().trim();
        if (fanId.isEmpty()) {
            System.out.println("[FAILED] Fan ID cannot be empty!");
            return;
        }

        System.out.print("Are you sure you want to delete fan account " + fanId + "? [Y/N]: ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String confirm = this.scanner.nextLine().trim().toUpperCase();
        if (!"Y".equals(confirm)) {
            System.out.println("[INFO] Delete operation cancelled.");
            return;
        }

        boolean ok = this.adminController.deleteFanAccount(fanId);
        if (ok) {
            System.out.println("[SUCCESS] Fan account " + fanId + " has been deleted successfully.");
        } else {
            System.out.println("[FAILED] Fan account not found: " + fanId);
        }
    }

    // =========================================================================
    // ORGANIZER HANDLERS
    // =========================================================================

    public void handleApproveOrRejectOrganizer() {
        List<Organizer> pendingList = this.adminController.getPendingOrganizers();
        this.displayPendingOrganizers(pendingList);

        if (pendingList == null || pendingList.isEmpty()) {
            return;
        }

        System.out.print("Enter Organizer ID to process (or '0' to cancel): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String orgId = this.scanner.nextLine().trim();
        if (orgId.isEmpty() || "0".equals(orgId)) {
            return;
        }

        System.out.print("Do you want to (A)pprove or (R)eject this account? [A/R]: ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String decision = this.scanner.nextLine().trim().toUpperCase();

        if ("A".equals(decision)) {
            boolean ok = this.adminController.approveOrganizerAccount(orgId);
            if (ok) {
                System.out.println("[SUCCESS] Organizer account " + orgId + " has been approved (ACTIVE).");
            } else {
                System.out.println("[FAILED] Organizer account not found or not in PENDING status: " + orgId);
            }
        } else if ("R".equals(decision)) {
            boolean ok = this.adminController.rejectOrganizerAccount(orgId);
            if (ok) {
                System.out.println("[SUCCESS] Organizer account " + orgId + " has been rejected (REJECTED).");
            } else {
                System.out.println("[FAILED] Organizer account not found or not in PENDING status: " + orgId);
            }
        } else {
            System.out.println("[FAILED] Invalid choice! Please enter 'A' or 'R'.");
        }
    }

    public void handleSearchOrganizers() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                        SEARCH ORGANIZER ACCOUNTS                        |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter keyword (Username, ID, or Org Name - Enter to skip): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String keyword = this.scanner.nextLine().trim();

        System.out.println("Filter by status:");
        System.out.println("  1. ALL");
        System.out.println("  2. PENDING");
        System.out.println("  3. ACTIVE");
        System.out.println("  4. REJECTED");
        System.out.println("  5. BLOCKED");
        System.out.print("Choose status (1-5) [Default 1]: ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String statusChoice = this.scanner.nextLine().trim();
        AccountStatus status = null;
        if ("2".equals(statusChoice)) {
            status = AccountStatus.PENDING;
        } else if ("3".equals(statusChoice)) {
            status = AccountStatus.ACTIVE;
        } else if ("4".equals(statusChoice)) {
            status = AccountStatus.REJECTED;
        } else if ("5".equals(statusChoice)) {
            status = AccountStatus.BLOCKED;
        }

        AccountSearchCriteria criteria = new AccountSearchCriteria(keyword, UserRole.ORGANIZER, status);
        List<Organizer> results = this.adminController.searchOrganizerAccounts(criteria);
        this.displayOrganizersTable(results);
    }

    public void handleDeleteOrganizer() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                        DELETE ORGANIZER ACCOUNT                         |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter Organizer ID to delete (e.g., ORG01): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String orgId = this.scanner.nextLine().trim();
        if (orgId.isEmpty()) {
            System.out.println("[FAILED] Organizer ID cannot be empty!");
            return;
        }

        System.out.print("Are you sure you want to delete organizer account " + orgId + "? [Y/N]: ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String confirm = this.scanner.nextLine().trim().toUpperCase();
        if (!"Y".equals(confirm)) {
            System.out.println("[INFO] Delete operation cancelled.");
            return;
        }

        boolean ok = this.adminController.deleteOrganizerAccount(orgId);
        if (ok) {
            System.out.println("[SUCCESS] Organizer account " + orgId + " has been deleted successfully.");
        } else {
            System.out.println("[FAILED] Organizer account not found: " + orgId);
        }
    }

    // =========================================================================
    // SEAT HANDLERS
    // =========================================================================

    public void handleCreateSeat() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                            CREATE NEW SEAT                              |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter Section ID (e.g., SEC_STD01_A): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String sectionId = this.scanner.nextLine().trim();
        if (sectionId.isEmpty()) {
            System.out.println("[FAILED] Section ID cannot be empty!");
            return;
        }

        System.out.print("Enter Row Number (e.g., 1): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String rowStr = this.scanner.nextLine().trim();
        int rowNumber;
        try {
            rowNumber = Integer.parseInt(rowStr);
            if (rowNumber <= 0) {
                System.out.println("[FAILED] Row number must be positive!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("[FAILED] Invalid row number format!");
            return;
        }

        System.out.print("Enter Seat Number (e.g., S15 or R01-S15): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String seatNum = this.scanner.nextLine().trim();
        if (seatNum.isEmpty()) {
            System.out.println("[FAILED] Seat number cannot be empty!");
            return;
        }

        System.out.println("Select Seat Type:");
        System.out.println("  1. STANDARD");
        System.out.println("  2. VIP");
        System.out.println("  3. ECONOMY");
        System.out.println("  4. STANDING");
        System.out.print("Choose seat type (1-4) [Default 1]: ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String typeChoice = this.scanner.nextLine().trim();
        SeatType seatType = SeatType.STANDARD;
        if ("2".equals(typeChoice)) {
            seatType = SeatType.VIP;
        } else if ("3".equals(typeChoice)) {
            seatType = SeatType.ECONOMY;
        } else if ("4".equals(typeChoice)) {
            seatType = SeatType.STANDING;
        }

        boolean ok = this.adminController.createSeat(sectionId, rowNumber, seatNum, seatType);
        if (ok) {
            System.out.println("[SUCCESS] Seat created successfully in section " + sectionId + ".");
        } else {
            System.out.println("[FAILED] Failed to create seat. Seat ID may already exist or input is invalid.");
        }
    }

    public void handleUpdateSeat() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                               UPDATE SEAT                               |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter Seat ID to update (e.g., SEC_STD01_A_R01_S01): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String seatId = this.scanner.nextLine().trim();
        if (seatId.isEmpty()) {
            System.out.println("[FAILED] Seat ID cannot be empty!");
            return;
        }

        System.out.println("Select New Seat Type (Enter to skip):");
        System.out.println("  1. STANDARD");
        System.out.println("  2. VIP");
        System.out.println("  3. ECONOMY");
        System.out.println("  4. STANDING");
        System.out.print("Choose seat type (1-4, Enter to skip): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String typeChoice = this.scanner.nextLine().trim();
        SeatType newType = null;
        if ("1".equals(typeChoice)) {
            newType = SeatType.STANDARD;
        } else if ("2".equals(typeChoice)) {
            newType = SeatType.VIP;
        } else if ("3".equals(typeChoice)) {
            newType = SeatType.ECONOMY;
        } else if ("4".equals(typeChoice)) {
            newType = SeatType.STANDING;
        }

        System.out.println("Select New Seat Status (Enter to skip):");
        System.out.println("  1. AVAILABLE");
        System.out.println("  2. BOOKED");
        System.out.println("  3. LOCKED");
        System.out.print("Choose status (1-3, Enter to skip): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String statusChoice = this.scanner.nextLine().trim();
        SeatStatus newStatus = null;
        if ("1".equals(statusChoice)) {
            newStatus = SeatStatus.AVAILABLE;
        } else if ("2".equals(statusChoice)) {
            newStatus = SeatStatus.BOOKED;
        } else if ("3".equals(statusChoice)) {
            newStatus = SeatStatus.LOCKED;
        }

        if (newType == null && newStatus == null) {
            System.out.println("[INFO] No changes specified. Update aborted.");
            return;
        }

        boolean ok = this.adminController.updateSeat(seatId, newType, newStatus);
        if (ok) {
            System.out.println("[SUCCESS] Seat " + seatId + " updated successfully.");
        } else {
            System.out.println("[FAILED] Seat not found: " + seatId);
        }
    }

    public void handleSearchSeats() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                              SEARCH SEATS                               |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter Section ID (e.g., SEC_STD01_A - Enter to search all): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String sectionId = this.scanner.nextLine().trim();

        System.out.println("Filter by Seat Type:");
        System.out.println("  1. ALL");
        System.out.println("  2. STANDARD");
        System.out.println("  3. VIP");
        System.out.println("  4. ECONOMY");
        System.out.println("  5. STANDING");
        System.out.print("Choose seat type (1-5) [Default 1]: ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String typeChoice = this.scanner.nextLine().trim();
        SeatType seatType = null;
        if ("2".equals(typeChoice)) {
            seatType = SeatType.STANDARD;
        } else if ("3".equals(typeChoice)) {
            seatType = SeatType.VIP;
        } else if ("4".equals(typeChoice)) {
            seatType = SeatType.ECONOMY;
        } else if ("5".equals(typeChoice)) {
            seatType = SeatType.STANDING;
        }

        System.out.println("Filter by Seat Status:");
        System.out.println("  1. ALL");
        System.out.println("  2. AVAILABLE");
        System.out.println("  3. BOOKED");
        System.out.println("  4. LOCKED");
        System.out.print("Choose status (1-4) [Default 1]: ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String statusChoice = this.scanner.nextLine().trim();
        SeatStatus seatStatus = null;
        if ("2".equals(statusChoice)) {
            seatStatus = SeatStatus.AVAILABLE;
        } else if ("3".equals(statusChoice)) {
            seatStatus = SeatStatus.BOOKED;
        } else if ("4".equals(statusChoice)) {
            seatStatus = SeatStatus.LOCKED;
        }

        List<Seat> results = this.adminController.searchSeat(sectionId, seatType, seatStatus);
        this.displaySeatsTable(results);
    }

    public void handleDeleteSeat() {
        System.out.println();
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.println("|                               DELETE SEAT                               |");
        System.out.println("+-------------------------------------------------------------------------+");
        System.out.print("Enter Seat ID to delete (e.g., SEC_STD01_A_R01_S01): ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String seatId = this.scanner.nextLine().trim();
        if (seatId.isEmpty()) {
            System.out.println("[FAILED] Seat ID cannot be empty!");
            return;
        }

        System.out.print("Are you sure you want to delete seat " + seatId + "? [Y/N]: ");
        if (!this.scanner.hasNextLine()) {
            return;
        }
        String confirm = this.scanner.nextLine().trim().toUpperCase();
        if (!"Y".equals(confirm)) {
            System.out.println("[INFO] Delete operation cancelled.");
            return;
        }

        boolean ok = this.adminController.deleteSeat(seatId);
        if (ok) {
            System.out.println("[SUCCESS] Seat " + seatId + " deleted successfully.");
        } else {
            System.out.println("[FAILED] Seat not found: " + seatId);
        }
    }

    // =========================================================================
    // TABLE PRESENTATION METHODS
    // =========================================================================

    public void displayPendingOrganizers(List<Organizer> pendingList) {
        String border = "+-----------------------------------------------------------------------------+";
        String gridBorder = "+----+--------+---------------+----------------------+------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                             PENDING ORGANIZERS                              |");
        System.out.println(border);
        if (pendingList == null || pendingList.isEmpty()) {
            System.out.println("| [INFO] No organizer accounts currently pending approval.                    |");
            System.out.println(border);
            return;
        }

        System.out.println(gridBorder);
        System.out.println("| #  | Org ID | Username      | Organization Name    | Email                  |");
        System.out.println(gridBorder);

        int index = 1;
        for (Organizer org : pendingList) {
            String num = this.padOrTruncate(String.valueOf(index++), 2);
            String id = this.padOrTruncate(org.getId(), 6);
            String user = this.padOrTruncate(org.getUsername(), 13);
            String name = this.padOrTruncate(org.getOrgName(), 20);
            String email = this.padOrTruncate(org.getEmail(), 22);

            System.out.printf("| %s | %s | %s | %s | %s |%n", num, id, user, name, email);
        }
        System.out.println(gridBorder);
        System.out.printf("| Total: %-68s |%n", pendingList.size() + " pending requests awaiting approval");
        System.out.println(border);
    }

    public void displayPendingFans(List<Fan> pendingList) {
        String border = "+-----------------------------------------------------------------------------+";
        String gridBorder = "+----+----------+---------------+----------------------+----------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                                PENDING FANS                                 |");
        System.out.println(border);
        if (pendingList == null || pendingList.isEmpty()) {
            System.out.println("| [INFO] No fan accounts currently pending approval.                          |");
            System.out.println(border);
            return;
        }

        System.out.println(gridBorder);
        System.out.println("| #  | Fan ID   | Username      | Full Name            | Email                |");
        System.out.println(gridBorder);

        int index = 1;
        for (Fan fan : pendingList) {
            String num = this.padOrTruncate(String.valueOf(index++), 2);
            String id = this.padOrTruncate(fan.getId(), 8);
            String user = this.padOrTruncate(fan.getUsername(), 13);
            String name = this.padOrTruncate(fan.getFullName(), 20);
            String email = this.padOrTruncate(fan.getEmail(), 20);

            System.out.printf("| %s | %s | %s | %s | %s |%n", num, id, user, name, email);
        }
        System.out.println(gridBorder);
        System.out.printf("| Total: %-68s |%n", pendingList.size() + " pending fan requests");
        System.out.println(border);
    }

    public void displayFansTable(List<Fan> fans) {
        String border = "+-----------------------------------------------------------------------------+";
        String gridBorder = "+----+----------+---------------+--------------------+------------+-----------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                                FAN ACCOUNTS                                 |");
        System.out.println(border);
        if (fans == null || fans.isEmpty()) {
            System.out.println("| [INFO] No fan accounts matched the search criteria.                         |");
            System.out.println(border);
            return;
        }

        System.out.println(gridBorder);
        System.out.println("| #  | Fan ID   | Username      | Full Name          | Phone      | Status    |");
        System.out.println(gridBorder);

        int index = 1;
        for (Fan fan : fans) {
            String num = this.padOrTruncate(String.valueOf(index++), 2);
            String id = this.padOrTruncate(fan.getId(), 8);
            String user = this.padOrTruncate(fan.getUsername(), 13);
            String name = this.padOrTruncate(fan.getFullName(), 18);
            String phone = this.padOrTruncate(fan.getPhone(), 10);
            String status = this.padOrTruncate(fan.getStatus() != null ? fan.getStatus().name() : "N/A", 9);

            System.out.printf("| %s | %s | %s | %s | %s | %s |%n", num, id, user, name, phone, status);
        }
        System.out.println(gridBorder);
        System.out.printf("| Total: %-68s |%n", fans.size() + " accounts found");
        System.out.println(border);
    }

    public void displayOrganizersTable(List<Organizer> orgs) {
        String border = "+-----------------------------------------------------------------------------+";
        String gridBorder = "+----+--------+---------------+--------------------+------------+-----------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                             ORGANIZER ACCOUNTS                              |");
        System.out.println(border);
        if (orgs == null || orgs.isEmpty()) {
            System.out.println("| [INFO] No organizer accounts matched the search criteria.                   |");
            System.out.println(border);
            return;
        }

        System.out.println(gridBorder);
        System.out.println("| #  | Org ID | Username      | Organization Name  | Phone      | Status    |");
        System.out.println(gridBorder);

        int index = 1;
        for (Organizer org : orgs) {
            String num = this.padOrTruncate(String.valueOf(index++), 2);
            String id = this.padOrTruncate(org.getId(), 6);
            String user = this.padOrTruncate(org.getUsername(), 13);
            String name = this.padOrTruncate(org.getOrgName(), 18);
            String phone = this.padOrTruncate(org.getPhone(), 10);
            String status = this.padOrTruncate(org.getStatus() != null ? org.getStatus().name() : "N/A", 9);

            System.out.printf("| %s | %s | %s | %s | %s | %s |%n", num, id, user, name, phone, status);
        }
        System.out.println(gridBorder);
        System.out.printf("| Total: %-68s |%n", orgs.size() + " accounts found");
        System.out.println(border);
    }

    public void displaySeatsTable(List<Seat> seats) {
        String border = "+-----------------------------------------------------------------------------+";
        String gridBorder = "+----+-----------------------+-------------+---+---------+----------+---------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                                STADIUM SEATS                                |");
        System.out.println(border);
        if (seats == null || seats.isEmpty()) {
            System.out.println("| [INFO] No seats matched the search criteria.                                |");
            System.out.println(border);
            return;
        }

        // Limit display to top 50 if result is very large to keep terminal clean
        int displayLimit = Math.min(seats.size(), 50);

        System.out.println(gridBorder);
        System.out.println("| #  | Seat ID               | Section ID  |Row| SeatNum | Type     | Status  |");
        System.out.println(gridBorder);

        for (int i = 0; i < displayLimit; i++) {
            Seat s = seats.get(i);
            String num = this.padOrTruncate(String.valueOf(i + 1), 2);
            String id = this.padOrTruncate(s.getId(), 21);
            String sec = this.padOrTruncate(s.getSectionId(), 11);
            String row = this.padOrTruncate(String.valueOf(s.getRowNumber()), 1);
            String seatNum = this.padOrTruncate(s.getSeatNumber(), 7);
            String type = this.padOrTruncate(s.getSeatType() != null ? s.getSeatType().name() : "N/A", 8);
            String status = this.padOrTruncate(s.getStatus() != null ? s.getStatus().name() : "N/A", 7);

            System.out.printf("| %s | %s | %s | %s | %s | %s | %s |%n", num, id, sec, row, seatNum, type, status);
        }
        System.out.println(gridBorder);
        if (seats.size() > displayLimit) {
            System.out.printf("| Showing first 50 of %-47s |%n", seats.size() + " seats");
        } else {
            System.out.printf("| Total: %-68s |%n", seats.size() + " seats found");
        }
        System.out.println(border);
    }

    public String padOrTruncate(String text, int width) {
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
