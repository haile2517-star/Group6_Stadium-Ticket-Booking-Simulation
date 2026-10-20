# BẢNG PHÂN CÔNG NHIỆM VỤ TUẦN 8 (SIMULATOR TOOL & STRESS TEST)
## HỆ THỐNG MÔ PHỎNG ĐẶT VÉ SÂN VẬN ĐỘNG (STADIUM TICKET BOOKING SIMULATION)

> **Nhóm thực hiện:** Nhóm 6 (OOP with Java - Semester 3, FPT University)  
> **Leader & Reviewer:** Siêu  
> **Các thành viên:** Kiệt, Nam, Huy, Văn, Siêu  
> **Giai đoạn:** Tuần 8 (T8) — Xây dựng công cụ mô phỏng tải cao (100 - 500 Threads) & Báo cáo so sánh  
> **Căn cứ thiết kế:** Đề tài gốc (`LAB211_TicketBooking_De_Tai.docx.pdf`), Sơ đồ lớp chuẩn (`mermaid-class-diagram.docx`), Thư viện dữ liệu CSV (`data/*.csv`)

---

### I. MỤC TIÊU VÀ DELIVERABLE TUẦN 8
* **Mục tiêu kỹ thuật:**
  * Hiện thực `SimulatorController` kết hợp `CountDownLatch` và `ExecutorService` (Thread pool đa luồng) để mô phỏng tải cao Flash Sale với 100 – 500 threads tranh chấp mua vé đồng thời.
  * Hiện thực `SimulatorView` in bảng so sánh hiệu năng ASCII trực quan (Throughput, Double Booking Rate, Thời gian thực thi).
  * Đo lường và ghi nhận kết quả vào `simulation_results.csv` qua `SimulationResultRepository`.
* **Deliverable Tuần 8:**
  * Demo hoàn chỉnh công cụ Simulator chạy 500 threads mô phỏng tải cao.
  * Bảng kết quả so sánh đối chiếu 4 cơ chế (`NO_LOCK`, `FILE_LOCK`, `SYNCHRONIZED`, `OPTIMISTIC`) hiển thị chuẩn format trên màn hình console.

---

### II. BẢNG PHÂN CÔNG CHI TIẾT TUẦN 8 (5 THÀNH VIÊN)

| Thành viên | Domain chuyên trách | Tầng (Layer) | Class & Phương thức phân công (Chuẩn Class Diagram) | Chi tiết nhiệm vụ & Deliverable |
| :--- | :--- | :--- | :--- | :--- |
| **Văn** *(Chủ trì Simulator)* | Simulation Engine | Controller | **`SimulatorController`**<br>- `threadCount: int`<br>- `totalRequests: int`<br>- `lockingMechanism: SynchronizationMechanism`<br>- `runConcurrencyTest(mech: SynchronizationMechanism, threads: int, reqs: int): SimulationResult`<br>- `executeConcurrentTasks(latch: Object, pool: Object): void`<br>- `calculateThroughput(successCount: int, durationMs: long): double`<br>- `calculateDoubleBookingRate(doubleBookings: int, totalSuccess: int): double`<br><br>**`SimulationResultRepository`**<br>- `findByMechanism(mech: SynchronizationMechanism): List<SimulationResult>`<br>- `getLatestResult(): SimulationResult` | - Xây dựng Engine bắn tải mô phỏng bán vé Flash Sale sử dụng `ExecutorService` (FixedThreadPool) và `CountDownLatch(1)` để đồng loạt kích nổ các luồng cùng 1 thời điểm.<br>- Thu thập kết quả: số request thành công, thất bại, số vé bị đặt trùng (double booking).<br>- Công thức tính toán chuẩn:<br>  * $\text{Throughput} = \frac{\text{successCount}}{\text{durationMs} / 1000.0}$ (vé/giây)<br>  * $\text{DoubleBookingRate} = \frac{\text{doubleBookingCount}}{\text{totalSuccess}} \times 100\%$<br>- Khởi tạo và lưu kết quả vào `simulation_results.csv` qua `SimulationResultRepository`.<br>- **Deliverable:** Engine chạy tải 100 – 500 threads ổn định, không crash JVM. |
| **Siêu** *(Leader)* | Báo cáo ASCII & Admin UI | View / Controller | **`SimulatorView`**<br>- `displaySimulationTable(results: List<SimulationResult>): void`<br><br>**`AdminView`** & **`AdminController`**<br>- `AdminView.displayBenchmarkMenu(): void`<br>- `AdminController.runConcurrencyBenchmark(...)`<br>- `AdminController.viewBenchmarkReports()` | - Thiết kế giao diện `SimulatorView` in bảng so sánh ASCII trực quan định dạng chuyên nghiệp với các cột: Cơ chế, Số thread, Tổng req, Thành công, Thất bại, Trùng vé, Throughput (req/s), Tỉ lệ trùng (%), Thời gian (ms).<br>- Tích hợp menu chạy mô phỏng trực tiếp từ giao diện Admin.<br>- **Deliverable:** Bảng so sánh 4 cơ chế hiển thị đẹp mắt, rõ ràng. |
| **Huy** | Dữ liệu Thử nghiệm | Utility / Repo | **`BenchmarkDataSeeder`**<br>- Tiện ích reset và nạp dữ liệu mẫu ghế/vé | - Viết module tiện ích khôi phục (reset) trạng thái ghế trong `seats.csv` và `tickets.csv` về trạng thái ban đầu `AVAILABLE` trước mỗi lần chạy test một cơ chế mới (đảm bảo tính công bằng và chính xác của phép đo benchmark).<br>- Thu thập log chi tiết các trường hợp ghế bị double booking dưới cơ chế `NO_LOCK`.<br>- **Deliverable:** Dữ liệu thử nghiệm luôn sạch và có thể tái lặp 100%. |
| **Nam** | Tối ưu I/O & FileLock | Repository | **`SeatRepository`** (Tối ưu tải cao) | - Tinh chỉnh hiệu năng đọc/ghi file CSV của `SeatRepository` khi chịu tải 100 - 500 threads dưới cơ chế `FILE_LOCK`.<br>- Đánh giá và ghi nhận hiện tượng thắt cổ chai I/O đĩa (disk bottleneck) khi có quá nhiều luồng tranh chấp FileLock.<br>- **Deliverable:** Phân tích độ trễ của cơ chế FILE_LOCK phục vụ viết báo cáo tuần 9. |
| **Kiệt** | Đối soát & Xác thực | Testing / Audit | **`SimulationAuditValidator`**<br>- Đối soát số liệu thực tế | - Tiến hành chạy thử nghiệm độc lập ở 3 mốc tải: 100 threads, 200 threads, 500 threads.<br>- Đối chiếu số liệu trong bảng kết quả `SimulationResult` với dữ liệu thực tế được ghi xuống file `tickets.csv` và `transactions.csv` (kiểm tra không phát sinh vé ảo).<br>- **Deliverable:** Báo cáo xác thực tính chính xác của số liệu mô phỏng. |

---

### III. NGUYÊN TẮC BẮT BUỘC KHI VIẾT CODE (BÀN TAY SẮT)
1. **Chuẩn Package & Tên Lớp:** Package duy nhất là `org.stadium.*`. Tuyệt đối cấm dùng `com.group6.stadium`.
2. **Quy tắc 100% Tiền tố `this.`:** Bắt buộc 100% các truy cập thuộc tính instance (fields) và phương thức nội bộ trong class phải có tiền tố `this.` (ví dụ: `this.threadCount`, `this.totalRequests`).
3. **Tuyệt đối KHÔNG DÙNG `static` (Ngoại trừ hàm `main`):** Cấm khai báo `static class`, `static field` (lưu state/cache) hoặc `static method` helper.
4. **Quản lý Vòng Đời Luồng (Thread Pool Lifecycle):** Khi sử dụng `ExecutorService`, bắt buộc phải gọi `shutdown()` và `awaitTermination()` trong khối `finally` để tránh rò rỉ tài nguyên luồng (Thread Leak).
5. **Class Diagram là Chân Lý:** Chỉ được code các phương thức có trong sơ đồ `mermaid-class-diagram.docx`.

---

### IV. BÁO CÁO KIỂM TOÁN CHÉO (TỪ SKILL LAB211-COMPREHENSIVE-REVIEWER)
1. 📊 **Class Diagram & Syntax Audit:** Toàn bộ thuộc tính và hàm của `SimulatorController`, `SimulatorView`, `SimulationResultRepository` khớp 100% với Class Diagram.
2. 🏛️ **MVC & Rules Compliance:** Kiến trúc đa luồng đóng gói gọn gàng trong Controller và Repository, View thuần túy in bảng kết quả ASCII.
3. 🗃️ **Data/CSV Sync:** Khớp 10 cột dữ liệu với `data/simulation_results.csv`.
4. 🔗 **Cross-layer Sync:** Đấu nối trực tiếp vào `AdminController.runConcurrencyBenchmark` và `AdminView.displayBenchmarkMenu`.
### 👉 Phán quyết: 🟢 **[APPROVE - ĐẠT CHUẨN XUẤT SẮC 100%]**
