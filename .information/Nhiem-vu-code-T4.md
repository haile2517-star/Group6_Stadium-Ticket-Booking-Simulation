# PHÂN CÔNG NHIỆM VỤ TUẦN 4 (T4) — REPOSITORY LAYER

**Mục tiêu Tuần 4 (Theo tài liệu LAB211):**
- Hoàn thiện toàn bộ các Repository (kế thừa `CsvRepository<T>`).
- Thực hiện được các thao tác CRUD (Thêm, Đọc, Sửa, Xóa).
- Implement tính năng **tìm kiếm theo điều kiện** sử dụng `findByCondition(Predicate<T>)`.
- **Yêu cầu nghiệm thu (DoD):** CRUD test thành công và tốc độ đọc file CSV $\ge 10.000$ dòng phải $\le 500ms$.

---

## 1. Phân công chi tiết theo Domain

Dựa vào việc mỗi thành viên đã nằm lòng cấu trúc Entity ở Tuần 3, công việc Tuần 4 sẽ được phân chia bám sát theo domain tương ứng để phát huy tối đa hiệu quả:

### 👤 Nguyễn Tuấn Kiệt — Nhóm Account
- **Nhiệm vụ:** Implement `FanRepository`, `OrganizerRepository`, `AdminRepository`.
- **Yêu cầu kỹ thuật:**
  - Khởi tạo repo truyền vào đúng file path (Ví dụ: `data/fans.csv`).
  - Định nghĩa hàm `getHeader()` trả về đúng header (Ví dụ: `fanId,username,passwordHash,fullName,email,phone,fanType,status`).
  - Tích hợp class `AccountSearchCriteria` vào hàm tìm kiếm. Ví dụ: Viết một phương thức `findAccounts(AccountSearchCriteria criteria)` sử dụng `findByCondition`.
- **Lưu ý rút kinh nghiệm:** Ở T3 code Entity rất tốt, hãy giữ vững phong độ. Khi truy vấn, hãy chú ý việc so sánh chuỗi phân biệt hoa thường (nên dùng `.equalsIgnoreCase()`).

---

### 👤 Đinh Trọng Nam — Nhóm Sân vận động & Ghế
- **Nhiệm vụ:** Implement `StadiumRepository`, `SectionRepository`, `SeatRepository`.
- **Yêu cầu kỹ thuật:**
  - `SeatRepository` là repo quan trọng và nặng nhất dự án (~10.000 dòng `seats.csv`). Phải đặc biệt tối ưu tốc độ.
  - Viết các hàm tiện ích như `findByStadiumId()`, `findBySectionId()`, `findAvailableSeats()`.
- **Lưu ý:** Đừng vội làm cơ chế Lock (NIO FileLock, Optimistic) ở Tuần 4. Tuần này chỉ tập trung vào CRUD cơ bản và tốc độ nạp dữ liệu vào RAM (`loadFromFile()`).

---

### 👤 Lê Chấn Huy — Nhóm Giao dịch & Vé
- **Nhiệm vụ:** Implement `TicketPricingRepository`, `TransactionRepository`, `TicketRepository`.
- **Yêu cầu kỹ thuật:**
  - Viết các hàm truy vấn quan trọng như `findTicketsByFanId()`, `findTransactionsByMatchId()`.
  - Chú ý xử lý tính toán doanh thu nếu cần (truy vấn tổng `totalAmount` trong `TransactionRepository`).
- **Lưu ý rút kinh nghiệm:** Ở T3 bạn có sai sót khi ánh xạ sai thứ tự cột (`transactionDate` và `totalAmount`). Lên T4, hãy đảm bảo hàm `getHeader()` trong Repository trả về chuỗi **khớp tuyệt đối 100%** với header trong file thực tế `data/transactions.csv`.

---

### 👤 Huỳnh Hoàng Siêu (Leader) — Nhóm Trận đấu & Tổng hợp
- **Nhiệm vụ:** Implement `MatchRepository` + Quản lý tiến độ.
- **Yêu cầu kỹ thuật:**
  - Implement `MatchRepository`, tích hợp class `MatchFilter` để tìm kiếm trận đấu theo điều kiện `Predicate` linh hoạt (theo tên, ngày, sân, trạng thái).
  - Code Review: Là Leader, Siêu cần review các Pull Request (PR) của Kiệt, Nam, Huy và Văn trước khi merge. Chú ý check kỹ hàm `getHeader()` của mọi người.

---

### 👤 Phạm Vũ Gia Văn — Nhóm Simulation & Performance Test
- **Nhiệm vụ:** Implement `SimulationResultRepository` + Kịch bản đo lường hiệu năng.
- **Yêu cầu kỹ thuật:**
  - Implement `SimulationResultRepository` để lưu trữ kết quả chạy mô phỏng.
  - **Nhiệm vụ đặc biệt:** Theo yêu cầu T4, hệ thống phải đọc $\ge 10.000$ dòng $< 500ms$. Văn chịu trách nhiệm viết một class Unit Test / Benchmark đơn giản (`PerformanceTest.java`) để gọi hàm `loadFromFile()` của `SeatRepository` (file bự nhất) và đo lường thời gian thực thi `System.currentTimeMillis()`. Báo cáo kết quả lại cho nhóm.
- **Lưu ý rút kinh nghiệm:** Code T3 của Văn rất chặt chẽ. Hãy áp dụng sự tỉ mỉ đó vào việc đo lường hiệu năng để pass bài kiểm tra của Giảng viên.

---

## 2. Bảng Tổng Hợp Phân Công Nhiệm Vụ Tuần 4

| Thành viên | Trách nhiệm chính | Các Class / Repository phụ trách | Các phương thức đặc thù cần viết | File CSV & Dataset kiểm thử |
| :--- | :--- | :--- | :--- | :--- |
| **KIỆT**<br>(Nhóm Account) | Triển khai Repository cho tài khoản người dùng | • `FanRepository`<br>• `OrganizerRepository`<br>• `AdminRepository` | • `findAccounts(AccountSearchCriteria criteria)`<br>• `findByUsername(String username)`<br>• `findByEmail(String email)` | • `data/fans.csv` (250 dòng)<br>• `data/organizers.csv` (5 dòng)<br>• `data/admins.csv` (2 dòng) |
| **NAM**<br>(Nhóm Sân & Ghế) | Triển khai Repository quản lý sân, khán đài, ghế | • `StadiumRepository`<br>• `SectionRepository`<br>• `SeatRepository` | • `findByStadiumId(String stadiumId)`<br>• `findBySectionId(String sectionId)`<br>• `findAvailableSeats(String sectionId)` | • `data/stadiums.csv` (3 dòng)<br>• `data/sections.csv` (12 dòng)<br>• `data/seats.csv` (~10.000 dòng) |
| **HUY**<br>(Nhóm Vé & Giao dịch) | Triển khai Repository vé, định giá và thanh toán | • `TicketPricingRepository`<br>• `TransactionRepository`<br>• `TicketRepository` | • `findTicketsByFanId(String fanId)`<br>• `findTransactionsByMatchId(String matchId)`<br>• `calculateTotalRevenue(String matchId)` | • `data/ticket_pricings.csv` (14 dòng)<br>• `data/transactions.csv` (30 dòng)<br>• `data/tickets.csv` (30 dòng) |
| **SIÊU**<br>(Core & Trận đấu) | Quản lý tiến độ, Match Repo & Code Review | • `MatchRepository`<br>• Quản trị dự án & Review PR | • `findByFilter(MatchFilter filter)`<br>• `findByStatus(MatchStatus status)`<br>• `findByDateRange(LocalDate from, LocalDate to)` | • `data/matches.csv` (5 dòng)<br>• Review PR cho toàn nhóm |
| **VĂN**<br>(Simulation & Test) | Repo lưu kết quả mô phỏng & Đo hiệu năng | • `SimulationResultRepository`<br>• `PerformanceTest` (Benchmark) | • `findByMatchId(String matchId)`<br>• Benchmark `loadFromFile()` trên `SeatRepository` ($\ge 10.000$ dòng $\le 500ms$) | • `data/simulation_results.csv` (4 dòng)<br>• Bộ test đo tốc độ đọc file CSV |

---

## 3. Các Lưu Ý Kỹ Thuật Quan Trọng (Best Practices)

1. **Tận dụng `CsvRepository<T>`:**
   Tuyệt đối không viết lại logic đọc (`BufferedReader`) và ghi (`BufferedWriter`) file CSV trong các class con. Cứ truyền `filePath` qua `super(filePath)` và triển khai 2 hàm abstract `createEntity(String[] fields)` và `getHeader()`. Hệ thống sẽ tự động hoạt động!
2. **Sức mạnh của `Predicate<T>`:**
   Trong hàm `findByCondition(Predicate<T> predicate)`, các bạn có thể truyền lambda expression rất ngắn gọn từ Controller hoặc Unit Test.
   *Ví dụ tìm Fan có tên "Nguyễn":*
   ```java
   List<Fan> fans = fanRepo.findByCondition(fan -> fan.getFullName() != null && fan.getFullName().contains("Nguyễn"));
   ```
3. **Thận trọng với Null:**
   Khi tìm kiếm hoặc lọc dữ liệu, luôn lường trước các trường có thể bị `null` (nhất là tìm kiếm theo chuỗi). Sử dụng `if (str != null && str.equalsIgnoreCase(...))` để tránh `NullPointerException`.
4. **Không thay đổi File Data Gốc:**
   Khi test CRUD, các thao tác `save()` hoặc `delete()` sẽ thay đổi file trong thư mục `data/`. Hãy copy thư mục `data/` ra một thư mục `data_test/` để chạy thử nghiệm, tránh làm hỏng dữ liệu gốc.
