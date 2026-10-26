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
        System.out.println();
        System.out.println("========================== ĐĂNG NHẬP HỆ THỐNG ==========================");
        System.out.print("👉 Nhập tên đăng nhập (Username): ");
        String username = this.scanner.nextLine().trim();
        System.out.print("👉 Nhập mật khẩu (Password): ");
        String password = this.scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Tên đăng nhập và mật khẩu không được để trống!");
            return;
        }

        Account account = this.authController.login(username, password);
        if (account != null) {
            System.out.println("✅ Đăng nhập thành công! Xin chào, " + this.getDisplayName(account) + "!");
            System.out.println("   Vai trò: " + this.getRoleName(account));
        } else {
            String reason = this.authController.getLoginFailureReason(username);
            System.out.println("❌ Đăng nhập thất bại!");
            System.out.println("   Lý do: " + reason);
        }
    }

    public void displayFanRegisterForm() {
        System.out.println();
        System.out.println("====================== ĐĂNG KÝ TÀI KHOẢN FAN ======================");
        System.out.print("👉 Nhập tên đăng nhập (Username): ");
        String username = this.scanner.nextLine().trim();
        System.out.print("👉 Nhập mật khẩu: ");
        String password = this.scanner.nextLine().trim();
        System.out.print("👉 Xác nhận mật khẩu: ");
        String confirmPass = this.scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Tên đăng nhập và mật khẩu không được để trống!");
            return;
        }

        if (!password.equals(confirmPass)) {
            System.out.println("❌ Mật khẩu xác nhận không khớp! Vui lòng thử lại.");
            return;
        }

        System.out.print("👉 Nhập họ và tên đầy đủ: ");
        String fullName = this.scanner.nextLine().trim();
        System.out.print("👉 Nhập địa chỉ email: ");
        String email = this.scanner.nextLine().trim();
        System.out.print("👉 Nhập số điện thoại: ");
        String phone = this.scanner.nextLine().trim();

        if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            System.out.println("❌ Thông tin không được để trống!");
            return;
        }

        boolean success = this.authController.registerFan(username, password, fullName, email, phone);
        if (success) {
            System.out.println("🎉 Đăng ký tài khoản Fan thành công! Bạn có thể đăng nhập ngay.");
        } else {
            System.out.println("❌ Đăng ký thất bại! Tên đăng nhập đã tồn tại hoặc dữ liệu không hợp lệ.");
        }
    }

    public void displayOrganizerRegisterForm() {
        System.out.println();
        System.out.println("================ ĐĂNG KÝ TÀI KHOẢN BAN TỔ CHỨC ================");
        System.out.print("👉 Nhập tên đăng nhập (Username): ");
        String username = this.scanner.nextLine().trim();
        System.out.print("👉 Nhập mật khẩu: ");
        String password = this.scanner.nextLine().trim();
        System.out.print("👉 Xác nhận mật khẩu: ");
        String confirmPass = this.scanner.nextLine().trim();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("❌ Tên đăng nhập và mật khẩu không được để trống!");
            return;
        }

        if (!password.equals(confirmPass)) {
            System.out.println("❌ Mật khẩu xác nhận không khớp! Vui lòng thử lại.");
            return;
        }

        System.out.print("👉 Nhập tên đơn vị tổ chức: ");
        String orgName = this.scanner.nextLine().trim();
        System.out.print("👉 Nhập địa chỉ email: ");
        String email = this.scanner.nextLine().trim();
        System.out.print("👉 Nhập số điện thoại: ");
        String phone = this.scanner.nextLine().trim();

        if (orgName.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            System.out.println("❌ Thông tin không được để trống!");
            return;
        }

        boolean success = this.authController.registerOrganizer(username, password, orgName, email, phone);
        if (success) {
            System.out.println("🎉 Đăng ký tài khoản Ban tổ chức thành công!");
            System.out.println("ℹ️ Tài khoản đang ở trạng thái PENDING — Vui lòng chờ Admin phê duyệt trước khi đăng nhập.");
        } else {
            System.out.println("❌ Đăng ký thất bại! Tên đăng nhập đã tồn tại hoặc dữ liệu không hợp lệ.");
        }
    }

    public void displayProfile(Account account) {
        System.out.println();
        System.out.println("========================= THÔNG TIN HỒ SƠ CÁ NHÂN =========================");
        if (account == null) {
            System.out.println("⚠️ Không có thông tin tài khoản để hiển thị.");
            return;
        }

        System.out.printf("%-20s: %s%n", "Mã tài khoản", account.getId());
        System.out.printf("%-20s: %s%n", "Tên đăng nhập", account.getUsername());
        System.out.printf("%-20s: %s%n", "Email", account.getEmail());
        System.out.printf("%-20s: %s%n", "Số điện thoại", account.getPhone());
        System.out.printf("%-20s: %s%n", "Trạng thái", account.getStatus() != null ? account.getStatus().name() : "N/A");

        if (account instanceof Fan) {
            Fan fan = (Fan) account;
            System.out.printf("%-20s: %s%n", "Họ và tên", fan.getFullName());
            System.out.printf("%-20s: %s%n", "Hạng thành viên", fan.getFanType() != null ? fan.getFanType() : "N/A");
            System.out.printf("%-20s: %s%n", "Vai trò", "FAN");
        } else if (account instanceof Organizer) {
            Organizer org = (Organizer) account;
            System.out.printf("%-20s: %s%n", "Tên đơn vị", org.getOrgName());
            System.out.printf("%-20s: %s%n", "Vai trò", "ORGANIZER");
        } else if (account instanceof Admin) {
            Admin admin = (Admin) account;
            System.out.printf("%-20s: %s%n", "Họ và tên", admin.getFullName());
            System.out.printf("%-20s: %s%n", "Vai trò", "ADMIN");
        }

        System.out.println("=============================================================================");

        System.out.println("\n [1] Cập nhật thông tin liên hệ (Email & SĐT)");
        System.out.println(" [0] Quay lại");
        System.out.print("👉 Lựa chọn: ");
        String choice = this.scanner.nextLine().trim();
        if ("1".equals(choice)) {
            this.displayUpdateProfileForm();
        }
    }

    public void displayChangePasswordForm() {
        System.out.println();
        System.out.println("========================== ĐỔI MẬT KHẨU ==========================");

        Account currentUser = this.authController.getCurrentUser();
        if (currentUser == null) {
            System.out.println("❌ Bạn chưa đăng nhập! Không thể đổi mật khẩu.");
            return;
        }

        System.out.print("👉 Nhập mật khẩu hiện tại: ");
        String oldPass = this.scanner.nextLine().trim();
        System.out.print("👉 Nhập mật khẩu mới: ");
        String newPass = this.scanner.nextLine().trim();
        System.out.print("👉 Xác nhận mật khẩu mới: ");
        String confirmPass = this.scanner.nextLine().trim();

        if (oldPass.isEmpty() || newPass.isEmpty()) {
            System.out.println("❌ Mật khẩu không được để trống!");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            System.out.println("❌ Mật khẩu mới và xác nhận không khớp!");
            return;
        }

        boolean success = this.authController.changePassword(currentUser.getId(), oldPass, newPass);
        if (success) {
            System.out.println("✅ Đổi mật khẩu thành công!");
        } else {
            System.out.println("❌ Đổi mật khẩu thất bại! Mật khẩu hiện tại không đúng.");
        }
    }

    public void displayUpdateProfileForm() {
        System.out.println();
        System.out.println("==================== CẬP NHẬT THÔNG TIN LIÊN HỆ ====================");

        Account currentUser = this.authController.getCurrentUser();
        if (currentUser == null) {
            System.out.println("❌ Bạn chưa đăng nhập!");
            return;
        }

        System.out.print("👉 Nhập email mới: ");
        String newEmail = this.scanner.nextLine().trim();
        System.out.print("👉 Nhập số điện thoại mới: ");
        String newPhone = this.scanner.nextLine().trim();

        if (newEmail.isEmpty() || newPhone.isEmpty()) {
            System.out.println("❌ Email và số điện thoại không được để trống!");
            return;
        }

        boolean success = this.authController.updateProfile(currentUser.getId(), newEmail, newPhone);
        if (success) {
            System.out.println("✅ Cập nhật thông tin liên hệ thành công!");
        } else {
            System.out.println("❌ Cập nhật thất bại!");
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
            return "ADMIN (Quản trị viên)";
        } else if (account instanceof Organizer) {
            return "ORGANIZER (Ban tổ chức)";
        } else if (account instanceof Fan) {
            return "FAN (Người hâm mộ)";
        }
        return "UNKNOWN";
    }
}
