package org.stadium.view;

import org.stadium.controller.AuthController;
import org.stadium.model.Account;
import org.stadium.model.Admin;
import org.stadium.model.Fan;
import org.stadium.model.Organizer;

import java.util.Scanner;

public class AuthView {

    private final AuthController authController;
    private final Scanner scanner;

    public AuthView(AuthController authController, Scanner scanner) {
        this.authController = authController;
        this.scanner = scanner;
    }

    public AuthView(AuthController authController) {
        this(authController, new Scanner(System.in));
    }

    public AuthView() {
        this(new AuthController(), new Scanner(System.in));
    }

    public void displayLoginForm() {
        String border = "+-------------------------------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                              SYSTEM LOGIN                               |");
        System.out.println(border);
        System.out.print("Enter username: ");
        String username = this.scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = this.scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("[FAILED] Username and password cannot be empty!");
            return;
        }

        Account account = this.authController.login(username, password);
        if (account != null) {
            System.out.println("[SUCCESS] Login successful! Welcome, " + this.getDisplayName(account) + "!");
            System.out.println("   Role: " + this.getRoleName(account));
        } else {
            String reason = this.authController.getLoginFailureReason(username);
            System.out.println("[FAILED] Login failed!");
            System.out.println("   Reason: " + reason);
        }
    }

    public void displayFanRegisterForm() {
        String border = "+-------------------------------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                          REGISTER FAN ACCOUNT                           |");
        System.out.println(border);
        System.out.print("Enter username: ");
        String username = this.scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = this.scanner.nextLine().trim();
        System.out.print("Confirm password: ");
        String confirmPass = this.scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("[FAILED] Username and password cannot be empty!");
            return;
        }

        if (!password.equals(confirmPass)) {
            System.out.println("[FAILED] Password confirmation does not match! Please try again.");
            return;
        }

        System.out.print("Enter full name: ");
        String fullName = this.scanner.nextLine().trim();
        System.out.print("Enter email address: ");
        String email = this.scanner.nextLine().trim();
        System.out.print("Enter phone number: ");
        String phone = this.scanner.nextLine().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            System.out.println("[FAILED] Fields cannot be empty!");
            return;
        }

        boolean success = this.authController.registerFan(username, password, fullName, email, phone);
        if (success) {
            System.out.println("[SUCCESS] Fan account registered successfully! You can now log in.");
        } else {
            System.out.println("[FAILED] Registration failed! Username may already exist or data is invalid.");
        }
    }

    public void displayOrganizerRegisterForm() {
        String border = "+-------------------------------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                       REGISTER ORGANIZER ACCOUNT                        |");
        System.out.println(border);
        System.out.print("Enter username: ");
        String username = this.scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = this.scanner.nextLine().trim();
        System.out.print("Confirm password: ");
        String confirmPass = this.scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("[FAILED] Username and password cannot be empty!");
            return;
        }

        if (!password.equals(confirmPass)) {
            System.out.println("[FAILED] Password confirmation does not match! Please try again.");
            return;
        }

        System.out.print("Enter organization name: ");
        String orgName = this.scanner.nextLine().trim();
        System.out.print("Enter email address: ");
        String email = this.scanner.nextLine().trim();
        System.out.print("Enter phone number: ");
        String phone = this.scanner.nextLine().trim();

        if (orgName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            System.out.println("[FAILED] Fields cannot be empty!");
            return;
        }

        boolean success = this.authController.registerOrganizer(username, password, orgName, email, phone);
        if (success) {
            System.out.println("[SUCCESS] Organizer account registered successfully!");
            System.out.println("[INFO] Account status is PENDING — Please wait for Admin approval before logging in.");
        } else {
            System.out.println("[FAILED] Registration failed! Username may already exist or data is invalid.");
        }
    }

    public void displayProfile(Account account) {
        String border = "+-------------------------------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                              USER PROFILE                               |");
        System.out.println(border);
        if (account == null) {
            System.out.println("| [WARNING] No account information to display.                            |");
            System.out.println(border);
            return;
        }

        System.out.printf("| Account ID   : %-56s |%n", this.padOrTruncate(account.getId(), 56));
        System.out.printf("| Username     : %-56s |%n", this.padOrTruncate(account.getUsername(), 56));
        System.out.printf("| Email        : %-56s |%n", this.padOrTruncate(account.getEmail(), 56));
        System.out.printf("| Phone        : %-56s |%n", this.padOrTruncate(account.getPhone(), 56));
        System.out.printf("| Status       : %-56s |%n",
                this.padOrTruncate(account.getStatus() != null ? account.getStatus().name() : "N/A", 56));

        if (account instanceof Fan) {
            Fan fan = (Fan) account;
            System.out.printf("| Full Name    : %-56s |%n", this.padOrTruncate(fan.getFullName(), 56));
            System.out.printf("| Member Tier  : %-56s |%n",
                    this.padOrTruncate(fan.getFanType() != null ? fan.getFanType() : "N/A", 56));
            System.out.printf("| Role         : %-56s |%n", this.padOrTruncate("FAN", 56));
        } else if (account instanceof Organizer) {
            Organizer org = (Organizer) account;
            System.out.printf("| Organization : %-56s |%n", this.padOrTruncate(org.getOrgName(), 56));
            System.out.printf("| Role         : %-56s |%n", this.padOrTruncate("ORGANIZER", 56));
        } else if (account instanceof Admin) {
            Admin admin = (Admin) account;
            System.out.printf("| Full Name    : %-56s |%n", this.padOrTruncate(admin.getFullName(), 56));
            System.out.printf("| Role         : %-56s |%n", this.padOrTruncate("ADMIN", 56));
        }

        System.out.println(border);

        System.out.println("\n [1] Update contact info (Email & Phone)");
        System.out.println(" [0] Back");
        System.out.print("Enter choice: ");
        String choice = this.scanner.nextLine().trim();
        if ("1".equals(choice)) {
            this.displayUpdateProfileForm();
        }
    }

    public void displayChangePasswordForm() {
        String border = "+-------------------------------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                             CHANGE PASSWORD                             |");
        System.out.println(border);

        Account currentUser = this.authController.getCurrentUser();
        if (currentUser == null) {
            System.out.println("[FAILED] You are not logged in! Cannot change password.");
            return;
        }

        System.out.print("Enter current password: ");
        String oldPass = this.scanner.nextLine().trim();
        System.out.print("Enter new password: ");
        String newPass = this.scanner.nextLine().trim();
        System.out.print("Confirm new password: ");
        String confirmPass = this.scanner.nextLine().trim();

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            System.out.println("[FAILED] Password cannot be empty!");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            System.out.println("[FAILED] New password and confirmation do not match!");
            return;
        }

        boolean success = this.authController.changePassword(currentUser.getId(), oldPass, newPass);
        if (success) {
            System.out.println("[SUCCESS] Password changed successfully!");
        } else {
            System.out.println("[FAILED] Password change failed! Current password incorrect.");
        }
    }

    public void displayUpdateProfileForm() {
        String border = "+-------------------------------------------------------------------------+";
        System.out.println();
        System.out.println(border);
        System.out.println("|                       UPDATE CONTACT INFORMATION                        |");
        System.out.println(border);

        Account currentUser = this.authController.getCurrentUser();
        if (currentUser == null) {
            System.out.println("[FAILED] You are not logged in!");
            return;
        }

        System.out.print("Enter new email: ");
        String newEmail = this.scanner.nextLine().trim();
        System.out.print("Enter new phone: ");
        String newPhone = this.scanner.nextLine().trim();

        if (newEmail.isEmpty() || newPhone.isEmpty()) {
            System.out.println("[FAILED] Email and phone cannot be empty!");
            return;
        }

        boolean success = this.authController.updateProfile(currentUser.getId(), newEmail, newPhone);
        if (success) {
            System.out.println("[SUCCESS] Contact information updated successfully!");
        } else {
            System.out.println("[FAILED] Update failed!");
        }
    }

    private String getDisplayName(Account account) {
        if (account instanceof Fan) {
            return ((Fan) account).getFullName();
        } else if (account instanceof Organizer) {
            return ((Organizer) account).getOrgName();
        } else if (account instanceof Admin) {
            return ((Admin) account).getFullName();
        }
        return account.getUsername();
    }

    private String getRoleName(Account account) {
        if (account instanceof Admin) {
            return "ADMIN";
        } else if (account instanceof Organizer) {
            return "ORGANIZER";
        } else if (account instanceof Fan) {
            return "FAN";
        }
        return "UNKNOWN";
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
