package org.stadium.view;

import org.stadium.controller.AuthController;
import org.stadium.controller.BookingController;
import org.stadium.controller.MatchController;
import org.stadium.controller.StadiumController;
import org.stadium.controller.TicketController;
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
import org.stadium.repository.StadiumRepository;
import org.stadium.repository.TicketPricingRepository;
import org.stadium.repository.TicketRepository;
import org.stadium.repository.TransactionRepository;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Menu điều phối trung tâm của hệ thống (Main View & Core Routing).
 * Phụ trách: Siêu (Tuần 6 - Core Routing & View Layer).
 */
public class MainView {

    private Scanner scanner;
    private AuthController authController;
    private MatchController matchController;
    private BookingController bookingController;
    private StadiumController stadiumController;
    private TicketController ticketController;
    private MatchView matchView;
    private BookingView bookingView;
    private OrganizerView organizerView;
    private Account currentUser;
    private boolean running;

    public MainView() {
        String pathMatches = new File("data/matches.csv").exists() ? "data/matches.csv"
                : (new File("../data/matches.csv").exists() ? "../data/matches.csv" : "data/matches.csv");
        String pathStadiums = new File("data/stadiums.csv").exists() ? "data/stadiums.csv"
                : (new File("../data/stadiums.csv").exists() ? "../data/stadiums.csv" : "data/stadiums.csv");
        String pathSections = new File("data/sections.csv").exists() ? "data/sections.csv"
                : (new File("../data/sections.csv").exists() ? "../data/sections.csv" : "data/sections.csv");
        String pathSeats = new File("data/seats.csv").exists() ? "data/seats.csv"
                : (new File("../data/seats.csv").exists() ? "../data/seats.csv" : "data/seats.csv");
        String pathPricings = new File("data/ticket_pricings.csv").exists() ? "data/ticket_pricings.csv"
                : (new File("../data/ticket_pricings.csv").exists() ? "../data/ticket_pricings.csv" : "data/ticket_pricings.csv");
        String pathTickets = new File("data/tickets.csv").exists() ? "data/tickets.csv"
                : (new File("../data/tickets.csv").exists() ? "../data/tickets.csv" : "data/tickets.csv");
        String pathTxns = new File("data/transactions.csv").exists() ? "data/transactions.csv"
                : (new File("../data/transactions.csv").exists() ? "../data/transactions.csv" : "data/transactions.csv");

        MatchRepository matchRepo = new MatchRepository(pathMatches);
        StadiumRepository stadiumRepo = new StadiumRepository(pathStadiums);
        SectionRepository sectionRepo = new SectionRepository(pathSections);
        SeatRepository seatRepo = new SeatRepository(pathSeats);
        TicketPricingRepository pricingRepo = new TicketPricingRepository(pathPricings);
        TicketRepository ticketRepo = new TicketRepository(pathTickets);
        TransactionRepository txnRepo = new TransactionRepository(pathTxns);

        this.authController = new AuthController();
        this.matchController = new MatchController(matchRepo);
        this.stadiumController = new StadiumController(stadiumRepo, sectionRepo, seatRepo);
        this.bookingController = new BookingController(seatRepo, ticketRepo, txnRepo);
        this.ticketController = new TicketController(matchRepo, sectionRepo, seatRepo, ticketRepo, pricingRepo, txnRepo);

        this.scanner = new Scanner(System.in);
        this.matchView = new MatchView(this.scanner, this.matchController);
        this.bookingView = new BookingView(this.scanner, this.bookingController);
        this.organizerView = new OrganizerView(this.scanner, this.ticketController);
        this.currentUser = null;
        this.running = true;
    }

    public void start() {
        System.out.println("======================================================================");
        System.out.println("   HỆ THỐNG MÔ PHỎNG ĐẶT VÉ SÂN VẬN ĐỘNG (STADIUM TICKET BOOKING)");
        System.out.println("                  NHÓM 6 - LAB211 (SEMESTER 3)");
        System.out.println("======================================================================");

        while (this.running) {
            if (this.currentUser == null) {
                this.displayGuestMenu();
            } else if (this.currentUser instanceof Fan) {
                this.displayFanMenu();
            } else if (this.currentUser instanceof Organizer) {
                this.displayOrganizerMenu();
            } else if (this.currentUser instanceof Admin) {
                this.displayAdminMenu();
            } else {
                this.currentUser = null;
            }
        }
        System.out.println("\nCảm ơn bạn đã sử dụng hệ thống Đặt vé Sân vận động! Tạm biệt.");
    }

    public void displayGuestMenu() {
        System.out.println();
        System.out.println("==================== MENU KHÁCH (GUEST) ====================");
        System.out.println("1. Xem danh sách trận đấu");
        System.out.println("2. Xem chi tiết trận đấu");
        System.out.println("3. Tìm kiếm trận đấu theo từ khóa");
        System.out.println("4. Lọc trận đấu theo tiêu chí");
        System.out.println("5. Xem danh sách sân vận động");
        System.out.println("6. Xem khán đài & sơ đồ ghế sân vận động");
        System.out.println("7. Đăng nhập hệ thống");
        System.out.println("8. Đăng ký tài khoản Fan (Người hâm mộ)");
        System.out.println("9. Đăng ký tài khoản Organizer (Ban tổ chức)");
        System.out.println("0. Thoát chương trình");
        System.out.println("============================================================");
        System.out.print("Vui lòng chọn chức năng (0-9): ");
        String choice = this.scanner.nextLine().trim();

        if ("1".equals(choice)) {
            List<Match> matches = this.matchController.viewAllMatches();
            this.matchView.displayMatches(matches);
        } else if ("2".equals(choice)) {
            System.out.print("Nhập mã trận đấu cần xem chi tiết (VD: MTH01): ");
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
            System.out.println("\n============= DANH SÁCH SÂN VẬN ĐỘNG =============");
            for (Stadium s : stadiums) {
                System.out.printf("%-10s | %-35s | %-12d | %s%n",
                        s.getStadiumId(), s.getStadiumName(), s.getCapacity(), s.getAddress());
            }
        } else if ("6".equals(choice)) {
            System.out.print("Nhập mã sân vận động (VD: STD01): ");
            String stadiumId = this.scanner.nextLine().trim();
            List<SeatSection> sections = this.stadiumController.getSections(stadiumId);
            if (sections.isEmpty()) {
                System.out.println("[INFO] Không tìm thấy khán đài nào thuộc sân " + stadiumId);
            } else {
                System.out.println("============= DANH SÁCH KHÁN ĐÀI (" + stadiumId + ") =============");
                for (SeatSection sec : sections) {
                    System.out.printf("%-15s | %-25s | %-12s | %d ghế%n",
                            sec.getSectionId(), sec.getSectionName(),
                            sec.getSectionType() != null ? sec.getSectionType().name() : "N/A",
                            sec.getTotalSeats());
                }
                System.out.print("Nhập mã khán đài muốn xem sơ đồ ghế ASCII (VD: " + sections.get(0).getSectionId() + "): ");
                String secId = this.scanner.nextLine().trim();
                String map = this.stadiumController.buildSeatMap(secId);
                System.out.println(map);
            }
        } else if ("7".equals(choice)) {
            System.out.println("\n============= ĐĂNG NHẬP =============");
            System.out.print("Tên đăng nhập: ");
            String u = this.scanner.nextLine().trim();
            System.out.print("Mật khẩu: ");
            String p = this.scanner.nextLine().trim();
            Account acc = this.authController.login(u, p);
            if (acc != null) {
                this.currentUser = acc;
                System.out.println("[SUCCESS] Đăng nhập thành công! Xin chào " + acc.getUsername());
            } else {
                System.out.println("[FAILED] Đăng nhập thất bại. Sai tài khoản, mật khẩu hoặc tài khoản chưa ACTIVE.");
            }
        } else if ("8".equals(choice)) {
            System.out.println("\n============= ĐĂNG KÝ TÀI KHOẢN FAN =============");
            System.out.print("Username: ");
            String fu = this.scanner.nextLine().trim();
            System.out.print("Password: ");
            String fp = this.scanner.nextLine().trim();
            System.out.print("Họ và tên: ");
            String fname = this.scanner.nextLine().trim();
            System.out.print("Email: ");
            String femail = this.scanner.nextLine().trim();
            System.out.print("Số điện thoại: ");
            String fphone = this.scanner.nextLine().trim();
            boolean ok = this.authController.registerFan(fu, fp, fname, femail, fphone);
            if (ok) {
                System.out.println("[SUCCESS] Đăng ký Fan thành công! Bạn có thể đăng nhập ngay.");
            } else {
                System.out.println("[FAILED] Đăng ký thất bại. Kiểm tra trùng username/email/phone.");
            }
        } else if ("9".equals(choice)) {
            System.out.println("\n============= ĐĂNG KÝ BAN TỔ CHỨC (ORGANIZER) =============");
            System.out.print("Username: ");
            String ou = this.scanner.nextLine().trim();
            System.out.print("Password: ");
            String op = this.scanner.nextLine().trim();
            System.out.print("Tên tổ chức: ");
            String orgName = this.scanner.nextLine().trim();
            System.out.print("Email: ");
            String oemail = this.scanner.nextLine().trim();
            System.out.print("Số điện thoại: ");
            String ophone = this.scanner.nextLine().trim();
            boolean ok = this.authController.registerOrganizer(ou, op, orgName, oemail, ophone);
            if (ok) {
                System.out.println("[SUCCESS] Đăng ký Organizer thành công! Trạng thái PENDING, chờ Admin duyệt.");
            } else {
                System.out.println("[FAILED] Đăng ký thất bại. Kiểm tra lại thông tin nhập.");
            }
        } else if ("0".equals(choice)) {
            this.running = false;
        } else {
            System.out.println("[WARNING] Lựa chọn không hợp lệ, vui lòng thử lại.");
        }
    }

    public void displayFanMenu() {
        System.out.println();
        System.out.println("==================== MENU NGƯỜI HÂM MỘ (FAN) ====================");
        System.out.println("Tài khoản: " + this.currentUser.getUsername() + " | ID: " + this.currentUser.getId());
        System.out.println("1. Xem danh sách trận đấu");
        System.out.println("2. Tìm kiếm trận đấu");
        System.out.println("3. Lọc trận đấu theo tiêu chí");
        System.out.println("4. Xem sơ đồ ghế khán đài");
        System.out.println("5. Đặt vé xem trận đấu (Tối đa 4 vé)");
        System.out.println("6. Xem lịch sử đặt vé của tôi");
        System.out.println("7. Hủy vé đã đặt");
        System.out.println("8. Xem hồ sơ cá nhân");
        System.out.println("9. Đổi mật khẩu");
        System.out.println("0. Đăng xuất");
        System.out.println("=================================================================");
        System.out.print("Vui lòng chọn chức năng (0-9): ");
        String choice = this.scanner.nextLine().trim();

        if ("1".equals(choice)) {
            this.matchView.displayMatches(this.matchController.viewAllMatches());
        } else if ("2".equals(choice)) {
            this.matchView.displayMatches(this.matchController.searchMatches(this.matchView.promptSearchKeyword()));
        } else if ("3".equals(choice)) {
            this.matchView.displayMatches(this.matchController.filterMatches(this.matchView.promptFilterCriteria()));
        } else if ("4".equals(choice)) {
            System.out.print("Nhập mã khán đài muốn xem sơ đồ ghế (VD: SEC_STD01_A): ");
            String secId = this.scanner.nextLine().trim();
            System.out.println(this.stadiumController.buildSeatMap(secId));
        } else if ("5".equals(choice)) {
            System.out.print("Nhập mã khán đài bạn muốn chọn ghế (VD: SEC_STD01_A): ");
            String sectionId = this.scanner.nextLine().trim();
            List<Seat> availableSeats = this.stadiumController.getAvailableSeats(sectionId);
            this.bookingView.displayBookingForm(availableSeats);
        } else if ("6".equals(choice)) {
            List<BookingTransaction> txns = this.bookingController.viewBookingHistory(this.currentUser.getId());
            this.bookingView.displayBookingHistory(txns);
        } else if ("7".equals(choice)) {
            this.bookingView.displayCancelTicketForm();
        } else if ("8".equals(choice)) {
            Account acc = this.authController.viewProfile(this.currentUser.getId());
            if (acc != null) {
                System.out.println("\n============= HỒ SƠ CÁ NHÂN =============");
                System.out.println("Mã tài khoản : " + acc.getId());
                System.out.println("Tên đăng nhập: " + acc.getUsername());
                System.out.println("Email        : " + (acc.getEmail() != null ? acc.getEmail() : ""));
                System.out.println("Số điện thoại: " + (acc.getPhone() != null ? acc.getPhone() : ""));
                System.out.println("Trạng thái   : " + acc.getStatus());
                if (acc instanceof Fan) {
                    System.out.println("Họ và tên    : " + ((Fan) acc).getFullName());
                    System.out.println("Hạng Fan     : " + ((Fan) acc).getFanType());
                }
                System.out.println("=========================================");
            }
        } else if ("9".equals(choice)) {
            System.out.println("\n============= ĐỔI MẬT KHẨU =============");
            System.out.print("Mật khẩu hiện tại: ");
            String oldPass = this.scanner.nextLine().trim();
            System.out.print("Mật khẩu mới    : ");
            String newPass = this.scanner.nextLine().trim();
            boolean ok = this.authController.changePassword(this.currentUser.getId(), oldPass, newPass);
            if (ok) {
                System.out.println("[SUCCESS] Đổi mật khẩu thành công!");
            } else {
                System.out.println("[FAILED] Đổi mật khẩu thất bại. Mật khẩu cũ không chính xác hoặc không hợp lệ.");
            }
        } else if ("0".equals(choice)) {
            this.authController.logout(this.currentUser.getId());
            this.currentUser = null;
            System.out.println("[INFO] Đã đăng xuất thành công.");
        } else {
            System.out.println("[WARNING] Lựa chọn không hợp lệ, vui lòng thử lại.");
        }
    }

    public void displayOrganizerMenu() {
        System.out.println();
        System.out.println("==================== MENU BAN TỔ CHỨC (ORGANIZER) ====================");
        System.out.println("Đơn vị: " + this.currentUser.getUsername() + " | ID: " + this.currentUser.getId());
        System.out.println("1. Xem danh sách trận đấu");
        System.out.println("2. Tạo sự kiện trận đấu mới");
        System.out.println("3. Cập nhật thông tin trận đấu");
        System.out.println("4. Xóa trận đấu");
        System.out.println("5. Cấu hình giá vé theo khán đài");
        System.out.println("6. Tự động phát hành vé cho trận đấu");
        System.out.println("7. Xem báo cáo doanh thu bán vé");
        System.out.println("8. Xem hồ sơ đơn vị");
        System.out.println("9. Đổi mật khẩu");
        System.out.println("0. Đăng xuất");
        System.out.println("======================================================================");
        System.out.print("Vui lòng chọn chức năng (0-9): ");
        String choice = this.scanner.nextLine().trim();

        if ("1".equals(choice)) {
            this.matchView.displayMatches(this.matchController.viewAllMatches());
        } else if ("2".equals(choice)) {
            this.matchView.displayCreateMatchForm();
        } else if ("3".equals(choice)) {
            this.matchView.displayUpdateMatchForm();
        } else if ("4".equals(choice)) {
            System.out.print("Nhập mã trận đấu muốn xóa: ");
            String matchId = this.scanner.nextLine().trim();
            boolean ok = this.matchController.deleteMatch(matchId);
            if (ok) {
                System.out.println("[SUCCESS] Xóa trận đấu " + matchId + " thành công!");
            } else {
                System.out.println("[FAILED] Xóa trận đấu thất bại. Mã trận không tồn tại.");
            }
        } else if ("5".equals(choice)) {
            this.organizerView.displayPricingConfigForm();
        } else if ("6".equals(choice)) {
            this.organizerView.displayGenerateTicketsForm();
        } else if ("7".equals(choice)) {
            System.out.print("Nhập mã trận đấu cần xem báo cáo doanh thu (VD: MTH01): ");
            String matchId = this.scanner.nextLine().trim();
            Map<String, Object> sales = this.ticketController.viewTicketSalesReport(matchId);
            this.organizerView.displaySalesReport(sales);
        } else if ("8".equals(choice)) {
            Account acc = this.authController.viewProfile(this.currentUser.getId());
            if (acc != null) {
                System.out.println("\n============= THÔNG TIN ĐƠN VỊ =============");
                System.out.println("Mã ban tổ chức: " + acc.getId());
                System.out.println("Username      : " + acc.getUsername());
                System.out.println("Email         : " + (acc.getEmail() != null ? acc.getEmail() : ""));
                System.out.println("Số điện thoại : " + (acc.getPhone() != null ? acc.getPhone() : ""));
                System.out.println("Trạng thái    : " + acc.getStatus());
                if (acc instanceof Organizer) {
                    System.out.println("Tên tổ chức   : " + ((Organizer) acc).getOrgName());
                }
                System.out.println("============================================");
            }
        } else if ("9".equals(choice)) {
            System.out.println("\n============= ĐỔI MẬT KHẨU =============");
            System.out.print("Mật khẩu hiện tại: ");
            String oldPass = this.scanner.nextLine().trim();
            System.out.print("Mật khẩu mới    : ");
            String newPass = this.scanner.nextLine().trim();
            boolean ok = this.authController.changePassword(this.currentUser.getId(), oldPass, newPass);
            if (ok) {
                System.out.println("[SUCCESS] Đổi mật khẩu thành công!");
            } else {
                System.out.println("[FAILED] Đổi mật khẩu thất bại.");
            }
        } else if ("0".equals(choice)) {
            this.authController.logout(this.currentUser.getId());
            this.currentUser = null;
            System.out.println("[INFO] Đã đăng xuất thành công.");
        } else {
            System.out.println("[WARNING] Lựa chọn không hợp lệ, vui lòng thử lại.");
        }
    }

    public void displayAdminMenu() {
        System.out.println();
        System.out.println("==================== MENU QUẢN TRỊ VIÊN (ADMIN) ====================");
        System.out.println("Quản trị viên: " + this.currentUser.getUsername() + " | ID: " + this.currentUser.getId());
        System.out.println("1. Xem danh sách toàn bộ trận đấu");
        System.out.println("2. Xem danh sách sân vận động & khán đài");
        System.out.println("3. Xem hồ sơ cá nhân");
        System.out.println("0. Đăng xuất");
        System.out.println("====================================================================");
        System.out.print("Vui lòng chọn chức năng (0-3): ");
        String choice = this.scanner.nextLine().trim();

        if ("1".equals(choice)) {
            this.matchView.displayMatches(this.matchController.viewAllMatches());
        } else if ("2".equals(choice)) {
            List<Stadium> stadiums = this.stadiumController.getAllStadiums();
            System.out.println("\n============= DANH SÁCH SÂN VẬN ĐỘNG =============");
            for (Stadium s : stadiums) {
                System.out.printf("%-10s | %-35s | %-12d | %s%n",
                        s.getStadiumId(), s.getStadiumName(), s.getCapacity(), s.getAddress());
            }
        } else if ("3".equals(choice)) {
            Account acc = this.authController.viewProfile(this.currentUser.getId());
            if (acc != null) {
                System.out.println("\n============= HỒ SƠ ADMIN =============");
                System.out.println("Mã Admin     : " + acc.getId());
                System.out.println("Username     : " + acc.getUsername());
                System.out.println("Email        : " + (acc.getEmail() != null ? acc.getEmail() : ""));
                System.out.println("Số điện thoại: " + (acc.getPhone() != null ? acc.getPhone() : ""));
                System.out.println("========================================");
            }
        } else if ("0".equals(choice)) {
            this.authController.logout(this.currentUser.getId());
            this.currentUser = null;
            System.out.println("[INFO] Đã đăng xuất thành công.");
        } else {
            System.out.println("[WARNING] Lựa chọn không hợp lệ, vui lòng thử lại.");
        }
    }
}
