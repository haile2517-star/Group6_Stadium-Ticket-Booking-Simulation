# 🎫 Stadium Ticket Booking Simulation — Nhóm 6 (LAB211)

> **Đọc file này trước khi code.** Đây là nguồn chân lý về mọi quy ước trong dự án.

---

## 👥 Thành Viên Nhóm 6

| Tên | MSSV | Vai trò | Branch phụ trách |
|---|---|---|---|
| Huỳnh Hoàng Siêu | QE210005 | Leader | `main`, `feat/t3-foundation` |
| Lê Chấn Huy | QE210078 | Dev | `feat/t3-tickets` |
| Nguyễn Tuấn Kiệt | QE190093 | Dev | `feat/t3-accounts` |
| Đinh Trọng Nam | QE200103 | Dev | `feat/t3-stadium-seats` |
| Phạm Vũ Gia Văn | QE210046 | Dev | `feat/t3-simulation` |

---

## 📌 Bối Cảnh & Mục Tiêu

Hệ thống mô phỏng bán vé trực tuyến cho sân vận động. Trọng tâm nghiên cứu là bài toán **Double Booking**:

> **Câu hỏi cốt lõi**: Cơ chế đồng bộ hóa nào đảm bảo không xảy ra Double Booking khi hàng nghìn Fan Threads cùng đặt vé đồng thời — và sự đánh đổi (trade-off) về throughput là gì?

**4 cơ chế benchmarked:**
| Cơ chế | Mô tả |
|---|---|
| `NO_LOCK` | Baseline — chứng minh Double Booking xảy ra |
| `FILE_LOCK` | Java NIO `FileLock` khóa cấp file |
| `SYNCHRONIZED` | `synchronized` block trong Repository |
| `OPTIMISTIC` | Kiểm tra `version` trước khi ghi (CAS) |

---

## 🏗 Kiến Trúc MVC (Bắt Buộc Theo)

```
View → Controller → Repository → CSV File
                ↕
             Model (Entity)
```

**Quy tắc bất di bất dịch:**
- ❌ Controller KHÔNG truy cập file CSV trực tiếp.
- ❌ Controller KHÔNG giữ `List<T>` dữ liệu riêng.
- ❌ View KHÔNG gọi Repository.
- ❌ Model (Entity) KHÔNG có logic đọc/ghi file.
- ✅ Mọi đọc/ghi CSV phải đi qua `CsvRepository<T>`.

---

## 📦 Package & Cấu Trúc Thư Mục

```
com.group6.stadium
├── model/
│   ├── enums/         ← Tất cả Enum
│   ├── BaseEntity.java
│   ├── Account.java (abstract)
│   ├── Fan.java, Organizer.java, Admin.java
│   ├── Stadium.java, SeatSection.java, Seat.java
│   ├── Match.java, TicketPricing.java
│   ├── Ticket.java, BookingTransaction.java
│   └── SimulationResult.java
├── repository/
│   ├── CsvRepository.java (abstract generic)
│   ├── FanRepository.java, OrganizerRepository.java, AdminRepository.java
│   ├── StadiumRepository.java, SectionRepository.java, SeatRepository.java
│   ├── MatchRepository.java, TicketPricingRepository.java
│   ├── TicketRepository.java, TransactionRepository.java
│   └── SimulationResultRepository.java
├── controller/
│   ├── AuthController.java
│   ├── MatchController.java, StadiumController.java
│   ├── BookingController.java, TicketController.java
│   ├── AdminController.java, SimulatorController.java
├── view/
│   ├── MainView.java, AuthView.java
│   ├── MatchView.java, SeatMapView.java
│   ├── BookingView.java, OrganizerView.java
│   ├── AdminView.java, SimulatorView.java
└── Main.java
```

---

## 🗂 Quy Ước ID (Bắt Buộc Đúng Định Dạng)

| Entity | Format ID | Ví dụ |
|---|---|---|
| Fan | `FAN` + 4 chữ số | `FAN0001` |
| Organizer | `ORG` + 2 chữ số | `ORG01` |
| Admin | `ADM` + 2 chữ số | `ADM01` |
| Stadium | `STD` + 2 chữ số | `STD01` |
| SeatSection | `SEC_` + stadiumId + `_` + tên khu | `SEC_STD01_A` |
| Seat | sectionId + `_R` + row + `_S` + seat | `SEC_STD01_A_R01_S01` |
| Match | `MTH` + 2 chữ số | `MTH01` |
| TicketPricing | `PRC_` + matchId + `_` + sectionId | `PRC_MTH01_SEC_STD01_A` |
| BookingTransaction | `TXN` + 5 chữ số | `TXN00001` |
| Ticket | `TCK` + 6 chữ số | `TCK000001` |
| SimulationResult | `SIM` + 3 chữ số | `SIM001` |

---

## 📄 Chuẩn CSV (Tên Cột Chính Xác — Theo Thứ Tự)

> ⚠️ Thứ tự cột bên dưới = thứ tự parse trong `fromCsvLine()`. Không được đổi thứ tự.

| File | Tên Cột (Theo Thứ Tự) | Entity |
|---|---|---|
| `admins.csv` | `adminId,username,passwordHash,fullName,email,phone,status` | Admin |
| `fans.csv` | `fanId,username,passwordHash,fullName,email,phone,fanType,status` | Fan |
| `organizers.csv` | `organizerId,username,passwordHash,orgName,email,phone,status` | Organizer |
| `stadiums.csv` | `stadiumId,stadiumName,address,capacity` | Stadium |
| `sections.csv` | `sectionId,stadiumId,sectionName,sectionType,totalRows,seatsPerRow` | SeatSection |
| `seats.csv` | `seatId,sectionId,rowNumber,seatNumber,seatType,status,version` | Seat |
| `matches.csv` | `matchId,organizerId,stadiumId,matchTitle,teamA,teamB,startTime,endTime,status` | Match |
| `ticket_pricings.csv` | `pricingId,matchId,sectionId,price` | TicketPricing |
| `transactions.csv` | `transactionId,fanId,matchId,totalAmount,ticketCount,transactionDate,status` | BookingTransaction |
| `tickets.csv` | `ticketId,matchId,seatId,transactionId,fanId,price,bookingDate,status` | Ticket |
| `simulation_results.csv` | `resultId,mechanism,threadCount,totalRequests,successCount,failedCount,doubleBookingCount,throughput,doubleBookingRate,executionTimeMs` | SimulationResult |

**Định dạng datetime:** `yyyy-MM-dd HH:mm:ss` (ví dụ: `2026-10-10 19:30:00`)

---

## 🔢 Enum Values Chuẩn

```java
AccountStatus  : PENDING, ACTIVE, REJECTED, BLOCKED
UserRole       : GUEST, FAN, ORGANIZER, ADMIN
SeatType       : VIP, STANDARD, ECONOMY, STANDING
SeatStatus     : AVAILABLE, LOCKED, BOOKED
MatchStatus    : SCHEDULED, LIVE, FINISHED, CANCELLED
TicketStatus   : AVAILABLE, BOOKED, CANCELLED
TransactionStatus : PENDING, SUCCESS, FAILED, CANCELLED
SynchronizationMechanism : NO_LOCK, FILE_LOCK, SYNCHRONIZED, OPTIMISTIC
```

---

## 📐 Kế Thừa Model (Class Hierarchy)

```
BaseEntity (abstract)
├── Account (abstract)
│   ├── Fan
│   ├── Organizer
│   └── Admin
├── Stadium
├── SeatSection
├── Seat
├── Match
├── TicketPricing
├── BookingTransaction
├── Ticket
└── SimulationResult
```

**Quan hệ Composition (có vòng đời phụ thuộc):**
- `Stadium "1" *-- "*" SeatSection`
- `SeatSection "1" *-- "*" Seat`
- `Match "1" *-- "*" TicketPricing`
- `BookingTransaction "1" *-- "1..4" Ticket`

---

## ⚙️ Quy Tắc Code Kỹ Thuật Quan Trọng

### 1. CSV Parsing
```java
// ✅ ĐÚNG — Giữ trường rỗng ở cuối dòng
String[] parts = line.split(",", -1);

// ❌ SAI — Bỏ sót trường rỗng cuối dòng
String[] parts = line.split(",");
```

### 2. Enum Parse từ CSV
```java
// ✅ ĐÚNG
SeatStatus status = SeatStatus.valueOf(parts[5]);

// ❌ SAI — So sánh String thủ công
if (parts[5].equals("AVAILABLE")) { ... }
```

### 3. CsvRepository Generic
```java
// ✅ ĐÚNG — Kế thừa CsvRepository
public class FanRepository extends CsvRepository<Fan> { ... }

// ❌ SAI — Tự mở BufferedReader/Writer riêng trong Repository con
```

---

## 📅 Roadmap Theo Tuần

| Tuần | Milestone | Nội Dung | Tiêu Chí Nghiệm Thu |
|---|---|---|---|
| **T3 (Nay)** | Model Layer | BaseEntity, CsvRepository<T>, tất cả Entity + Enum | 100% fromCsvLine/toCsvLine khớp file data/ |
| **T4-5** | Repository Layer | CRUD + tìm kiếm đặc thù cho từng Entity | Đọc/ghi 11 file CSV không lỗi |
| **T6-7** | Controller + View | Login/Logout, đặt vé CLI đơn luồng hoạt động | Chạy được menu 3 role trên console |
| **T8-9** | Concurrency Simulation | 4 cơ chế khóa + đo Double Booking | NO_LOCK phải sinh double booking > 0 |
| **T10** | Final | Refactor, báo cáo, slide, demo | Nộp đồ án hoàn chỉnh |

---

## 🤖 AI (Antigravity) — Cách Dùng Hiệu Quả

Dự án có bộ **Skills AI** trong `.agents/skills/` để tự động hóa việc kiểm tra:

| Skill | Mô Tả | Câu Lệnh Kích Hoạt |
|---|---|---|
| `lab211-class-diagram-checker` | Kiểm tra code khớp Class Diagram | *"check Fan.java đúng diagram chưa"* |
| `lab211-code-sync-checker` | Kiểm tra đồng bộ xuyên suốt các tầng | *"các tầng có xung đột gì không"* |
| `lab211-csv-model-tester` | Nghiệm thu fromCsvLine/toCsvLine | *"test parse seats.csv với Seat.java"* |
| `lab211-concurrency-reviewer` | Review 4 cơ chế đồng bộ hóa | *"review code NO_LOCK có đúng không"* |
| `lab211-pr-merge-review` | Checklist trước khi merge PR | *"review PR của Nam có merge được chưa"* |

**Lưu ý khi dùng AI:** Mở file liên quan trong IDE trước khi hỏi để AI có đủ context.

---

## 📁 Tài Liệu Tham Khảo (Trong `.information/`)

| File | Nội Dung |
|---|---|
| `LAB211_TicketBooking_De_Tai.docx.pdf` | Đề tài gốc LAB211 |
| `mermaid-class-diagram.docx` | Class Diagram đầy đủ dạng Mermaid text |
| `Nhiem-vu-code-T3.docx` | Phân công nhiệm vụ Tuần 3 chi tiết |
| `Class diagram(MVC).drawio.png` | Class Diagram dạng ảnh |
| `Usecase diagram.drawio.png` | Use Case Diagram dạng ảnh |
