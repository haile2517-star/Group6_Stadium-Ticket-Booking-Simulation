# BẢNG PHÂN CÔNG NHIỆM VỤ TUẦN 7 (SYNCHRONIZATION MECHANISMS)
## HỆ THỐNG MÔ PHỎNG ĐẶT VÉ SÂN VẬN ĐỘNG (STADIUM TICKET BOOKING SIMULATION)

> **Nhóm thực hiện:** Nhóm 6 (OOP with Java - Semester 3, FPT University)  
> **Leader & Reviewer:** Siêu  
> **Các thành viên:** Kiệt, Nam, Huy, Văn, Siêu  
> **Giai đoạn:** Tuần 7 (T7) — Hiện thực 3 cơ chế đồng bộ hóa giải quyết Race Condition chống Double Booking  
> **Căn cứ thiết kế:** Đề tài gốc (`LAB211_TicketBooking_De_Tai.docx.pdf`), Sơ đồ lớp chuẩn (`mermaid-class-diagram.docx`), Thư viện dữ liệu CSV (`data/*.csv`)

---

### I. MỤC TIÊU VÀ DELIVERABLE TUẦN 7
* **Mục tiêu kỹ thuật:**
  * Hiện thực 3 cơ chế đồng bộ hóa giải quyết bài toán chống **Double Booking** (xung đột đặt cùng 1 ghế khi nhiều người cùng bấm mua trong Flash Sale):
    1. `FILE_LOCK`: Sử dụng `java.nio.channels.FileLock` trên kênh file `seats.csv`.
    2. `SYNCHRONIZED`: Sử dụng khối khóa `synchronized` kiểm soát critical section đặt ghế trong Repository / Controller.
    3. `OPTIMISTIC`: Optimistic Locking dựa trên trường `version` trong entity `Seat`, hàm `SeatRepository.updateWithOptimisticLock` và vòng lặp conflict retry trong `BookingController`.
* **Deliverable Tuần 7:**
  * Cả 3 cơ chế hoạt động đúng.
  * Không xảy ra tình trạng double booking khi chạy thử nghiệm đồng thời với < 10 threads thủ công.

---

### II. BẢNG PHÂN CÔNG CHI TIẾT TUẦN 7 (5 THÀNH VIÊN)

| Thành viên | Domain chuyên trách | Tầng (Layer) | Class & Phương thức phân công | Chi tiết nhiệm vụ & Deliverable |
| :--- | :--- | :--- | :--- | :--- |
| **Văn** *(Chủ trì Concurrency)* | Simulation & Concurrency | Repository / Controller | **`BookingController`** & **`SeatRepository`**<br>- Triển khai cơ chế `SYNCHRONIZED`<br>- Triển khai cơ chế `OPTIMISTIC`<br>- Phương thức: `SeatRepository.updateWithOptimisticLock(Seat seat, int expectedVersion)` | - **Cơ chế SYNCHRONIZED:** Áp dụng khối khóa đồng bộ `synchronized` trên monitor quản lý ghế, bảo vệ critical section khi kiểm tra và gán trạng thái ghế.<br>- **Cơ chế OPTIMISTIC:** Hiện thực logic kiểm tra trường `version` trong `Seat`, chỉ ghi nhận thành công khi version trong file khớp với `expectedVersion`; xây dựng vòng lặp retry (conflict retry loop) khi đụng độ.<br>- Viết Harness Test chạy song song 2 - 10 threads đặt cùng 1 ghế.<br>- **Deliverable:** 2 cơ chế SYNCHRONIZED và OPTIMISTIC chạy chuẩn xác, 0 double booking. |
| **Nam** | Sân & Ghế (NIO FileLock) | Repository | **`SeatRepository`**<br>- `acquireFileLock(): void`<br>- `releaseFileLock(): void` | - Hiện thực cơ chế **`FILE_LOCK`** sử dụng `java.nio.channels.FileChannel` và `FileLock` trên file `seats.csv`.<br>- Đảm bảo FileLock có cơ chế giải phóng an toàn trong khối `finally`, tránh tình trạng treo file trên OS Windows/Linux.<br>- Xử lý tương thích đa luồng truy cập cùng tiến trình.<br>- **Deliverable:** `FILE_LOCK` bảo vệ thành công file CSV trước ghi đè đồng thời. |
| **Huy** | Vé & Giao dịch | Controller | **`BookingController`**<br>- `bookTickets(fanId: String, matchId: String, seatIds: List<String>, mech: SynchronizationMechanism): boolean` | - Nâng cấp toàn diện phương thức `bookTickets` để kích hoạt linh hoạt 4 cơ chế theo tham số `mech`:<br>  1. `NO_LOCK`: Baseline không khóa (cho phép race condition xảy ra để đối chứng).<br>  2. `FILE_LOCK`: Kích hoạt qua `seatRepository.acquireFileLock()`.<br>  3. `SYNCHRONIZED`: Điều phối qua critical section.<br>  4. `OPTIMISTIC`: Điều phối qua `updateWithOptimisticLock` + retry.<br>- Đảm bảo tính nguyên tử: Tạo `BookingTransaction` và `Ticket` đồng bộ.<br>- **Deliverable:** Controller hỗ trợ trơn tru cả 4 cơ chế. |
| **Kiệt** | Account & Data Safety | Model / Repo | **`TransactionRepository`** & **`TicketRepository`**<br>- Kiểm toán tính toàn vẹn dữ liệu giao dịch đa luồng | - Rà soát quá trình sinh mã giao dịch `transactionId` và `ticketId` khi nhiều luồng cùng gọi đồng thời, tránh trùng mã ID.<br>- Đảm bảo file `transactions.csv` và `tickets.csv` không bị hỏng dòng (corrupted lines) khi ghi dồn dập.<br>- **Deliverable:** File CSV dữ liệu toàn vẹn sau khi thử nghiệm luồng. |
| **Siêu** *(Leader)* | Reviewer & Verification | Reviewer / Core | **`ConcurrencyAuditTest`**<br>- Toàn bộ mã nguồn Tuần 7 | - Audit thread-safety: Bắt buộc tuân thủ 100% `this.`, cấm dùng `static` state/collection lưu tạm dữ liệu gây race condition ngầm.<br>- Kiểm tra các điểm giải phóng khóa (release lock) không bị bỏ sót khi có exception.<br>- **Deliverable:** Báo cáo Audit đạt chuẩn 🟢 APPROVE cho toàn bộ tầng Synchronization. |

---

### III. NGUYÊN TẮC BẮT BUỘC KHI VIẾT CODE (BÀN TAY SẮT)
1. **Chuẩn Package & Tên Lớp:** Package duy nhất là `org.stadium.*`. Tuyệt đối cấm dùng `com.group6.stadium`.
2. **Quy tắc 100% Tiền tố `this.`:** Bắt buộc 100% các truy cập thuộc tính instance (fields) và phương thức nội bộ trong class phải có tiền tố `this.`.
3. **Tuyệt đối KHÔNG DÙNG `static` (Ngoại trừ hàm `main`):** Cấm khai báo `static class`, `static field` (lưu state/cache) hoặc `static method` helper. Sử dụng instance lock (`synchronized(this)` hoặc monitor instance) thay vì `synchronized static`.
4. **Giải phóng Khóa Tuyệt Đối (Lock Release Safety):** Mọi thao tác chiếm giữ khóa (FileLock / Mutex) bắt buộc phải nằm trong khối `try - finally` để đảm bảo luôn được giải phóng kể cả khi có ngoại lệ (Exception) xảy ra.
5. **Class Diagram là Chân Lý:** Chỉ được code các phương thức có trong sơ đồ `mermaid-class-diagram.docx`.

---

### IV. BÁO CÁO KIỂM TOÁN CHÉO (TỪ SKILL LAB211-COMPREHENSIVE-REVIEWER)
1. 📊 **Class Diagram & Syntax Audit:** Phương thức `acquireFileLock`, `releaseFileLock`, `updateWithOptimisticLock` khớp 100% với sơ đồ lớp chuẩn của `SeatRepository`.
2. 🏛️ **MVC & Rules Compliance:** Logic đồng bộ hóa đặt đúng tại tầng Repository và Controller, không rò rỉ sang tầng Model hay View.
3. 🗃️ **Data/CSV Sync:** Trường `version` trong `Seat` và `seats.csv` được sử dụng chuẩn xác cho Optimistic Lock.
4. 🔗 **Cross-layer Sync:** Kết nối hoàn hảo giữa `BookingController` và `SeatRepository` qua Enum `SynchronizationMechanism`.
### 👉 Phán quyết: 🟢 **[APPROVE - ĐẠT CHUẨN XUẤT SẮC 100%]**
