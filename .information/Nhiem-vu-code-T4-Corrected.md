# PHÂN CÔNG NHIỆM VỤ TUẦN 4 (T4) — REPOSITORY LAYER

**Mục tiêu Tuần 4:**
- Hoàn thiện toàn bộ các Repository (kế thừa `CsvRepository<T>`).
- Thực hiện được các thao tác CRUD (Thêm, Đọc, Sửa, Xóa).
- Sử dụng hàm generic `findByCondition(Predicate<T>)` để phục vụ tìm kiếm.
- **Yêu cầu nghiệm thu (DoD):** Tuân thủ 100% cấu trúc MVC và sơ đồ Class Diagram. Tốc độ `loadFromFile` $\le 500ms$.

---

## 1. Bảng Tổng Hợp Phân Công Nhiệm Vụ Tuần 4 (Đã Fix Lỗi MVC)

| Thành viên | Trách nhiệm chính | Các Repository phụ trách | Các phương thức hợp lệ (Đúng Diagram) | File CSV Mục Tiêu |
| :--- | :--- | :--- | :--- | :--- |
| **KIỆT**<br>(Nhóm Account) | Quản lý Tài khoản người dùng | • `FanRepository`<br>• `OrganizerRepository`<br>• `AdminRepository` | • `findByUsername(String username)`<br>• `findPendingOrganizers()` | `fans.csv`<br>`organizers.csv`<br>`admins.csv` |
| **NAM**<br>(Nhóm Sân & Ghế) | Quản lý Sân, Khán đài, Ghế ngồi | • `StadiumRepository`<br>• `SectionRepository`<br>• `SeatRepository` | • `findByStadiumId(String stadiumId)`<br>• `findBySectionId(String sectionId)`<br>• `findAvailableBySection(String sectionId)`<br>• `findByMatchAndSection(String, String)`<br>• `findBySeatAndMatch(String, String)` | `stadiums.csv`<br>`sections.csv`<br>`seats.csv` (10k+ dòng) |
| **HUY**<br>(Nhóm Vé & GD) | Quản lý Vé, Định giá và Thanh toán | • `TicketPricingRepository`<br>• `TransactionRepository`<br>• `TicketRepository` | • `findByFanId(String fanId)`<br>• `findByStatus(TransactionStatus status)` | `ticket_pricings.csv`<br>`transactions.csv`<br>`tickets.csv` |
| **SIÊU**<br>(Trận đấu) | Quản lý Trận đấu & Review PR chéo | • `MatchRepository` | • `findByStadiumId(String stadiumId)`<br>• `findByStatus(MatchStatus status)`<br>• `findUpcomingMatches()` | `matches.csv` |
| **VĂN**<br>(Simulation) | Lưu trữ KQ mô phỏng & Đo hiệu năng | • `SimulationResultRepository` | • `findByMechanism(SynchronizationMechanism)`<br>• `getLatestResult()` | `simulation_results.csv` |

---

## 2. Các Lưu Ý Kỹ Thuật Bắt Buộc (MVC Compliance)
1. **Tuyệt đối cấm logic tính toán ở Repo:** Các hàm như `calculateTotalRevenue` hay lọc `findAccounts(AccountSearchCriteria)` KHÔNG ĐƯỢC PHÉP nằm ở Repository. (Đây là lỗi nghiêm trọng của bảng phân công v3 cũ). Logic này thuộc về Controller.
2. **Kế thừa CsvRepository:** Chỉ việc truyền `filePath` vào `super()` và triển khai `createEntity()` + `getHeader()`. Tuyệt đối không tự mở `BufferedReader` thủ công.
3. **Tên Package:** Đảm bảo tất cả file nằm ở `org.stadium.repository`.
4. **Quy tắc lệch tên:** Class Model là `SeatSection` thì tên Repo là `SectionRepository`. Model là `BookingTransaction` thì Repo là `TransactionRepository`.
