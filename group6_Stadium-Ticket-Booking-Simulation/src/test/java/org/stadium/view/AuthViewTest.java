package org.stadium.view;

import org.stadium.controller.AuthController;
import org.stadium.model.Account;
import org.stadium.model.AccountStatus;
import org.stadium.model.Fan;
import org.stadium.model.Organizer;
import org.stadium.repository.AdminRepository;
import org.stadium.repository.FanRepository;
import org.stadium.repository.OrganizerRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class AuthViewTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    private static PrintStream createPrintStream(ByteArrayOutputStream out) {
        try {
            return new PrintStream(out, true, "UTF-8");
        } catch (Exception e) {
            return new PrintStream(out, true);
        }
    }

    private static String getOutput(ByteArrayOutputStream out) {
        try {
            return out.toString("UTF-8");
        } catch (Exception e) {
            return out.toString();
        }
    }

    public static void main(String[] args) {
        System.out.println("=== BAT DAU KIEM THU AUTHVIEW (TUAN 6 - KIET) ===\n");

        File tempDir = new File("target/test-data-view");
        tempDir.mkdirs();

        File tempFans = new File(tempDir, "fans_view_test.csv");
        File tempAdmins = new File(tempDir, "admins_view_test.csv");
        File tempOrgs = new File(tempDir, "orgs_view_test.csv");

        try {
            initSampleData(tempFans, tempAdmins, tempOrgs);

            FanRepository fanRepo = new FanRepository(tempFans.getAbsolutePath());
            AdminRepository adminRepo = new AdminRepository(tempAdmins.getAbsolutePath());
            OrganizerRepository orgRepo = new OrganizerRepository(tempOrgs.getAbsolutePath());

            AuthController authController = new AuthController(fanRepo, orgRepo, adminRepo);

            test("1. Test displayLoginForm - Dang nhap thanh cong", () -> {
                String simulatedInput = "namml1\nnamml1\n";
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(outContent));

                try {
                    Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
                    AuthView authView = new AuthView(authController, scanner);
                    authView.displayLoginForm();

                    String output = getOutput(outContent);
                    assertCondition(output.contains("Đăng nhập thành công!"), "Thong bao dang nhap thanh cong phai xuat hien");
                    assertCondition(authController.getCurrentUser() != null, "CurrentUser phai duoc luu trong AuthController");
                    assertCondition("FAN0001".equals(authController.getCurrentUser().getId()), "ID nguoi dung phai la FAN0001");
                } finally {
                    System.setOut(originalOut);
                }
            });

            test("2. Test displayLoginForm - Dang nhap that bai do sai mat khau", () -> {
                authController.logout(null);
                String simulatedInput = "namml1\nwrong_pass\n";
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(outContent));

                try {
                    Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
                    AuthView authView = new AuthView(authController, scanner);
                    authView.displayLoginForm();

                    String output = getOutput(outContent);
                    assertCondition(output.contains("Đăng nhập thất bại!"), "Thong bao that bai phai xuat hien");
                    assertCondition(output.contains("Sai mật khẩu"), "Ly do sai mat khau phai xuat hien");
                    assertCondition(authController.getCurrentUser() == null, "CurrentUser phai la null");
                } finally {
                    System.setOut(originalOut);
                }
            });

            test("3. Test displayFanRegisterForm - Dang ky Fan thanh cong", () -> {
                String simulatedInput = "kiet_fan6\npassword123\npassword123\nNguyen Tuan Kiet\nkiet.tuan@stadium.vn\n0987654321\n";
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(outContent));

                try {
                    Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
                    AuthView authView = new AuthView(authController, scanner);
                    authView.displayFanRegisterForm();

                    String output = getOutput(outContent);
                    assertCondition(output.contains("Đăng ký tài khoản Fan thành công!"), "Phai bao thanh cong");

                    Fan created = fanRepo.findByUsername("kiet_fan6");
                    assertCondition(created != null, "Fan moi phai duoc luu vao fanRepo");
                    assertCondition(created.getStatus() == AccountStatus.ACTIVE, "Trang thai phai la ACTIVE");
                    assertCondition("Nguyen Tuan Kiet".equals(created.getFullName()), "Ho ten phai khop");
                } finally {
                    System.setOut(originalOut);
                }
            });

            test("4. Test displayOrganizerRegisterForm - Dang ky BTC thanh cong trang thai PENDING", () -> {
                String simulatedInput = "vff_event\npassword123\npassword123\nCong Ty To Chuc Su Kien VFF\nevent@vff.org.vn\n0912345678\n";
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(outContent));

                try {
                    Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
                    AuthView authView = new AuthView(authController, scanner);
                    authView.displayOrganizerRegisterForm();

                    String output = getOutput(outContent);
                    assertCondition(output.contains("Đăng ký tài khoản Ban tổ chức thành công!"), "Phai bao thanh cong");
                    assertCondition(output.contains("PENDING"), "Phai thong bao trang thai PENDING");

                    Organizer createdOrg = orgRepo.findByUsername("vff_event");
                    assertCondition(createdOrg != null, "Organizer moi phai ton tai");
                    assertCondition(createdOrg.getStatus() == AccountStatus.PENDING, "Organizer moi phai o trang thai PENDING");
                } finally {
                    System.setOut(originalOut);
                }
            });

            test("5. Test displayProfile - Hien thi dung thong tin ho so", () -> {
                Account fan = fanRepo.findByUsername("namml1");
                authController.setCurrentUser(fan);

                String simulatedInput = "0\n";
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(outContent));

                try {
                    Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
                    AuthView authView = new AuthView(authController, scanner);
                    authView.displayProfile(fan);

                    String output = getOutput(outContent);
                    assertCondition(output.contains("FAN0001"), "Output phai co ma FAN0001");
                    assertCondition(output.contains("namml1"), "Output phai co username namml1");
                    assertCondition(output.contains("Le Minh Nam"), "Output phai co ho ten");
                    assertCondition(output.contains("VIP"), "Output phai co hang thanh vien");
                } finally {
                    System.setOut(originalOut);
                }
            });

            test("6. Test displayChangePasswordForm - Doi mat khau thanh cong", () -> {
                Account fan = fanRepo.findByUsername("namml1");
                authController.setCurrentUser(fan);

                String simulatedInput = "namml1\nnewsecret123\nnewsecret123\n";
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(outContent));

                try {
                    Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
                    AuthView authView = new AuthView(authController, scanner);
                    authView.displayChangePasswordForm();

                    String output = getOutput(outContent);
                    assertCondition(output.contains("Đổi mật khẩu thành công!"), "Phai co thong bao doi mat khau thanh cong");

                    Account reLogin = authController.login("namml1", "newsecret123");
                    assertCondition(reLogin != null, "Phai dang nhap thanh cong voi mat khau moi");
                } finally {
                    System.setOut(originalOut);
                }
            });

            test("7. Test displayUpdateProfileForm - Cap nhat Email va Phone", () -> {
                Account fan = fanRepo.findByUsername("namml1");
                authController.setCurrentUser(fan);

                String simulatedInput = "nam.new@gmail.com\n0988776655\n";
                ByteArrayOutputStream outContent = new ByteArrayOutputStream();
                PrintStream originalOut = System.out;
                System.setOut(createPrintStream(outContent));

                try {
                    Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8)));
                    AuthView authView = new AuthView(authController, scanner);
                    authView.displayUpdateProfileForm();

                    String output = getOutput(outContent);
                    assertCondition(output.contains("Cập nhật thông tin liên hệ thành công!"), "Phai bao cap nhat thanh cong");
                    assertCondition("nam.new@gmail.com".equals(fan.getEmail()), "Email phai duoc cap nhat");
                    assertCondition("0988776655".equals(fan.getPhone()), "Phone phai duoc cap nhat");
                } finally {
                    System.setOut(originalOut);
                }
            });

        } catch (Exception e) {
            System.err.println("❌ Ngoai le ngoai y muon: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--------------------------------------------------");
        System.out.println("KET QUA AUTHVIEW TEST: " + testsPassed + "/" + testsRun + " test cases DA PASS!");
        System.out.println("--------------------------------------------------");
        if (testsPassed != testsRun) {
            System.exit(1);
        }
    }

    private static void test(String name, Runnable runnable) {
        testsRun++;
        try {
            runnable.run();
            testsPassed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable t) {
            System.err.println("[FAIL] " + name + " -> " + t.getMessage());
        }
    }

    private static void assertCondition(boolean condition, String msg) {
        if (!condition) {
            throw new AssertionError(msg);
        }
    }

    private static void initSampleData(File fans, File admins, File orgs) throws IOException {
        try (FileWriter fw = new FileWriter(admins)) {
            fw.write("adminId,username,passwordHash,fullName,email,phone,status\n");
            fw.write("ADM01,admin,hash_admin_2026,System Administrator,admin@stadium.vn,0909999999,ACTIVE\n");
        }

        try (FileWriter fw = new FileWriter(fans)) {
            fw.write("fanId,username,passwordHash,fullName,email,phone,fanType,status\n");
            fw.write("FAN0001,namml1,hash_pass_namml1,Le Minh Nam,namml1@gmail.com,0902948884,VIP,ACTIVE\n");
            fw.write("FAN0002,binhvt246,hash_pass_binhvt246,Vu Thanh Binh,binh@gmail.com,0902948885,STANDARD,BLOCKED\n");
        }

        try (FileWriter fw = new FileWriter(orgs)) {
            fw.write("organizerId,username,passwordHash,orgName,email,phone,status\n");
            fw.write("ORG01,vff_official,hash_vff_2026,Lien doan Bong da Viet Nam,contact@vff.org.vn,02438453234,ACTIVE\n");
            fw.write("ORG02,vietfootball,hash_vietfb_2026,VietFootball Corp,admin@vietfb.vn,0901234567,PENDING\n");
        }
    }
}
