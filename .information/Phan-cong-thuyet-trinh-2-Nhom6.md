# KẾ HOẠCH & BẢNG PHÂN CÔNG THUYẾT TRÌNH BẢO VỆ DỰ ÁN 2 (ĐẾN HẾT TUẦN 7)
## HỆ THỐNG MÔ PHỎNG ĐẶT VÉ SÂN VẬN ĐỘNG (STADIUM TICKET BOOKING SIMULATION)

> **Môn học:** OOP with Java (LAB211 - Semester 3, FPT University)  
> **Nhóm thực hiện:** Nhóm 6  
> **Leader & Reviewer:** Huỳnh Hoàng Siêu  
> **Các thành viên:** Huỳnh Hoàng Siêu, Nguyễn Tuấn Kiệt, Đinh Trọng Nam, Lê Chấn Huy, Phạm Vũ Gia Văn  
> **Phạm vi nghiệm thu:** **Chính xác đến HẾT GIAI ĐOẠN TUẦN 7** (Trọng tâm: 3 Cơ chế Đồng bộ hóa chống Double Booking `FILE_LOCK`, `SYNCHRONIZED`, `OPTIMISTIC` & An toàn Dữ liệu Giao dịch). Tuyệt đối **KHÔNG** đưa các chức năng/class dự kiến của Tuần 8 vào.  
> **Ràng buộc thép:** **100% Class và Method phân công phải trùng khớp tuyệt đối** với mã nguồn thực tế đang tồn tại trong thư mục `src/main/java` (đúng 49 files Java). Tuyệt đối **KHÔNG** có các class giả định như `AdminController`, `SimulatorController`, `SimulatorView`.  
> **Nguyên tắc phân định rõ ràng (Phương án 1 - Chuẩn xác lịch sử & thực tế):**  
> - **Lê Chấn Huy:** Chuyên trách **Tầng Nghiệp vụ Bán vé, Vé & Giao dịch (Business Layer)**.  
> - **Phạm Vũ Gia Văn:** Chuyên trách **Tầng Giải thuật Đa luồng, Khóa Đồng bộ & Đo kiểm Hiệu năng (Concurrency & Benchmark Layer)**.  
> **File Word hoàn chỉnh:** [Phan-cong-thuyet-trinh-2-Nhom6-Chuan.docx](file:///d:/Github/Group6_Stadium-Ticket-Booking-Simulation/.information/Phan-cong-thuyet-trinh-2-Nhom6-Chuan.docx) | [Phan-cong-thuyet-trinh-2-Nhom6.docx](file:///d:/Github/Group6_Stadium-Ticket-Booking-Simulation/.information/Phan-cong-thuyet-trinh-2-Nhom6.docx)

---

## PHẦN I: TIẾN TRÌNH THUYẾT TRÌNH & QUY TRÌNH 4 BƯỚC CHO MỖI CLASS

### 1. Cấu trúc Buổi Thuyết trình (Top-Down Flow)
1. **Khởi động & Phần trình bày của Leader Huỳnh Hoàng Siêu:** Leader mở đầu bằng **Live Demo toàn diện từ hàm `Main.main()`**, sau đó **TRÌNH BÀY LUÔN** phần mã nguồn mình trực tiếp xây dựng: Khung sườn điều hướng `MainView`, Quản lý trận đấu `MatchController`, Quản trị Admin duyệt Ban tổ chức qua `AdminView`, và Kiến trúc nền tảng `CsvRepository<T>` & `BaseEntity`.
2. **Thuyết trình chi tiết tiếp nối của 4 thành viên:** Lần lượt **Nguyễn Tuấn Kiệt** (Tài khoản & An toàn dữ liệu) $\rightarrow$ **Đinh Trọng Nam** (Sân vận động, Ghế & FileLock) $\rightarrow$ **Lê Chấn Huy** (Tầng Nghiệp vụ Bán vé, Vé & Giao dịch) $\rightarrow$ **Phạm Vũ Gia Văn** (Tầng Giải thuật Đa luồng, Khóa Đồng bộ & Đo kiểm Hiệu năng).
3. **Tổng kết & Báo cáo Kiểm toán (Closing & Audit Report):** Leader Huỳnh Hoàng Siêu chốt lại toàn bộ 4 tiêu chí Bàn Tay Sắt và mời Thầy vấn đáp phản biện.

### 2. Quy trình 4 bước bắt buộc khi thuyết minh mỗi Class
Mỗi thành viên khi bước lên bục thuyết trình về bất kỳ Class nào đều phải tuần tự đi qua đúng 4 bước sau:
* **Bước 1 — Vị trí trong Sơ đồ lớp (Class Diagram Mapping):** Chỉ rõ class nằm ở package nào trong `org.stadium.*`, kế thừa class nào (`BaseEntity`, `Account`, `CsvRepository<T>`), phụ thuộc hay liên kết với các class nào trong sơ đồ `mermaid-class-diagram.docx`.
* **Bước 2 — Bản đồ Use Case (Usecase Mapping):** Nêu rõ class phục vụ Actor nào (Guest, Fan, Organizer, Admin) và hiện thực hóa Use Case cụ thể nào theo yêu cầu đề tài gốc LAB211.
* **Bước 3 — Tác dụng từng hàm trong Class thực tế (Method Purpose):** Trình chiếu đúng chữ ký phương thức (signature) có trong code thực tế, giải thích rõ các tham số truyền vào và kết quả trả về.
* **Bước 4 — Mổ xẻ Kỹ thuật Lập trình chuyên sâu (Technical Deep-Dive):** Đi sâu vào giải thuật then chốt: Khóa đồng bộ (`FileLock`, `synchronized`, `optimistic CAS`), cơ chế an toàn I/O (`Atomic File Replacement`), xử lý ngoại lệ vòng lặp, kiểm soát luồng dữ liệu, tuân thủ 100% tiền tố `this.` và cấm tuyệt đối từ khóa `static`.

---

## PHẦN II: MA TRẬN PHÂN CÔNG THUYẾT TRÌNH (KHỚP 100% CODE THỰC TẾ TUẦN 7)

| Thứ tự | Thành viên | Domain chuyên trách (Tuần 7) | Tầng View (Console UI) | Tầng Controller | Tầng Repository & Model (Thực tế 100%) |
| :---: | :--- | :--- | :--- | :--- | :--- |
| **Mở đầu & 1** | **Huỳnh Hoàng Siêu** *(Leader)* | **Live Demo Tổng quan & Trận đấu, Admin, Core Generic** | `MainView` (start, routing 4 role), `MatchView`, `AdminView` | `MatchController` (& Wiring điều phối không static) | **Repo:** `MatchRepo`, `CsvRepository<T>` (Generic Base)<br>**Model:** `BaseEntity`, `Match`, `MatchFilter`, `AccountSearchCriteria`, `MatchStatus` |
| **2** | **Nguyễn Tuấn Kiệt** | **Account, Auth & Data Safety** | `AuthView` | `AuthController` | **Repo:** `FanRepo`, `OrganizerRepo`, `AdminRepo`, `TransactionRepo` (Atomic Write), `TicketRepo`<br>**Model:** `Account`, `Fan`, `Organizer`, `Admin`, `UserRole`, `AccountStatus`<br>**Test:** `DataSafetyConcurrencyTest` (6/6 PASS) |
| **3** | **Đinh Trọng Nam** | **Sân vận động, Ghế & FileLock** | `SeatMapView`, `StadiumView` | `StadiumController` | **Repo:** `StadiumRepo`, `SectionRepo`, `SeatRepo` (Java NIO `FileLock` trên `seats.csv.lock`)<br>**Model:** `Stadium`, `SeatSection`, `Seat` (trường `version`), `SeatType`, `SeatStatus`<br>**Test:** `SeatMapViewTest`, `StadiumControllerTest` |
| **4** | **Lê Chấn Huy** | **Tầng Nghiệp vụ Bán vé, Vé & Giao dịch (Business Layer)** | `BookingView`, `OrganizerView` | `TicketController` & Nghiệp vụ `BookingController` (`cancelTicket`, `viewBookingHistory`, `getTicketDetails`) | **Repo:** `TicketPricingRepo`, `TicketRepo`<br>**Model:** `TicketPricing`, `Ticket`, `BookingTransaction`, `TicketStatus`, `TransactionStatus`<br>**Test:** `BookingControllerTest` (7/7 PASS) |
| **5** | **Phạm Vũ Gia Văn** | **Tầng Giải thuật Đa luồng & Đo kiểm (Concurrency Layer)** | Console Benchmark UI (`ConcurrencyHarnessTest`) | Giải thuật Concurrency trong `BookingController` (Khối `SYNCHRONIZED` & Vòng lặp Retry `OPTIMISTIC`) | **Repo:** `SimulationResultRepo` & Thuật toán CAS `SeatRepo` (`updateWithOptimisticLock`)<br>**Model:** `SimulationResult`, Enum `SynchronizationMechanism`<br>**Test:** `ConcurrencyHarnessTest` (28/28 PASS) |
| **Tổng kết** | **Huỳnh Hoàng Siêu** *(Leader)* | **Nghiệm thu Dự án & Q&A Phản biện** | Tổng kết UI Console | Tổng kết Kiến trúc MVC & Bàn Tay Sắt | **Báo cáo Kiểm toán (Audit Report) Tuần 7:**<br>100% `this.`, 0% `static`, 49/49 files PASS, 0 double booking |

---

## PHẦN III: KỊCH BẢN THUYẾT MINH CHI TIẾT THEO TỪNG THÀNH VIÊN

### 1. GIAI ĐOẠN 1: LEADER HUỲNH HOÀNG SIÊU — MỞ ĐẦU (LIVE DEMO) & PHÂN HỆ TRẬN ĐẤU, ADMIN, CORE
* **Mục tiêu:** Mở màn ấn tượng bằng Live Demo sản phẩm thực tế chạy mượt mà từ hàm `Main.main()`; sau đó trực tiếp thuyết minh luôn phần mã nguồn nền tảng mà mình xây dựng.

#### A. Kịch bản 4 bước Live Demo Hệ thống
1. *Khởi động Console & Menu Điều hướng:* Chạy `Main.main()`, giới thiệu menu phân cấp theo 4 vai trò rõ rệt: Guest, Fan, Organizer, Admin trong `MainView`.
2. *Luồng Khách vãng lai (Guest):* Xem danh sách các trận đấu bóng đá đang mở bán và tìm kiếm trận đấu theo từ khóa.
3. *Luồng Fan Đặt vé Flash Sale:* Đăng nhập tài khoản Fan (`fan1` / `123456`), chọn trận đấu, hiển thị sơ đồ khán đài ASCII hai chiều sinh động (`[O]` khả dụng, `[X]` đã bán, `[L]` bị khóa), tiến hành đặt 2 ghế liền nhau, thanh toán thành công và in biên lai giao dịch chi tiết.
4. *Luồng Quản trị & Concurrency:* Đăng nhập quyền Admin, hiển thị dashboard duyệt tài khoản Ban tổ chức; đồng thời chạy bài kiểm thử `ConcurrencyHarnessTest` chứng minh cả 3 cơ chế `FILE_LOCK`, `SYNCHRONIZED`, `OPTIMISTIC` đều đạt 0 double booking khi bị bắn tải đồng thời.

#### B. Tầng View: `MainView`, `MatchView` & `AdminView`
1. **Vị trí Sơ đồ lớp:** Tầng 1 (View Layer), package `org.stadium.view`.
2. **Use Case Mapping:** Khung sườn điều hướng trung tâm cho toàn bộ 4 vai trò (Guest, Fan, Organizer, Admin); Quản lý sự kiện trận đấu và Bảng điều khiển Quản trị viên duyệt Ban tổ chức.
3. **Tác dụng từng hàm trong code thực tế:**
   - `MainView.start()`: Vòng lặp chính của toàn bộ chương trình, phân luồng điều hướng menu theo vai trò người dùng (`displayGuestMenu`, `displayFanMenu`, `displayOrganizerMenu`, `displayAdminMenu`).
   - `MatchView`: `displayMatches(matches)`, `displayMatchDetails(match)`, `displayCreateMatchForm()`, `displayUpdateMatchForm()`, `promptSearchKeyword()`, `promptFilterCriteria()`.
   - `AdminView`: `displayAdminDashboard()`, `displayPendingOrganizers(pendingList)`, `approveOrganizer()`, `rejectOrganizer()` — Giao diện Admin duyệt và từ chối Ban tổ chức (tương tác trực tiếp với `AuthController`).
4. **Kỹ thuật Lập trình:** Kỹ thuật Session Context duy trì `currentUser` và `UserRole`; xử lý ngoại lệ vòng lặp nhập sai Console; tuân thủ 100% tiền tố `this.`.

#### C. Tầng Controller: `MatchController` & Vai trò Kiến trúc Điều phối Wiring
1. **Vị trí Sơ đồ lớp:** Tầng 2 (Controller Layer), package `org.stadium.controller`. Nắm giữ `MatchRepository`.
2. **Use Case Mapping:** Đáp ứng trọn vẹn yêu cầu CRUD Match (cho Ban tổ chức) và tra cứu trận đấu (cho Khách/Fan) theo chuẩn Rubric LAB211.
3. **Tác dụng từng hàm trong code thực tế:**
   - `viewAllMatches()`: Lấy danh sách toàn bộ các trận đấu.
   - `searchMatches(String keyword)`: Tìm kiếm trận đấu linh hoạt theo tên trận, đội A hoặc đội B (không phân biệt hoa thường).
   - `filterMatches(MatchFilter filter)`: Lọc trận đấu đa tiêu chí qua đối tượng `MatchFilter`.
   - `createMatch(title, teamA, teamB, stadiumId, start, end, orgId)`: Tự động sinh mã `MTHxx` và khởi tạo trận đấu mới với trạng thái `SCHEDULED`.
   - `updateMatch(matchId, title, start, end, status)`: Cập nhật thông tin chi tiết và chuyển trạng thái trận đấu (`SCHEDULED` $\rightarrow$ `LIVE` $\rightarrow$ `FINISHED` / `CANCELLED`).
   - `deleteMatch(String matchId)`: Xóa trận đấu khỏi hệ thống.
4. **Kỹ thuật Kiến trúc Điều phối (Controller Wiring):**
   - Siêu giải thích cách `MainView` khởi tạo và kết nối các Controller độc lập (`AuthController`, `MatchController`, `StadiumController`, `TicketController`, `BookingController`) mà không hề sử dụng bất kỳ từ khóa `static` nào, đảm bảo tính đóng gói (Encapsulation) và Dependency Injection thủ công thuần khiết.

#### D. Tầng Repository & Thiết kế Generic Cốt lõi: `MatchRepository` & `CsvRepository<T>`
1. **Vị trí Sơ đồ lớp:** Tầng 3 (Repository Layer), package `org.stadium.repository`. `CsvRepository<T extends BaseEntity>` là lớp generic trừu tượng nền tảng mà toàn bộ 11 repositories trong đồ án đều kế thừa.
2. **Use Case Mapping:** Đọc và ghi dữ liệu CSV bền vững cho toàn bộ hệ thống.
3. **Kỹ nghệ Thiết kế Generic Đỉnh cao:**
   - Lớp `CsvRepository<T extends BaseEntity>` đóng gói sẵn toàn bộ các phương thức CRUD dùng chung: `findAll()`, `findById(String id)`, `findByCondition(Predicate<T> predicate)`, `save(T entity)`, `deleteById(String id)`, `loadFromFile()`, `saveToFile()`.
   - Triệt tiêu 100% tình trạng trùng lặp mã nguồn (nguyên lý DRY - Don't Repeat Yourself).
   - Quản lý tài nguyên file an toàn qua cú pháp `try-with-resources` và bộ mã hóa UTF-8, tự động đóng stream tránh rò rỉ bộ nhớ.

#### E. Tầng Model: `BaseEntity`, `Match`, `MatchFilter`, `AccountSearchCriteria` & Enums
1. **Vị trí Sơ đồ lớp:** Tầng 4 & 5. `BaseEntity` là lớp cha trừu tượng cao nhất của toàn bộ Entity trong hệ thống.
2. **Tác dụng & Kỹ thuật:**
   - `BaseEntity` định nghĩa 3 phương thức trừu tượng cốt lõi: `getId()`, `toCsvLine()`, `fromCsvLine(String line)`.
   - `Match` kế thừa `BaseEntity`, quản lý đầy đủ các trường thời gian, sân thi đấu và 2 đội bóng.
   - `MatchFilter` và `AccountSearchCriteria` áp dụng mô hình Specification Pattern để đóng gói tiêu chí tìm kiếm linh hoạt. Enum `MatchStatus`.
3. **Chuyển tiếp:** Leader Siêu chuyển lời mời bạn **Nguyễn Tuấn Kiệt** tiếp nối với phân hệ Tài khoản, Xác thực và An toàn dữ liệu.

---

### 2. GIAI ĐOẠN 2: THÀNH VIÊN NGUYỄN TUẤN KIỆT — PHÂN HỆ TÀI KHOẢN, XÁC THỰC & AN TOÀN DỮ LIỆU
* **Domain chuyên trách:** Phân hệ Tài khoản người dùng, Xác thực & Phiên làm việc (Session), Kỹ thuật ghi File nguyên tử (Atomic File Replacement) & Thread-Safe ID Generator.
* **Trình tự thuyết trình:** `AuthView` $\rightarrow$ `AuthController` $\rightarrow$ Repositories (`FanRepo`, `OrganizerRepo`, `AdminRepo`, `TransactionRepo`, `TicketRepo`) $\rightarrow$ Models (`Account`, `Fan`, `Organizer`, `Admin`, Enums) $\rightarrow$ `DataSafetyConcurrencyTest`.

#### A. Tầng View: Class `AuthView`
1. **Vị trí Sơ đồ lớp:** Tầng 1 (View), package `org.stadium.view`. Nắm giữ tham chiếu trực tiếp đến `AuthController` và `Scanner`.
2. **Use Case Mapping:** Đăng nhập hệ thống, Đăng ký Fan, Đăng ký Organizer, Xem hồ sơ cá nhân, Cập nhật thông tin và Đổi mật khẩu.
3. **Tác dụng từng hàm trong code thực tế:**
   - `displayLoginForm()`: Nhập `username` và `password`, gọi `authController.login()`, trả về đối tượng `Account` hoặc báo lỗi cụ thể.
   - `displayFanRegisterForm()`: Thu thập thông tin Fan (username, password, tên, email, SĐT) và chuyển xuống Controller.
   - `displayOrganizerRegisterForm()`: Thu thập thông tin Organizer (tên đơn vị tổ chức, giấy phép/email) để đăng ký với trạng thái `PENDING`.
   - `displayProfile(Account account)`: In bảng thông tin hồ sơ người dùng ra màn hình Console.
   - `displayChangePasswordForm()`: Thu thập mật khẩu cũ và mật khẩu mới để thực hiện đổi mật khẩu.
   - `displayUpdateProfileForm()`: Cập nhật email và số điện thoại liên lạc.
4. **Kỹ thuật Lập trình:** Kiểm tra validation regex cho Email (`^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$`) và SĐT (10 số); chống NPE; tuân thủ 100% tiền tố `this.`.

#### B. Tầng Controller: Class `AuthController`
1. **Vị trí Sơ đồ lớp:** Tầng 2 (Controller), package `org.stadium.controller`. Quản lý `FanRepository`, `OrganizerRepository`, `AdminRepository`.
2. **Use Case Mapping:** Bộ não xử lý nghiệp vụ xác thực, phân quyền và duy trì phiên làm việc (Session).
3. **Tác dụng từng hàm trong code thực tế:**
   - `login(String username, String pass)`: Xác thực thông tin đăng nhập, kiểm tra trạng thái tài khoản (`ACTIVE`, `PENDING`, `BLOCKED`).
   - `logout(String accountId)`: Hủy phiên làm việc và xóa ngữ cảnh `currentUser`.
   - `registerFan(...)`, `registerOrganizer(...)`: Kiểm tra trùng lặp `username` trên toàn bộ 3 bảng tài khoản, tự động sinh mã ID (`FANxx`, `ORGxx`) và lưu vào CSV.
   - `viewProfile(...)`, `updateProfile(...)`, `changePassword(...)`: Quản lý thông tin cá nhân và bảo mật tài khoản.
   - `getPendingOrganizers()`, `approveOrganizer(String id)`, `rejectOrganizer(String id)`: Cung cấp nghiệp vụ duyệt Ban tổ chức cho Admin.
4. **Kỹ thuật Lập trình:** Băm và so sánh mật khẩu qua `Account.validatePassword()`; thuật toán kiểm tra trùng lặp `username` đa bảng độc lập; tuân thủ 100% `this.`.

#### C. Tầng Repository & An toàn Dữ liệu (Điểm sáng Tuần 7 của Kiệt)
1. **Vị trí Sơ đồ lớp:** Tầng 3 (Repository), package `org.stadium.repository`. Kế thừa `CsvRepository<T>`.
2. **Kỹ thuật Atomic File Replacement trong `TransactionRepository` & `TicketRepository`:**
   - Khi ghi dữ liệu giao dịch dưới áp lực bắn tải dồn dập, việc ghi đè trực tiếp vào file CSV có thể làm file bị rách dòng (corrupted line) nếu JVM bị ngắt giữa chừng.
   - Kiệt triển khai cơ chế: Toàn bộ dữ liệu được ghi vào một file tạm `.tmp`, gọi `flush()` hoàn tất, sau đó sử dụng `Files.move(..., StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)` để tráo file tạm thành file chính thức ở cấp hệ điều hành. Cơ chế này cam kết tính nguyên tử 100%.
3. **Kỹ thuật Thread-Safe ID Generator:**
   - Trong `TransactionRepository.generateNextTransactionId()` và `TicketRepository.generateNextTicketId()`, khối khóa `synchronized(this)` kết hợp hàm quét `scanMaxIdNumber()` đảm bảo dù 20 threads cùng gọi sinh mã đồng thời thì các mã giao dịch `TXN01, TXN02...` không bao giờ bị trùng lặp.
4. **Minh chứng Kiểm thử `DataSafetyConcurrencyTest`:**
   - Chạy trực tiếp bộ test suite gồm 6 test cases PASS 100%: Sinh ID đa luồng không trùng lặp, ghi file dồn dập không rách dòng, tính toàn vẹn dữ liệu CSV được bảo toàn tuyệt đối.

#### D. Tầng Model: `Account`, `Fan`, `Organizer`, `Admin` & Enums
1. **Vị trí Sơ đồ lớp:** Tầng 4 & 5. `Account` kế thừa `BaseEntity`; `Fan`, `Organizer`, `Admin` kế thừa `Account`.
2. **Kỹ thuật:** 100% thuộc tính `private`, getter/setter có tiền tố `this.`. Hàm `fromCsvLine(String line)` bắt buộc sử dụng `split(",", -1)` để giữ nguyên các cột rỗng ở cuối dòng CSV. Enums: `UserRole`, `AccountStatus`.

---

### 3. GIAI ĐOẠN 3: THÀNH VIÊN ĐINH TRỌNG NAM — PHÂN HỆ SÂN VẬN ĐỘNG, SƠ ĐỒ GHẾ & FILE LOCK
* **Domain chuyên trách:** Cơ sở hạ tầng Sân vận động & Khán đài, Thuật toán dựng Sơ đồ ghế ASCII 2 chiều, Cơ chế đồng bộ số 1: Java NIO `FileLock` trên `SeatRepository`.
* **Trình tự thuyết trình:** `SeatMapView`, `StadiumView` $\rightarrow$ `StadiumController` $\rightarrow$ Repositories (`StadiumRepo`, `SectionRepo`, `SeatRepo`) $\rightarrow$ Models (`Stadium`, `SeatSection`, `Seat`, Enums) $\rightarrow$ `SeatMapViewTest`, `StadiumControllerTest`.

#### A. Tầng View: Class `SeatMapView` & `StadiumView`
1. **Vị trí Sơ đồ lớp:** Tầng 1 (View), package `org.stadium.view`. Gọi trực tiếp `StadiumController`.
2. **Use Case Mapping:** Hiển thị danh sách sân bãi, chi tiết khán đài và vẽ sơ đồ ghế ngồi trực quan cho Fan khi chọn chỗ.
3. **Tác dụng từng hàm trong code thực tế:**
   - `displaySeatMap(String asciiMap)`: Vẽ ma trận ghế 2 chiều ra màn hình Console với các ký hiệu trực quan: `[O]` (Ghế trống khả dụng), `[X]` (Ghế đã bán), `[L]` (Ghế bị khóa/bảo trì).
   - `displayAvailableSeats(List<Seat> seats)`: In bảng danh sách các ghế đang còn trống kèm loại ghế (`STANDARD`, `VIP`) và giá vé tương ứng.
   - `displayStadiumList(...)`, `displaySectionList(...)`, `displaySectionDetails(...)`: Hiển thị danh sách sân vận động và khán đài.
4. **Kỹ thuật Lập trình:** Căn chỉnh chuỗi qua `String.format()`, định dạng bảng Console chuyên nghiệp, tuân thủ 100% tiền tố `this.`.

#### B. Tầng Controller: Class `StadiumController`
1. **Vị trí Sơ đồ lớp:** Tầng 2 (Controller), package `org.stadium.controller`. Nắm giữ `StadiumRepository`, `SectionRepository`, `SeatRepository`.
2. **Use Case Mapping:** Điều phối toàn bộ dữ liệu sân vận động, khán đài và cấu trúc ghế ngồi.
3. **Tác dụng từng hàm trong code thực tế:**
   - `getAllStadiums()`: Lấy danh sách toàn bộ các sân vận động trong hệ thống.
   - `getSections(String stadiumId)`: Truy vấn danh sách các khán đài thuộc một sân vận động cụ thể.
   - `buildSeatMap(String sectionId)`: Thuật toán cốt lõi — chuyển đổi danh sách các đối tượng `Seat` trong một khán đài thành chuỗi bản đồ ASCII 2 chiều theo hàng (Row) và số thứ tự ghế (Seat Number).
   - `getSeats(String sectionId)`, `getAvailableSeats(String sectionId)`: Lấy toàn bộ ghế và lọc danh sách ghế khả dụng (`AVAILABLE`).
4. **Kỹ thuật Lập trình:** Thuật toán sắp xếp ghế theo `rowNumber` và `seatNumber`; tối ưu hóa nối chuỗi bằng `StringBuilder`; sử dụng Stream API để lọc ghế trống.

#### C. Tầng Repository & Cơ chế Đồng bộ số 1: `FILE_LOCK` (Điểm sáng Tuần 7 của Nam)
1. **Vị trí Sơ đồ lớp:** Tầng 3 (Repository), package `org.stadium.repository`. Kế thừa generic `CsvRepository<T>`.
2. **Kỹ thuật Java NIO `FileChannel` & `FileLock` trên `SeatRepository`:**
   - Nam phụ trách cơ chế đồng bộ hóa số 1 trong 3 cơ chế chống Double Booking của Tuần 7:
   - `acquireFileLock()`: Mở luồng `RandomAccessFile` trên file chuyên dụng `seats.csv.lock`, lấy đối tượng `FileChannel` và gọi `channel.lock()` để chiếm giữ khóa độc quyền ở cấp hệ điều hành (OS process-level lock).
   - **Giải pháp Phối hợp `jvmLock` (`ReentrantLock`):** Nếu nhiều thread trong cùng 1 JVM cùng gọi `FileChannel.lock()` trên một kênh file, Java NIO sẽ ném ra ngoại lệ `OverlappingFileLockException`. Do đó, Nam thiết kế một `jvmLock` (`ReentrantLock`) xếp hàng các thread nội bộ trước, thread nào thắng mới đại diện tiến trình đi chiếm `FileLock` của OS.
   - `releaseFileLock()`: Giải phóng `FileLock`, đóng `FileChannel`, đóng file lock và nhả `jvmLock` an toàn 100% trong khối `finally`.

#### D. Tầng Model: `Stadium`, `SeatSection`, `Seat` & Enums
1. **Vị trí Sơ đồ lớp:** Tầng 4 & 5. `Stadium` chứa danh sách `SeatSection`; `SeatSection` chứa danh sách `Seat`. Kế thừa `BaseEntity`.
2. **Kỹ thuật:** Entity `Seat` chứa thuộc tính `version` (int) phục vụ Optimistic Locking; hàm `isAvailable()` kiểm tra trạng thái ghế; Enum `SeatType` (`STANDARD`, `VIP`) và `SeatStatus` (`AVAILABLE`, `BOOKED`, `LOCKED`).

---

### 4. GIAI ĐOẠN 4: THÀNH VIÊN LÊ CHẤN HUY — TẦNG NGHIỆP VỤ BÁN VÉ, VÉ & GIAO DỊCH (BUSINESS LAYER)
* **Domain chuyên trách:** Toàn bộ luồng nghiệp vụ kinh doanh vé: Ticket Pricing, Ticket Generation, Booking Operations & Financial Reporting.
* **Trình tự thuyết trình:** `BookingView`, `OrganizerView` $\rightarrow$ `TicketController` & Luồng nghiệp vụ trong `BookingController` $\rightarrow$ Repositories (`TicketPricingRepo`, `TicketRepo`) $\rightarrow$ Models (`TicketPricing`, `Ticket`, `BookingTransaction`).

#### A. Tầng View: Class `BookingView` & `OrganizerView`
1. **Vị trí Sơ đồ lớp:** Tầng 1 (View), package `org.stadium.view`. Gọi `TicketController` và `BookingController`.
2. **Use Case Mapping:** Phục vụ luồng Đặt vé / Hủy vé của Fan và luồng Quản lý giá / Phát hành vé / Báo cáo doanh thu của Ban tổ chức.
3. **Tác dụng từng hàm trong code thực tế:**
   - `displayBookingForm(List<Seat> seats)`: Cho phép Fan nhập danh sách mã ghế muốn mua (tối đa 4 ghế/lần).
   - `showBookingResult(boolean success, String msg)`: In hóa đơn thanh toán thành công hoặc thông báo lỗi đặt vé.
   - `displayBookingHistory(List<BookingTransaction> txns)`: In bảng lịch sử các giao dịch đặt vé của Fan.
   - `displayCancelTicketForm()`: Form nhập mã vé muốn hủy.
   - `displayPricingConfigForm()`, `displayGenerateTicketsForm()`, `displayCancelTicketsForm()`, `displaySalesReport(...)`: Giao diện quản lý vé và báo cáo doanh thu cho Ban tổ chức.
4. **Kỹ thuật Lập trình:** Bắt lỗi trùng lặp mã ghế nhập vào; kiểm tra chặn mua quá 4 vé; định dạng tiền tệ Việt Nam (VNĐ).

#### B. Tầng Controller: `TicketController` & Luồng Nghiệp vụ Bán vé trong `BookingController`
1. **Vị trí Sơ đồ lớp:** Tầng 2 (Controller), package `org.stadium.controller`. Nắm giữ `SeatRepository`, `TicketRepository`, `TransactionRepository`, `TicketPricingRepository`.
2. **Use Case Mapping:** Xử lý toàn bộ logic nghiệp vụ kinh doanh vé và giao dịch bán hàng.
3. **Tác dụng từng hàm do Huy phụ trách:**
   - `configureTicketPricing(matchId, sectionId, price)`: Ban tổ chức thiết lập giá vé cho từng khán đài.
   - `generateTickets(matchId)`: Tự động phát hành toàn bộ vé cho các ghế trong sân theo bảng giá.
   - `cancelTickets(matchId)`: Hủy toàn bộ vé của một trận đấu bị hủy bỏ.
   - `viewTicketSalesReport(matchId)`: Tính toán tổng doanh thu, số vé đã bán và tỷ lệ lấp đầy khán đài.
   - `cancelTicket(ticketId, fanId)`: Hủy vé cá nhân của Fan, hoàn tiền giao dịch và trả trạng thái ghế về `AVAILABLE`.
   - `viewBookingHistory(fanId)`, `getTicketDetails(transactionId)`: Truy vấn lịch sử giao dịch và chi tiết vé.
4. **Kỹ thuật Lập trình (Tính toàn vẹn nghiệp vụ):**
   - **Tính Nguyên tử (Atomicity):** Trong critical section, đồng thời chuyển trạng thái toàn bộ các ghế sang `BOOKED`, tăng version, gán vé cho Fan và ghi nhận `BookingTransaction`; đảm bảo dữ liệu nhất quán tuyệt đối giữa 3 bảng CSV (`seats.csv`, `tickets.csv`, `transactions.csv`).
   - Ràng buộc toàn vẹn: Không cho phép phát hành vé trùng lặp hoặc hủy vé của trận đã diễn ra.

#### C. Tầng Repository: `TicketPricingRepository` & `TicketRepository`
1. **Vị trí Sơ đồ lớp:** Tầng 3 (Repository), package `org.stadium.repository`. Kế thừa `CsvRepository<T>`.
2. **Tác dụng hàm:** `findByMatchAndSection()`, `findBySeatAndMatch()`, `findByMatchId()`, `findByFanId()`. Toàn bộ I/O được đồng bộ hóa thread-safe.

#### D. Tầng Model: `TicketPricing`, `Ticket`, `BookingTransaction` & Enums
1. **Vị trí Sơ đồ lớp:** Tầng 4 & 5. 1 `BookingTransaction` liên kết với từ 1 đến 4 `Ticket`. Enums: `TicketStatus` (`AVAILABLE`, `BOOKED`, `CANCELLED`), `TransactionStatus` (`SUCCESS`, `FAILED`, `CANCELLED`).

---

### 5. GIAI ĐOẠN 5: THÀNH VIÊN PHẠM VŨ GIA VĂN — TẦNG GIẢI THUẬT ĐA LUỒNG & ĐO KIỂM HIỆU NĂNG (CONCURRENCY LAYER)
* **Domain chuyên trách:** Thử nghiệm Bắn tải Đa luồng Flash Sale, Cơ chế đồng bộ số 2: `SYNCHRONIZED`, Cơ chế đồng bộ số 3: `OPTIMISTIC` (Thuật toán CAS - Compare-And-Swap) & Benchmark Concurrency.
* **Trình tự thuyết trình:** Giao diện Đo kiểm Console (`ConcurrencyHarnessTest`) $\rightarrow$ Khối `SYNCHRONIZED` & Vòng lặp Retry `OPTIMISTIC` trong `BookingController.bookTickets` $\rightarrow$ Thuật toán CAS `SeatRepository.updateWithOptimisticLock` $\rightarrow$ `SimulationResultRepo` & Models.

#### A. Tầng Đo kiểm & Giao diện Đa luồng: `ConcurrencyHarnessTest`
1. **Vị trí & Vai trò:** Nằm tại package `org.stadium.controller` trong thư mục `src/test/java`, đóng vai trò như một bộ giao diện bắn tải đa luồng trực quan ra màn hình Console.
2. **Use Case Mapping:** Phục vụ bài toán thử nghiệm của Quản trị viên (Admin) nhằm tái hiện hiện tượng Race Condition khi hàng loạt Fan cùng bấm mua 1 chiếc ghế trong Flash Sale, đồng thời chứng minh 3 cơ chế đồng bộ loại bỏ 100% lỗi Double Booking.
3. **Kỹ thuật Lập trình Đỉnh cao của Văn:**
   - **Kỹ thuật Súng phát lệnh (`CountDownLatch`):** Sử dụng `readyLatch` để đợi toàn bộ các luồng (từ 2 đến 10 threads) khởi tạo xong và xếp hàng vào vị trí xuất phát, sau đó kích hoạt `startLatch.countDown()` để tất cả các luồng cùng lúc ùa vào gọi `bookTickets()`, tạo áp lực xung đột tối đa.
   - **Quản lý Luồng qua `ExecutorService` (ThreadPool):** Thu thập kết quả bất đồng bộ qua danh sách `Future<Boolean>`.
   - **Kết quả Thực nghiệm Tuyệt đối:** Vượt qua toàn bộ **28/28 kịch bản kiểm thử**, ghi nhận đúng duy nhất 1 giao dịch thành công cho mỗi ghế tranh chấp, **0 trường hợp double booking**.

#### B. Tầng Giải thuật Concurrency: Khối `SYNCHRONIZED` & Vòng lặp Retry `OPTIMISTIC` trong `BookingController`
1. **Vị trí Sơ đồ lớp:** Tầng 2 (Controller Layer), phương thức `bookTickets` trong `BookingController`.
2. **Kỹ thuật từng cơ chế Concurrency do Văn chủ trì:**
   - **Cơ chế `SYNCHRONIZED`:** Sử dụng khối khóa đồng bộ `synchronized(this.seatRepository)` bao bọc toàn bộ critical section kiểm tra và ghi vé. Cơ chế này chặn (block) các luồng khác ngoài cửa, đảm bảo tuần tự hóa tuyệt đối.
   - **Cơ chế `OPTIMISTIC`:** Không khóa luồng khi đọc dữ liệu. Luồng đọc version hiện tại của ghế, kiểm tra tính khả dụng và gọi `updateWithOptimisticLock`. Nếu phát hiện xung đột version (ghế đã bị luồng khác mua trước), kích hoạt vòng lặp Retry thử lại tối đa 5 lần (`maxRetries = 5`) trước khi trả về thất bại.
   - **Khối finally Rollback:** Nếu quá trình đặt nhiều ghế bị thất bại giữa chừng, tự động hoàn trả các ghế đã xí phần trước đó về trạng thái `AVAILABLE`.

#### C. Tầng Thuật toán CAS: `SeatRepository.updateWithOptimisticLock`
1. **Vị trí Sơ đồ lớp:** Tầng 3 (Repository Layer), package `org.stadium.repository`.
2. **Thuật toán CAS (Compare-And-Swap):**
   - Phương thức: `updateWithOptimisticLock(Seat seat, int expectedVersion)`
   - Nguyên lý hoạt động: Trước khi ghi đè dữ liệu, nạp lại thông tin ghế mới nhất từ file và so sánh `currentVersion` với `expectedVersion` mà luồng đã đọc lúc đầu.
   - Nếu `currentVersion == expectedVersion`: Phiên bản hợp lệ, gọi `seat.incrementVersion()` (tăng version lên 1), lưu dữ liệu vào file và trả về `true`.
   - Nếu `currentVersion != expectedVersion`: Dữ liệu đã bị luồng khác sửa đổi, lập tức từ chối ghi và trả về `false` để Controller kích hoạt vòng lặp Retry.

#### D. Tầng Model: `SimulationResultRepository`, `SimulationResult` & Enum `SynchronizationMechanism`
1. **Vị trí Sơ đồ lớp:** Tầng 3, 4 & 5. `SimulationResultRepository` kế thừa `CsvRepository<SimulationResult>`.
2. **Tác dụng:** Entity `SimulationResult` lưu trữ toàn bộ các chỉ số hiệu năng (threadCount, totalRequests, successCount, collisionCount, executionTimeMs); Enum định nghĩa đúng 4 cơ chế chuẩn: `NO_LOCK`, `FILE_LOCK`, `SYNCHRONIZED`, `OPTIMISTIC`.

---

### 6. GIAI ĐOẠN 6 (TỔNG KẾT): LEADER HUỲNH HOÀNG SIÊU — BÁO CÁO KIỂM TOÁN (AUDIT REPORT) & PHẢN BIỆN
Siêu đại diện nhóm 6 tổng kết 4 tiêu chuẩn thép Bàn Tay Sắt đã được hiện thực hóa trọn vẹn và mở đầu phần hỏi đáp:
1. **100% Tiền tố `this.`:** Đã kiểm toán toàn bộ mã nguồn, tất cả biến instance, getter, setter và internal method đều có tiền tố `this.`.
2. **0% Từ khóa `static`:** Ngoại trừ hàm `main` khởi chạy, tuyệt đối không có `static field`, `static helper` hay `static state` nào gây rò rỉ race condition ngầm.
3. **100% Khớp Mã nguồn Thực tế:** Đúng 49/49 files Java trong thư mục `src/main/java` hoạt động trơn tru, khớp 100% với Sơ đồ lớp chuẩn `mermaid-class-diagram.docx`.
4. **Vượt qua 100% Bài Test Đa luồng:** Giải quyết triệt để bài toán Double Booking trên cả 3 cơ chế `FILE_LOCK`, `SYNCHRONIZED`, `OPTIMISTIC` với 0 trường hợp trùng ghế.

---

## PHẦN IV: BỘ CÂU HỎI VẤN ĐÁP THƯỜNG GẶP (Q&A CHEAT-SHEET)

Dưới đây là các câu hỏi giảng viên hay hỏi xoáy nhất và câu trả lời chuẩn xác đã được chuẩn bị sẵn cho từng thành viên:

* **Câu 1 (Hỏi Kiệt): Vì sao trong hàm `fromCsvLine()` bắt buộc phải dùng `split(",", -1)` mà không dùng `split(",")` thông thường?**  
  👉 *Trả lời:* Nếu dùng `split(",")` thông thường, Java sẽ tự động cắt bỏ (discard) các phần tử rỗng ở cuối dòng. Trong các file CSV như `tickets.csv`, khi vé chưa được đặt thì các cột `transactionId`, `fanId`, `bookingDate` ở cuối dòng bị để trống. Nếu không có tham số `-1`, mảng `parts` trả về sẽ bị thiếu phần tử, dẫn đến ngoại lệ `ArrayIndexOutOfBoundsException` khi truy cập.

* **Câu 2 (Hỏi Nam): Tại sao khi dùng `FileLock` của Java NIO trên file `seats.csv.lock`, bạn vẫn phải dùng thêm `ReentrantLock` (`jvmLock`)?**  
  👉 *Trả lời:* Vì `FileChannel.lock()` của Java là khóa ở cấp tiến trình hệ điều hành (OS process-level). Trong cùng một tiến trình JVM, nếu 2 luồng (threads) cùng gọi `lock()` trên một kênh file thì Java NIO sẽ ném ngoại lệ `OverlappingFileLockException`. Do đó, em sử dụng `jvmLock` (`ReentrantLock`) để điều phối thứ tự xếp hàng giữa các thread nội bộ JVM trước, luồng nào giành quyền mới đại diện chiếm `FileLock` của hệ điều hành.

* **Câu 3 (Hỏi Huy): Trong phân hệ bán vé, nếu một Fan đặt cụm 4 ghế mà ghế thứ 4 bị xung đột không đặt được, hệ thống xử lý thế nào để đảm bảo tính nhất quán nghiệp vụ?**  
  👉 *Trả lời:* Hệ thống tuân thủ nghiêm ngặt Tính nguyên tử (Atomicity). Trong khối `try-finally` của phương thức `bookTickets` (đặc biệt ở cơ chế `OPTIMISTIC` do Văn xây dựng), nếu việc đặt 4 ghế không hoàn tất 100%, cờ `completed` vẫn là `false`. Khối `finally` sẽ tự động kích hoạt vòng lặp rollback, gọi `updateWithOptimisticLock` để hoàn trả các ghế đã xí phần trước đó về trạng thái `AVAILABLE`, cam kết không bao giờ làm rò rỉ trạng thái ghế của hệ thống và dữ liệu giữa 3 bảng `seats.csv`, `tickets.csv`, `transactions.csv` luôn đồng bộ 100%.

* **Câu 4 (Hỏi Văn): Sự khác nhau cốt lõi về nguyên lý và hiệu năng giữa cơ chế `SYNCHRONIZED` và `OPTIMISTIC` locking là gì?**  
  👉 *Trả lời:* `SYNCHRONIZED` là cơ chế Khóa bi quan (Pessimistic Locking), nó chặn (block) toàn bộ các luồng khác ngoài cửa critical section, đảm bảo an toàn tuyệt đối nhưng làm suy giảm thông lượng (Throughput) khi số lượng luồng tăng cao. Ngược lại, `OPTIMISTIC` là cơ chế Không khóa (Non-blocking), cho phép các luồng cùng đọc dữ liệu tự do và chỉ kiểm tra trường `version` ở thời điểm ghi (thuật toán CAS). Nếu có xung đột thì mới retry. Khi tỷ lệ tranh chấp thấp hoặc vừa phải, Optimistic Locking cho hiệu năng và thông lượng vượt trội hơn hẳn.

* **Câu 5 (Hỏi Siêu): Tại sao nhóm áp dụng nguyên tắc cấm hoàn toàn từ khóa `static` cho các class, field và method ngoài hàm `main`?**  
  👉 *Trả lời:* Việc dùng `static field` để lưu state hoặc cache dữ liệu trong môi trường đa luồng sẽ rất dễ gây ra hiện tượng Race Condition ngầm giữa các phiên làm việc và vi phạm tính đóng gói (Encapsulation) của lập trình hướng đối tượng. Nhóm thiết kế hệ thống 100% instance-based, mỗi controller nắm giữ thể hiện riêng của repository và truyền phụ thuộc qua Constructor (Dependency Injection thủ công), đảm bảo chuẩn mô hình MVC thuần khiết và phục vụ kiểm thử đơn vị độc lập.
