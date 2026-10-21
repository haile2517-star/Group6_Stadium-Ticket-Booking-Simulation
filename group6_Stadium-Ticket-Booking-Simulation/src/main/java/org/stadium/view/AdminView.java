package org.stadium.view;

import org.stadium.controller.AuthController;
import org.stadium.model.Organizer;

import java.util.List;
import java.util.Scanner;

/**
 * View dành cho Quản trị viên (Admin) duyệt ban tổ chức và quản trị hệ thống.
 * Tuân thủ mô hình MVC và nguyên tắc Bàn Tay Sắt.
 */
public class AdminView {

    private final AuthController authController;
    private final Scanner scanner;

    public AdminView(AuthController authController, Scanner scanner) {
        this.authController = authController;
        this.scanner = scanner;
    }

    public AdminView(AuthController authController) {
        this(authController, new Scanner(System.in));
    }

    public AdminView() {
        this(new AuthController(), new Scanner(System.in));
    }

    /**
     * Bảng điều khiển quản trị (Admin Dashboard).
     */
    public void displayAdminDashboard() {
        boolean inAdmin = true;
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
                case "1":
                    List<Organizer> pendingList = this.authController.getPendingOrganizers();
                    this.displayPendingOrganizers(pendingList);
                    break;
                case "2":
                    this.approveOrganizer();
                    break;
                case "3":
                    this.rejectOrganizer();
                    break;
                case "0":
                    inAdmin = false;
                    break;
                default:
                    System.out.println("❌ Lựa chọn không hợp lệ!");
            }
        }
    }

    /**
     * Hiển thị danh sách các tài khoản Ban tổ chức đang chờ phê duyệt.
     */
    public void displayPendingOrganizers(List<Organizer> pendingList) {
        System.out.println();
        System.out.println("================= DANH SÁCH BAN TỔ CHỨC CHỜ DUYỆT =================");
        if (pendingList == null || pendingList.isEmpty()) {
            System.out.println("ℹ️ Không có tài khoản Ban tổ chức nào đang chờ phê duyệt.");
            return;
        }

        System.out.printf("%-10s | %-16s | %-30s | %-20s%n",
                "Mã BTC", "Tên Đăng Nhập", "Tên Đơn Vị Tổ Chức", "Email");
        System.out.println("-------------------------------------------------------------------");
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

    /**
     * Form phê duyệt tài khoản Ban tổ chức.
     */
    public void approveOrganizer() {
        System.out.println();
        System.out.println("--- PHÊ DUYỆT TÀI KHOẢN BAN TỔ CHỨC ---");
        System.out.print("👉 Nhập mã Ban tổ chức cần duyệt (vd: ORG01): ");
        String orgId = this.scanner.nextLine().trim();
        if (orgId.isEmpty()) {
            System.out.println("❌ Mã không được để trống!");
            return;
        }

        boolean success = this.authController.approveOrganizer(orgId);
        if (success) {
            System.out.println("🎉 [THÀNH CÔNG] Đã phê duyệt tài khoản " + orgId + " sang trạng thái ACTIVE.");
        } else {
            System.out.println("❌ [THẤT BẠI] Không tìm thấy tài khoản với mã: " + orgId);
        }
    }

    /**
     * Form từ chối tài khoản Ban tổ chức.
     */
    public void rejectOrganizer() {
        System.out.println();
        System.out.println("--- TỪ CHỐI TÀI KHOẢN BAN TỔ CHỨC ---");
        System.out.print("👉 Nhập mã Ban tổ chức muốn từ chối: ");
        String orgId = this.scanner.nextLine().trim();
        if (orgId.isEmpty()) {
            System.out.println("❌ Mã không được để trống!");
            return;
        }

        boolean success = this.authController.rejectOrganizer(orgId);
        if (success) {
            System.out.println("ℹ️ Đã từ chối phê duyệt tài khoản " + orgId + " (chuyển REJECTED).");
        } else {
            System.out.println("❌ [THẤT BẠI] Không tìm thấy tài khoản với mã: " + orgId);
        }
    }
}
