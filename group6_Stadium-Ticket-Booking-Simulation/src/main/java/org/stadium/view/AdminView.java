package org.stadium.view;

import org.stadium.controller.AuthController;
import org.stadium.model.Organizer;

import java.util.List;
import java.util.Scanner;

public class AdminView {

    private final AuthController authController;
    private final Scanner scanner;

    // Khởi tạo AdminView với AuthController và Scanner được truyền vào (Dependency Injection)
    public AdminView(AuthController authController, Scanner scanner) {
        this.authController = authController;
        this.scanner = scanner;
    }

    // Constructor nhận AuthController và khởi tạo Scanner mặc định
    public AdminView(AuthController authController) {
        this(authController, new Scanner(System.in));
    }

    // Constructor mặc định tự khởi tạo AuthController và Scanner
    public AdminView() {
        this(new AuthController(), new Scanner(System.in));
    }
    // Bảng điều khiển quản trị (Admin Dashboard) duyệt tài khoản Ban tổ chức
    public void displayAdminDashboard() {
        boolean inAdmin = true;
        // Vòng lặp giao diện Admin Dashboard
        while (inAdmin) {
            System.out.println();
            System.out.println("╔══════════════════════════════════════════════════════════════╗");
            System.out.println("║                BẢNG ĐIỀU KHIỂN QUẢN TRỊ VIÊN                 ║");
            System.out.println("╠══════════════════════════════════════════════════════════════╣");
            System.out.println("║ 1. Xem danh sách Ban tổ chức chờ duyệt (PENDING)             ║");
            System.out.println("║ 2. Phê duyệt tài khoản Ban tổ chức (Approve)                 ║");
            System.out.println("║ 3. Từ chối tài khoản Ban tổ chức (Reject)                    ║");
            System.out.println("║ 0. Quay lại menu chính                                       ║");
            System.out.println("╚══════════════════════════════════════════════════════════════╝");
            System.out.print("👉 Lựa chọn của Admin [0-3]: ");

            String choice = this.scanner.nextLine().trim();
            switch (choice) {
                // 1. Xem danh sách BTC trạng thái PENDING
                case "1":
                    List<Organizer> pendingList = this.authController.getPendingOrganizers(); // Lấy danh sách BTC chờ duyệt
                    this.displayPendingOrganizers(pendingList); // In danh sách BTC chờ duyệt ra console
                    break;
                // 2. Duyệt tài khoản BTC sang ACTIVE
                case "2":
                    this.approveOrganizer(); // Mở form duyệt BTC
                    break;
                // 3. Từ chối tài khoản BTC sang REJECTED
                case "3":
                    this.rejectOrganizer(); // Mở form từ chối BTC
                    break;
                // 0. Quay lại menu chính
                case "0":
                    inAdmin = false; // Thoát vòng lặp dashboard
                    break;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }

    // Hiển thị danh sách các tài khoản Ban tổ chức đang ở trạng thái PENDING
    public void displayPendingOrganizers(List<Organizer> pendingList) {
        System.out.println();
        System.out.println("================= DANH SÁCH BAN TỔ CHỨC CHỜ DUYỆT =================");
        // Kiểm tra danh sách rỗng
        if (pendingList == null || pendingList.isEmpty()) {
            System.out.println("ℹ️ Không có tài khoản Ban tổ chức nào đang chờ phê duyệt.");
            return;
        }

        // In tiêu đề cột định dạng
        System.out.printf("%-10s | %-16s | %-30s | %-20s%n",
                "Mã BTC", "Tên Đăng Nhập", "Tên Đơn Vị Tổ Chức", "Email");
        System.out.println("-------------------------------------------------------------------");
        // Duyệt từng đơn vị BTC chờ duyệt
        for (Organizer org : pendingList) {
            System.out.printf("%-10s | %-16s | %-30s | %-20s%n",
                    org.getId(),
                    org.getUsername(),
                    org.getOrgName(),
                    org.getEmail());
        }
        System.out.println("-------------------------------------------------------------------");
        System.out.println("Tổng số đơn chờ duyệt: " + pendingList.size());
    }

    // Form nhập mã Ban tổ chức và thực hiện phê duyệt (chuyển sang ACTIVE)
    public void approveOrganizer() {
        System.out.println();
        System.out.println("--- PHÊ DUYỆT TÀI KHOẢN BAN TỔ CHỨC ---");
        System.out.print("👉 Nhập mã Ban tổ chức cần duyệt (vd: ORG01): ");
        String orgId = this.scanner.nextLine().trim();
        if (orgId.isEmpty()) {
            System.out.println("❌ Mã không được để trống!");
            return;
        }

        // Gọi AuthController cập nhật trạng thái ACTIVE và lưu vào organizers.csv
        boolean success = this.authController.approveOrganizer(orgId);
        if (success) {
            System.out.println("🎉 [THÀNH CÔNG] Đã phê duyệt tài khoản " + orgId + " sang trạng thái ACTIVE.");
        } else {
            System.out.println("❌ [THẤT BẠI] Không tìm thấy tài khoản với mã: " + orgId);
        }
    }

    // Form nhập mã Ban tổ chức và thực hiện từ chối (chuyển sang REJECTED)
    public void rejectOrganizer() {
        System.out.println();
        System.out.println("--- TỪ CHỐI TÀI KHOẢN BAN TỔ CHỨC ---");
        System.out.print("👉 Nhập mã Ban tổ chức muốn từ chối: ");
        String orgId = this.scanner.nextLine().trim();
        if (orgId.isEmpty()) {
            System.out.println("❌ Mã không được để trống!");
            return;
        }

        // Gọi AuthController cập nhật trạng thái REJECTED và lưu vào organizers.csv
        boolean success = this.authController.rejectOrganizer(orgId);
        if (success) {
            System.out.println("ℹ️ Đã từ chối phê duyệt tài khoản " + orgId + " (chuyển REJECTED).");
        } else {
            System.out.println("❌ [THẤT BẠI] Không tìm thấy tài khoản với mã: " + orgId);
        }
    }
}
