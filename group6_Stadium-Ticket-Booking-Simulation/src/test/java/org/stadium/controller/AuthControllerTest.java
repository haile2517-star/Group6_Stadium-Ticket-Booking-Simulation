package org.stadium.controller;

import org.stadium.model.Account;
import org.stadium.model.AccountStatus;
import org.stadium.model.Fan;
import org.stadium.repository.AdminRepository;
import org.stadium.repository.FanRepository;
import org.stadium.repository.OrganizerRepository;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class AuthControllerTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("=== BAT DAU KIEM THU AUTHCONTROLLER ===\n");

        File tempDir = new File("target/test-data");
        tempDir.mkdirs();

        File tempFans = new File(tempDir, "fans_test.csv");
        File tempAdmins = new File(tempDir, "admins_test.csv");
        File tempOrgs = new File(tempDir, "orgs_test.csv");

        try {
            initSampleData(tempFans, tempAdmins, tempOrgs);

            FanRepository fanRepo = new FanRepository(tempFans.getAbsolutePath());
            AdminRepository adminRepo = new AdminRepository(tempAdmins.getAbsolutePath());
            OrganizerRepository orgRepo = new OrganizerRepository(tempOrgs.getAbsolutePath());

            AuthController authController = new AuthController(fanRepo, orgRepo, adminRepo);

            // Test 1: Đăng nhập Admin thành công
            test("1. Dang nhap Admin thanh cong", () -> {
                Account acc = authController.login("admin", "hash_admin_2026");
                assertCondition(acc != null, "Admin phai dang nhap thanh cong");
                assertCondition("ADM01".equals(acc.getId()), "Admin ID phai la ADM01");
                assertCondition(authController.getCurrentUser() != null, "Current user phai duoc cap nhat");
            });

            // Test 2: Đăng xuất
            test("2. Dang xuat he thong", () -> {
                authController.logout("ADM01");
                assertCondition(authController.getCurrentUser() == null, "Current user phai ve null sau khi logout");
            });

            // Test 3: Đăng nhập Fan thành công
            test("3. Dang nhap Fan thanh cong", () -> {
                Account acc = authController.login("namml1", "namml1");
                assertCondition(acc != null, "Fan phai dang nhap thanh cong");
                assertCondition("FAN0001".equals(acc.getId()), "Fan ID phai la FAN0001");
            });

            // Test 4: Đăng nhập thất bại do sai mật khẩu
            test("4. Dang nhap that bai do sai mat khau", () -> {
                Account acc = authController.login("namml1", "wrong_password");
                assertCondition(acc == null, "Dang nhap phai that bai khi sai mat khau");
            });

            // Test 5: Đăng nhập thất bại do tài khoản bị BLOCKED
            test("5. Dang nhap that bai do tai khoan bi BLOCKED", () -> {
                Account acc = authController.login("binhvt246", "binhvt246");
                assertCondition(acc == null, "Tai khoan bi BLOCKED khong duoc phep dang nhap");
            });

            // Test 6: Đăng ký Fan mới hợp lệ
            test("6. Dang ky Fan moi thanh cong", () -> {
                boolean registered = authController.registerFan(
                        "kiet_test",
                        "password123",
                        "Nguyen Tuan Kiet",
                        "kiet@example.com",
                        "0912345678");
                assertCondition(registered, "Dang ky Fan moi phai tra ve true");

                Fan createdFan = fanRepo.findByUsername("kiet_test");
                assertCondition(createdFan != null, "Fan moi phai ton tai trong repository");
                assertCondition(createdFan.getId().startsWith("FAN"), "ID phai co prefix FAN");
                assertCondition(createdFan.getStatus() == AccountStatus.ACTIVE, "Fan moi phai co trang thai ACTIVE");
                assertCondition("STANDARD".equals(createdFan.getFanType()), "Loai Fan mac dinh phai la STANDARD");

                // Thử đăng nhập bằng tài khoản vừa tạo
                Account loginAcc = authController.login("kiet_test", "password123");
                assertCondition(loginAcc != null, "Fan moi tao phai dang nhap thanh cong");
            });

            // Test 7: Đăng ký Fan trùng username
            test("7. Dang ky Fan that bai khi trung username", () -> {
                boolean registered = authController.registerFan(
                        "kiet_test",
                        "different_pass",
                        "Another Name",
                        "another@example.com",
                        "0999999999");
                assertCondition(!registered, "Khong duoc phep dang ky khi username da ton tai");
            });

            // Test 8: Đăng ký Fan với tham số rỗng / null
            test("8. Dang ky Fan that bai khi du lieu khong hop le", () -> {
                boolean r1 = authController.registerFan("", "pass", "Name", "e@e.com", "090");
                boolean r2 = authController.registerFan("user_x", null, "Name", "e@e.com", "090");
                assertCondition(!r1 && !r2, "Dang ky phai that bai khi thieu thong tin");
            });

            // Test 9: Xem hồ sơ (viewProfile)
            test("9. Xem thong tin ho so (viewProfile)", () -> {
                Account profile = authController.viewProfile("FAN0001");
                assertCondition(profile != null, "Phai tim thay ho so FAN0001");
                assertCondition("namml1".equals(profile.getUsername()), "Username phai khop voi FAN0001");
            });

            // Test 10: Cập nhật hồ sơ (updateProfile)
            test("10. Cap nhat ho so (updateProfile)", () -> {
                boolean updated = authController.updateProfile("FAN0001", "new_nam@gmail.com", "0999888777");
                assertCondition(updated, "Cap nhat ho so phai thanh cong");

                Fan fan = fanRepo.findById("FAN0001");
                assertCondition("new_nam@gmail.com".equals(fan.getEmail()), "Email phai duoc cap nhat");
                assertCondition("0999888777".equals(fan.getPhone()), "Phone phai duoc cap nhat");
            });

            // Test 11: Đổi mật khẩu (changePassword)
            test("11. Doi mat khau thanh cong va dang nhap bang mat khau moi", () -> {
                boolean changed = authController.changePassword("FAN0001", "namml1", "newPass2026");
                assertCondition(changed, "Doi mat khau voi oldPass dung phai thanh cong");

                // Đăng nhập bằng pass cũ phải thất bại
                Account loginOld = authController.login("namml1", "namml1");
                assertCondition(loginOld == null, "Dang nhap voi pass cu phai that bai");

                // Đăng nhập bằng pass mới phải thành công
                Account loginNew = authController.login("namml1", "newPass2026");
                assertCondition(loginNew != null, "Dang nhap voi pass moi phai thanh cong");
            });

            // Test 12: Đổi mật khẩu thất bại khi sai mật khẩu cũ
            test("12. Doi mat khau that bai khi sai pass cu", () -> {
                boolean changed = authController.changePassword("FAN0001", "wrong_old_pass", "abc12345");
                assertCondition(!changed, "Doi mat khau phai that bai khi sai mat khau cu");
            });

            System.out.println("\n--------------------------------------------------");
            System.out.printf("KET QUA: %d/%d test cases DA PASS HOAN TOAN!\n", testsPassed, testsRun);
            System.out.println("--------------------------------------------------");

        } catch (Exception e) {
            System.err.println("Co loi xay ra trong qua trinh kiem thu: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Xoa file tam
            tempFans.delete();
            tempAdmins.delete();
            tempOrgs.delete();
            tempDir.delete();
        }
    }

    private static void initSampleData(File fans, File admins, File orgs) throws IOException {
        try (FileWriter fw = new FileWriter(fans)) {
            fw.write("fanId,username,passwordHash,fullName,email,phone,fanType,status\n");
            fw.write("FAN0001,namml1,hash_pass_namml1,Le Minh Nam,namml1@gmail.com,0902948884,VIP,ACTIVE\n");
            fw.write(
                    "FAN0246,binhvt246,hash_pass_binhvt246,Tran Van Binh,binhvt246@gmail.com,0957692194,SILVER,BLOCKED\n");
        }
        try (FileWriter fw = new FileWriter(admins)) {
            fw.write("adminId,username,passwordHash,fullName,email,phone,status\n");
            fw.write("ADM01,admin,hash_admin_2026,System Administrator,admin@stadium.vn,0909999999,ACTIVE\n");
        }
        try (FileWriter fw = new FileWriter(orgs)) {
            fw.write("organizerId,username,passwordHash,orgName,email,phone,status\n");
            fw.write("ORG01,vff_official,hash_vff_2026,VFF,contact@vff.org.vn,02438453234,ACTIVE\n");
        }
    }

    private static void test(String testName, Runnable testBody) {
        testsRun++;
        try {
            testBody.run();
            testsPassed++;
            System.out.println("[PASS] " + testName);
        } catch (AssertionError | Exception e) {
            System.err.println("[FAIL] " + testName + " -> " + e.getMessage());
        }
    }

    private static void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
