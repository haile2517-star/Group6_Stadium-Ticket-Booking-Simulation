package org.stadium.view;

import org.stadium.controller.AuthController;
import org.stadium.model.Account;
import org.stadium.model.AccountStatus;
import org.stadium.model.Admin;
import org.stadium.model.Fan;
import org.stadium.model.Organizer;

import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * View xử lý giao diện Console cho phân hệ Xác thực và Quản lý Tài khoản (Auth / Account).
 * Thành viên phụ trách: Kiệt (Tuần 6 - View Layer).
 * 
 * Các chức năng chính:
 * 1. displayLoginForm(): Đăng nhập vào hệ thống
 * 2. displayFanRegisterForm(): Đăng ký tài khoản người hâm mộ (Fan)
 * 3. displayOrganizerRegisterForm(): Đăng ký tài khoản ban tổ chức (Organizer)
 * 4. displayProfile(Account account): Xem và cập nhật hồ sơ cá nhân
 * 5. displayChangePasswordForm(): Đổi mật khẩu
 */
public class AuthView {

    private final AuthController authController;
    private final Scanner scanner;

    // Regex biểu thức chính quy cho dữ liệu đầu vào
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^0[0-9]{9,10}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");

    public AuthView() {
        this(new AuthController(), new Scanner(System.in));
    }

    public AuthView(AuthController authController) {
        this(authController, new Scanner(System.in));
    }

    public AuthView(AuthController authController, Scanner scanner) {
        this.authController = authController;
        this.scanner = scanner;
    }

    public AuthController getAuthController() {
        return authController;
    }

    /**
     * Khởi chạy Menu xác thực tương tác trên Console (Auth Portal).
     * Cho phép kiểm thử toàn diện luồng nghiệp vụ của Tuần 6.
     */
    public void start() {
        displayAuthMenu();
    }

    /**
     * Hiển thị menu điều hướng xác thực trên Console.
     */
    public void displayAuthMenu() {
        boolean running = true;
        while (running) {
            Account current = authController.getCurrentUser();
            String userInfo = (current != null)
                    ? current.getUsername() + " (" + getRoleName(current) + " - " + current.getStatus() + ")"
                    : "Chưa đăng nhập";

            System.out.println();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║         HỆ THỐNG XÁC THỰC TÀI KHOẢN - STADIUM BOOKING        ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║ Trạng thái: " + padRight(userInfo, 49) + "║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Đăng nhập hệ thống (Login)                                ║");
            System.out.println("║ 2. Đăng ký tài khoản Fan (Register Fan)                      ║");
            System.out.println("║ 3. Đăng ký tài khoản Ban tổ chức (Register Organizer)        ║");
            System.out.println("║ 4. Xem thông tin hồ sơ (View Profile)                        ║");
            System.out.println("║ 5. Đổi mật khẩu (Change Password)                            ║");
            System.out.println("║ 6. Cập nhật thông tin liên hệ (Update Contact Info)          ║");
            System.out.println("║ 7. Đăng xuất (Logout)                                        ║");
            System.out.println("║ 0. Thoát menu xác thực                                       ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.print("👉 Vui lòng chọn chức năng [0-7]: ");

            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1":
                    displayLoginForm();
                    break;
                case "2":
                    displayFanRegisterForm();
                    break;
                case "3":
                    displayOrganizerRegisterForm();
                    break;
                case "4":
                    displayProfile(authController.getCurrentUser());
                    break;
                case "5":
                    displayChangePasswordForm();
                    break;
                case "6":
                    displayUpdateProfileForm();
                    break;
                case "7":
                    handleLogout();
                    break;
                case "0":
                    running = false;
                    System.out.println("👋 Đã thoát khỏi trình quản lý xác thực.");
                    break;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ! Vui lòng chọn số từ 0 đến 7.");
            }
        }
    }

    /**
     * Form Đăng nhập hệ thống (chuẩn Sơ đồ lớp & Nhiệm vụ T6).
     */
    public void displayLoginForm() {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────────────────┐");
        System.out.println("│                     ĐĂNG NHẬP HỆ THỐNG                       │");
        System.out.println("└──────────────────────────────────────────────────────────────┘");

        if (authController.getCurrentUser() != null) {
            System.out.println("⚠️ Bạn đang đăng nhập với tài khoản: " + authController.getCurrentUser().getUsername());
            System.out.print("Bạn có muốn đăng xuất để đăng nhập tài khoản khác? (y/n): ");
            String ans = scanner.nextLine().trim();
            if (ans.equalsIgnoreCase("y")) {
                authController.logout(authController.getCurrentUser().getId());
                System.out.println("ℹ️ Đã đăng xuất thành công.");
            } else {
                return;
            }
        }

        String username = promptString("Tên đăng nhập (Username)", false);
        if (username.isEmpty()) {
            System.out.println("❌ Tên đăng nhập không được để trống!");
            return;
        }

        String password = promptString("Mật khẩu (Password)", false);
        if (password.isEmpty()) {
            System.out.println("❌ Mật khẩu không được để trống!");
            return;
        }

        System.out.println("⏳ Đang xác thực thông tin đăng nhập...");
        Account account = authController.login(username, password);

        if (account != null) {
            System.out.println();
            System.out.println("🎉 [THÀNH CÔNG] Đăng nhập thành công!");
            System.out.println("👤 Xin chào: " + getDisplayName(account));
            System.out.println("🏷️ Vai trò: " + getRoleName(account));
            System.out.println("📌 Mã tài khoản: " + account.getId());
        } else {
            System.out.println();
            System.out.println("❌ [LỖI] Đăng nhập thất bại!");
            explainLoginFailure(username);
        }
    }

    /**
     * Form Đăng ký tài khoản Người hâm mộ - Fan (chuẩn Sơ đồ lớp & Nhiệm vụ T6).
     */
    public void displayFanRegisterForm() {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────────────────┐");
        System.out.println("│                ĐĂNG KÝ TÀI KHOẢN NGƯỜI HÂM MỘ (FAN)          │");
        System.out.println("└──────────────────────────────────────────────────────────────┘");

        // 1. Nhập và kiểm tra Username
        String username;
        while (true) {
            username = promptString("Tên đăng nhập (3-20 ký tự, chữ/số/_)", false);
            if (!USERNAME_PATTERN.matcher(username).matches()) {
                System.out.println("❌ Tên đăng nhập không hợp lệ! Chỉ gồm 3-20 ký tự chữ, số hoặc dấu gạch dưới.");
                continue;
            }
            if (username.contains(",")) {
                System.out.println("❌ Tên đăng nhập không được chứa dấu phẩy (,)!");
                continue;
            }
            if (authController.isUsernameExists(username)) {
                System.out.println("❌ Tên đăng nhập '" + username + "' đã được sử dụng! Vui lòng chọn tên khác.");
                System.out.print("Bạn có muốn nhập lại? (y/n): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    System.out.println("ℹ️ Đã hủy đăng ký.");
                    return;
                }
                continue;
            }
            break;
        }

        // 2. Nhập và xác nhận Mật khẩu
        String password;
        while (true) {
            password = promptString("Mật khẩu (tối thiểu 6 ký tự)", false);
            if (password.length() < 6) {
                System.out.println("❌ Mật khẩu phải có ít nhất 6 ký tự!");
                continue;
            }
            if (password.contains(",")) {
                System.out.println("❌ Mật khẩu không được chứa dấu phẩy (,)!");
                continue;
            }

            String confirmPass = promptString("Xác nhận lại mật khẩu", false);
            if (!password.equals(confirmPass)) {
                System.out.println("❌ Mật khẩu xác nhận không khớp! Vui lòng nhập lại.");
                continue;
            }
            break;
        }

        // 3. Nhập Họ và tên
        String fullName;
        while (true) {
            fullName = promptString("Họ và tên đầy đủ", false);
            if (fullName.isEmpty()) {
                System.out.println("❌ Họ và tên không được để trống!");
                continue;
            }
            if (fullName.contains(",")) {
                System.out.println("❌ Họ và tên không được chứa dấu phẩy (,)!");
                continue;
            }
            break;
        }

        // 4. Nhập Email
        String email;
        while (true) {
            email = promptString("Địa chỉ email (vd: user@example.com)", false);
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("❌ Định dạng email không hợp lệ! Vui lòng nhập đúng cú pháp.");
                continue;
            }
            if (email.contains(",")) {
                System.out.println("❌ Email không được chứa dấu phẩy (,)!");
                continue;
            }
            break;
        }

        // 5. Nhập Số điện thoại
        String phone;
        while (true) {
            phone = promptString("Số điện thoại (10-11 số, bắt đầu bằng 0)", false);
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                System.out.println("❌ Số điện thoại không hợp lệ! Phải có 10-11 chữ số và bắt đầu bằng số 0.");
                continue;
            }
            if (phone.contains(",")) {
                System.out.println("❌ Số điện thoại không được chứa dấu phẩy (,)!");
                continue;
            }
            break;
        }

        System.out.println("⏳ Đang xử lý đăng ký tài khoản Fan...");
        boolean success = authController.registerFan(username, password, fullName, email, phone);

        if (success) {
            System.out.println();
            System.out.println("🎉 [THÀNH CÔNG] Đăng ký tài khoản Fan thành công!");
            System.out.println("✅ Trạng thái tài khoản: ACTIVE (Bạn có thể đăng nhập và đặt vé ngay lập tức).");
        } else {
            System.out.println();
            System.out.println("❌ [LỖI] Đăng ký tài khoản Fan thất bại! Vui lòng thử lại.");
        }
    }

    /**
     * Form Đăng ký tài khoản Ban tổ chức - Organizer (chuẩn Sơ đồ lớp & Nhiệm vụ T6).
     */
    public void displayOrganizerRegisterForm() {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────────────────┐");
        System.out.println("│             ĐĂNG KÝ TÀI KHOẢN BAN TỔ CHỨC (ORGANIZER)         │");
        System.out.println("└──────────────────────────────────────────────────────────────┘");

        // 1. Nhập và kiểm tra Username
        String username;
        while (true) {
            username = promptString("Tên đăng nhập (3-20 ký tự, chữ/số/_)", false);
            if (!USERNAME_PATTERN.matcher(username).matches()) {
                System.out.println("❌ Tên đăng nhập không hợp lệ! Chỉ gồm 3-20 ký tự chữ, số hoặc dấu gạch dưới.");
                continue;
            }
            if (username.contains(",")) {
                System.out.println("❌ Tên đăng nhập không được chứa dấu phẩy (,)!");
                continue;
            }
            if (authController.isUsernameExists(username)) {
                System.out.println("❌ Tên đăng nhập '" + username + "' đã được sử dụng! Vui lòng chọn tên khác.");
                System.out.print("Bạn có muốn nhập lại? (y/n): ");
                if (!scanner.nextLine().trim().equalsIgnoreCase("y")) {
                    System.out.println("ℹ️ Đã hủy đăng ký.");
                    return;
                }
                continue;
            }
            break;
        }

        // 2. Nhập và xác nhận Mật khẩu
        String password;
        while (true) {
            password = promptString("Mật khẩu (tối thiểu 6 ký tự)", false);
            if (password.length() < 6) {
                System.out.println("❌ Mật khẩu phải có ít nhất 6 ký tự!");
                continue;
            }
            if (password.contains(",")) {
                System.out.println("❌ Mật khẩu không được chứa dấu phẩy (,)!");
                continue;
            }

            String confirmPass = promptString("Xác nhận lại mật khẩu", false);
            if (!password.equals(confirmPass)) {
                System.out.println("❌ Mật khẩu xác nhận không khớp! Vui lòng nhập lại.");
                continue;
            }
            break;
        }

        // 3. Nhập Tên Ban tổ chức / Doanh nghiệp
        String orgName;
        while (true) {
            orgName = promptString("Tên đơn vị / Ban tổ chức (Organization Name)", false);
            if (orgName.isEmpty()) {
                System.out.println("❌ Tên đơn vị tổ chức không được để trống!");
                continue;
            }
            if (orgName.contains(",")) {
                System.out.println("❌ Tên đơn vị không được chứa dấu phẩy (,)!");
                continue;
            }
            break;
        }

        // 4. Nhập Email
        String email;
        while (true) {
            email = promptString("Địa chỉ email liên hệ (vd: contact@org.vn)", false);
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                System.out.println("❌ Định dạng email không hợp lệ! Vui lòng nhập đúng cú pháp.");
                continue;
            }
            if (email.contains(",")) {
                System.out.println("❌ Email không được chứa dấu phẩy (,)!");
                continue;
            }
            break;
        }

        // 5. Nhập Số điện thoại
        String phone;
        while (true) {
            phone = promptString("Số điện thoại liên hệ (10-11 số, bắt đầu bằng 0)", false);
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                System.out.println("❌ Số điện thoại không hợp lệ! Phải có 10-11 chữ số và bắt đầu bằng số 0.");
                continue;
            }
            if (phone.contains(",")) {
                System.out.println("❌ Số điện thoại không được chứa dấu phẩy (,)!");
                continue;
            }
            break;
        }

        System.out.println("⏳ Đang xử lý đăng ký tài khoản Ban tổ chức...");
        boolean success = authController.registerOrganizer(username, password, orgName, email, phone);

        if (success) {
            System.out.println();
            System.out.println("🎉 [THÀNH CÔNG] Đăng ký tài khoản Ban tổ chức thành công!");
            System.out.println("📋 Trạng thái tài khoản: PENDING (Chờ phê duyệt).");
            System.out.println("🔔 Lưu ý: Tài khoản của bạn cần được Quản trị viên (Admin) phê duyệt trước khi có thể đăng nhập.");
        } else {
            System.out.println();
            System.out.println("❌ [LỖI] Đăng ký Ban tổ chức thất bại! Vui lòng thử lại.");
        }
    }

    /**
     * Hiển thị thông tin hồ sơ tài khoản (chuẩn Sơ đồ lớp & Nhiệm vụ T6).
     *
     * @param account Tài khoản cần xem hồ sơ
     */
    public void displayProfile(Account account) {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────────────────┐");
        System.out.println("│                   THÔNG TIN HỒ SƠ CÁ NHÂN                    │");
        System.out.println("└──────────────────────────────────────────────────────────────┘");

        if (account == null) {
            account = authController.getCurrentUser();
        }

        if (account == null) {
            System.out.println("⚠️ Bạn chưa đăng nhập! Vui lòng đăng nhập để xem thông tin hồ sơ.");
            return;
        }

        // Làm mới dữ liệu từ repository thông qua viewProfile của Controller
        Account freshAccount = authController.viewProfile(account.getId());
        if (freshAccount != null) {
            account = freshAccount;
        }

        String role = getRoleName(account);
        String name = getDisplayName(account);

        System.out.println("┌───────────────────────┬──────────────────────────────────────┐");
        System.out.printf("│ %-21s │ %-36s │%n", "Mã tài khoản", account.getId());
        System.out.printf("│ %-21s │ %-36s │%n", "Tên đăng nhập", account.getUsername());
        System.out.printf("│ %-21s │ %-36s │%n", "Vai trò hệ thống", role);
        System.out.printf("│ %-21s │ %-36s │%n", "Tên hiển thị", name);

        if (account instanceof Fan) {
            Fan fan = (Fan) account;
            System.out.printf("│ %-21s │ %-36s │%n", "Hạng thành viên", fan.getFanType());
        }

        System.out.printf("│ %-21s │ %-36s │%n", "Địa chỉ Email", account.getEmail());
        System.out.printf("│ %-21s │ %-36s │%n", "Số điện thoại", account.getPhone());
        System.out.printf("│ %-21s │ %-36s │%n", "Trạng thái", account.getStatus());
        System.out.println("└───────────────────────┴──────────────────────────────────────┘");

        System.out.println("Tùy chọn:");
        System.out.println(" [1] Cập nhật thông tin liên hệ (Email, Số điện thoại)");
        System.out.println(" [2] Đổi mật khẩu");
        System.out.println(" [0] Quay lại");
        System.out.print("👉 Lựa chọn: ");

        String subChoice = scanner.nextLine().trim();
        if ("1".equals(subChoice)) {
            displayUpdateProfileForm();
        } else if ("2".equals(subChoice)) {
            displayChangePasswordForm();
        }
    }

    /**
     * Form Đổi mật khẩu (chuẩn Sơ đồ lớp & Nhiệm vụ T6).
     */
    public void displayChangePasswordForm() {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────────────────┐");
        System.out.println("│                        ĐỔI MẬT KHẨU                          │");
        System.out.println("└──────────────────────────────────────────────────────────────┘");

        Account current = authController.getCurrentUser();
        if (current == null) {
            System.out.println("⚠️ Bạn chưa đăng nhập! Vui lòng đăng nhập trước khi thực hiện đổi mật khẩu.");
            return;
        }

        String oldPass = promptString("Mật khẩu hiện tại", false);
        if (oldPass.isEmpty()) {
            System.out.println("❌ Mật khẩu hiện tại không được để trống!");
            return;
        }

        String newPass;
        while (true) {
            newPass = promptString("Mật khẩu mới (tối thiểu 6 ký tự)", false);
            if (newPass.length() < 6) {
                System.out.println("❌ Mật khẩu mới phải có tối thiểu 6 ký tự!");
                continue;
            }
            if (newPass.contains(",")) {
                System.out.println("❌ Mật khẩu không được chứa dấu phẩy (,)!");
                continue;
            }
            if (newPass.equals(oldPass)) {
                System.out.println("❌ Mật khẩu mới không được trùng với mật khẩu hiện tại!");
                continue;
            }

            String confirmPass = promptString("Xác nhận mật khẩu mới", false);
            if (!newPass.equals(confirmPass)) {
                System.out.println("❌ Xác nhận mật khẩu không khớp! Vui lòng nhập lại.");
                continue;
            }
            break;
        }

        System.out.println("⏳ Đang tiến hành thay đổi mật khẩu...");
        boolean success = authController.changePassword(current.getId(), oldPass, newPass);

        if (success) {
            System.out.println();
            System.out.println("🎉 [THÀNH CÔNG] Đổi mật khẩu thành công!");
            System.out.println("ℹ️ Mật khẩu mới đã được cập nhật an toàn vào hệ thống.");
        } else {
            System.out.println();
            System.out.println("❌ [LỖI] Đổi mật khẩu thất bại! Sai mật khẩu hiện tại hoặc tài khoản không tồn tại.");
        }
    }

    /**
     * Form Cập nhật thông tin liên hệ (Email và Số điện thoại).
     */
    public void displayUpdateProfileForm() {
        System.out.println();
        System.out.println("┌──────────────────────────────────────────────────────────────┐");
        System.out.println("│                 CẬP NHẬT THÔNG TIN LIÊN HỆ                   │");
        System.out.println("└──────────────────────────────────────────────────────────────┘");

        Account current = authController.getCurrentUser();
        if (current == null) {
            System.out.println("⚠️ Bạn chưa đăng nhập! Vui lòng đăng nhập để cập nhật thông tin.");
            return;
        }

        System.out.println("Email hiện tại: " + current.getEmail());
        String newEmail;
        while (true) {
            newEmail = promptString("Email mới (nhấn Enter để giữ nguyên)", true);
            if (newEmail.isEmpty()) {
                newEmail = current.getEmail();
                break;
            }
            if (!EMAIL_PATTERN.matcher(newEmail).matches()) {
                System.out.println("❌ Định dạng email không hợp lệ!");
                continue;
            }
            if (newEmail.contains(",")) {
                System.out.println("❌ Email không được chứa dấu phẩy (,)!");
                continue;
            }
            break;
        }

        System.out.println("Số điện thoại hiện tại: " + current.getPhone());
        String newPhone;
        while (true) {
            newPhone = promptString("Số điện thoại mới (nhấn Enter để giữ nguyên)", true);
            if (newPhone.isEmpty()) {
                newPhone = current.getPhone();
                break;
            }
            if (!PHONE_PATTERN.matcher(newPhone).matches()) {
                System.out.println("❌ Số điện thoại không hợp lệ! Phải có 10-11 chữ số và bắt đầu bằng số 0.");
                continue;
            }
            if (newPhone.contains(",")) {
                System.out.println("❌ Số điện thoại không được chứa dấu phẩy (,)!");
                continue;
            }
            break;
        }

        if (newEmail.equals(current.getEmail()) && newPhone.equals(current.getPhone())) {
            System.out.println("ℹ️ Không có thông tin nào thay đổi.");
            return;
        }

        System.out.println("⏳ Đang lưu cập nhật thông tin...");
        boolean success = authController.updateProfile(current.getId(), newEmail, newPhone);

        if (success) {
            System.out.println();
            System.out.println("🎉 [THÀNH CÔNG] Cập nhật thông tin liên hệ thành công!");
        } else {
            System.out.println();
            System.out.println("❌ [LỖI] Cập nhật thông tin thất bại!");
        }
    }

    /**
     * Xử lý đăng xuất tài khoản hiện tại.
     */
    private void handleLogout() {
        Account current = authController.getCurrentUser();
        if (current == null) {
            System.out.println("ℹ️ Bạn chưa đăng nhập!");
            return;
        }
        String username = current.getUsername();
        authController.logout(current.getId());
        System.out.println("✅ [THÀNH CÔNG] Đã đăng xuất khỏi tài khoản: " + username);
    }

    /**
     * Hỗ trợ giải thích lý do đăng nhập thất bại một cách chi tiết và thân thiện.
     */
    private void explainLoginFailure(String username) {
        String trimmed = username.trim();
        Account acc = authController.getAdminRepo().findByUsername(trimmed);
        if (acc == null) acc = authController.getFanRepo().findByUsername(trimmed);
        if (acc == null) acc = authController.getOrganizerRepo().findByUsername(trimmed);

        if (acc == null) {
            System.out.println("👉 Lý do: Tên đăng nhập không tồn tại trong hệ thống.");
        } else if (acc.getStatus() == AccountStatus.PENDING) {
            System.out.println("👉 Lý do: Tài khoản đang ở trạng thái [PENDING - Chờ duyệt]. Vui lòng liên hệ Admin!");
        } else if (acc.getStatus() == AccountStatus.BLOCKED) {
            System.out.println("👉 Lý do: Tài khoản đã bị [BLOCKED - Khóa]. Vui lòng liên hệ hỗ trợ!");
        } else if (acc.getStatus() == AccountStatus.REJECTED) {
            System.out.println("👉 Lý do: Tài khoản đã bị [REJECTED - Từ chối phê duyệt].");
        } else {
            System.out.println("👉 Lý do: Sai mật khẩu! Vui lòng kiểm tra lại mật khẩu đã nhập.");
        }
    }

    private String getDisplayName(Account acc) {
        if (acc instanceof Fan) {
            return ((Fan) acc).getFullName();
        } else if (acc instanceof Organizer) {
            return ((Organizer) acc).getOrgName();
        } else if (acc instanceof Admin) {
            return ((Admin) acc).getFullName();
        }
        return acc.getUsername();
    }

    private String getRoleName(Account acc) {
        if (acc instanceof Admin) return "ADMIN";
        if (acc instanceof Organizer) return "ORGANIZER";
        if (acc instanceof Fan) return "FAN";
        return "USER";
    }

    private String promptString(String label, boolean allowEmpty) {
        while (true) {
            System.out.print("👉 " + label + ": ");
            String input = scanner.nextLine().trim();
            if (allowEmpty || !input.isEmpty()) {
                return input;
            }
            System.out.println("❌ Trường này không được để trống!");
        }
    }

    private String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() >= n) return s.substring(0, n);
        return String.format("%-" + n + "s", s);
    }
}
