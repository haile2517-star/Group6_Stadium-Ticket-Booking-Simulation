# BẢNG PHÂN CÔNG NHIỆM VỤ TUẦN 6 (VIEW LAYER & MVC WIRING)
## HỆ THỐNG MÔ PHỎNG ĐẶT VÉ SÂN VẬN ĐỘNG (STADIUM TICKET BOOKING SIMULATION)

> **Nhóm thực hiện:** Nhóm 6 (OOP with Java - Semester 3, FPT University)  
> **Leader & Reviewer:** Siêu  
> **Các thành viên:** Kiệt, Nam, Huy, Văn, Siêu  
> **Giai đoạn:** Tuần 6 (T6) — View Layer & Kết nối luồng MVC hoàn chỉnh  
> **Căn cứ thiết kế:** Đề tài gốc (`LAB211_TicketBooking_De_Tai.docx.pdf`), Sơ đồ lớp chuẩn (`mermaid-class-diagram.docx`), Thư viện dữ liệu CSV (`data/*.csv`)

---

### I. MỤC TIÊU VÀ DELIVERABLE TUẦN 6
* **Mục tiêu kỹ thuật:**
  * Hiện thực toàn bộ các lớp thuộc tầng hiển thị (Console UI) theo mô hình MVC: `MainView`, `AuthView`, `MatchView`, `SeatMapView`, `BookingView`, `OrganizerView`, `AdminView`.
  * Đấu nối luồng tương tác giữa `View ↔ Controller ↔ Repository ↔ Model`. View chỉ nhận dữ liệu từ Controller và hiển thị ra màn hình hoặc nhận input người dùng, tuyệt đối không gọi trực tiếp xuống Repository hay đọc file CSV.
* **Deliverable Tuần 6:**
  * Demo hoàn chỉnh luồng đặt vé đơn luồng từ UI console: Đăng nhập/Đăng ký tài khoản Fan -> Xem danh sách trận đấu -> Chọn trận -> Chọn khán đài & Xem sơ đồ ghế ASCII -> Nhập ghế muốn đặt -> Thanh toán & In biên lai -> Xem lịch sử giao dịch.

---

### II. BẢNG PHÂN CÔNG CHI TIẾT TUẦN 6 (5 THÀNH VIÊN)

| Thành viên | Domain chuyên trách | Tầng (Layer) | Class & Phương thức phân công (Chuẩn Class Diagram) | Chi tiết nhiệm vụ & Deliverable |
| :--- | :--- | :--- | :--- | :--- |
| **Siêu** *(Leader)* | Trận đấu & Core Routing | View / Core | **`MainView`**<br>- `start(): void`<br>- `displayGuestMenu(): void`<br>- `displayFanMenu(): void`<br>- `displayOrganizerMenu(): void`<br>- `displayAdminMenu(): void`<br><br>**`MatchView`**<br>- `displayMatches(matches: List<Match>): void`<br>- `displayMatchDetails(match: Match): void`<br>- `displayCreateMatchForm(): void`<br>- `displayUpdateMatchForm(): void`<br>- `promptSearchKeyword(): String`<br>- `promptFilterCriteria(): MatchFilter` | - Thiết kế khung menu điều hướng trung tâm phân quyền theo vai trò (GUEST, FAN, ORGANIZER, ADMIN).<br>- Hiện thực giao diện xem danh sách trận đấu, chi tiết trận đấu, tìm kiếm theo từ khóa và lọc trận đấu theo tiêu chí.<br>- Đấu nối `MatchView` với `MatchController`.<br>- **Deliverable:** Menu chính và luồng xem trận đấu hoạt động mượt mà. |
| **Kiệt** | Account & Auth | View | **`AuthView`**<br>- `displayLoginForm(): void`<br>- `displayFanRegisterForm(): void`<br>- `displayOrganizerRegisterForm(): void`<br>- `displayProfile(account: Account): void`<br>- `displayChangePasswordForm(): void` | - Xây dựng các form nhập liệu: đăng nhập, đăng ký Fan, đăng ký Ban tổ chức, xem hồ sơ cá nhân và đổi mật khẩu.<br>- Đấu nối trực tiếp với `AuthController`.<br>- Xử lý bắt lỗi nhập dữ liệu (validation chuỗi rỗng, định dạng email, số điện thoại).<br>- **Deliverable:** Luồng xác thực tài khoản chạy hoàn chỉnh trên console. |
| **Nam** | Sân & Ghế | View | **`SeatMapView`**<br>- `displaySeatMap(asciiMap: String): void`<br>- `displayAvailableSeats(seats: List<Seat>): void`<br><br>**`StadiumView`** *(Hoàn thiện)*<br>- `displayStadiumList(stadiums: List<Stadium>): void`<br>- `displaySectionList(stadiumId: String, sections: List<SeatSection>): void`<br>- `displaySectionDetails(section: SeatSection, count: int): void` | - Hiện thực hiển thị sơ đồ ghế ASCII map trực quan từ chuỗi do `StadiumController.buildSeatMap` trả về.<br>- Hiển thị bảng danh sách sân vận động và khán đài với định dạng cột ngay ngắn.<br>- Đấu nối View với `StadiumController`.<br>- **Deliverable:** Sơ đồ ghế hiển thị rõ ràng ký hiệu trạng thái `[O]`, `[X]`, `[L]`. |
| **Huy** | Vé & Giao dịch | View | **`BookingView`**<br>- `displayBookingForm(seats: List<Seat>): void`<br>- `showBookingResult(success: boolean, msg: String): void`<br>- `displayBookingHistory(txns: List<BookingTransaction>): void`<br>- `displayCancelTicketForm(): void`<br><br>**`OrganizerView`**<br>- `displayPricingConfigForm(): void`<br>- `displayGenerateTicketsForm(): void`<br>- `displaySalesReport(salesData: Map<String, Object>): void` | - Xây dựng form đặt vé (nhập danh sách mã ghế tối đa 4 vé/lần).<br>- Hiển thị kết quả thanh toán, bảng lịch sử giao dịch và form hủy vé.<br>- Xây dựng giao diện cấu hình giá vé, phát hành vé và báo cáo doanh thu cho Organizer.<br>- Đấu nối với `BookingController` và `TicketController`.<br>- **Deliverable:** Luồng chọn ghế -> đặt vé -> in biên lai vé hoàn tất. |
| **Văn** | Test UI & Simulator Prep | Test | **`EndToEndBookingTest`**<br>- Kiểm thử tích hợp UI flow<br>- Chuẩn bị khung sườn UI cho module Simulator | - Viết kịch bản kiểm thử luồng console UI từ đầu đến cuối: Login Fan -> Xem trận -> Chọn khán đài -> Đặt vé -> Kiểm tra file CSV.<br>- Hỗ trợ bắt lỗi người dùng nhập sai dữ liệu.<br>- **Deliverable:** Bộ test case end-to-end pass 100%. |

---

### III. NGUYÊN TẮC BẮT BUỘC KHI VIẾT CODE (BÀN TAY SẮT)
1. **Chuẩn Package & Tên Lớp:** Package duy nhất là `org.stadium.view` (Model: `org.stadium.model`, Controller: `org.stadium.controller`). Tuyệt đối cấm dùng `com.group6.stadium`.
2. **Quy tắc 100% Tiền tố `this.`:** Bắt buộc 100% các truy cập thuộc tính instance (fields) và phương thức nội bộ trong class phải có tiền tố `this.` (ví dụ: `this.scanner`, `this.authController`).
3. **Tuyệt đối KHÔNG DÙNG `static` (Ngoại trừ hàm `main`):** Cấm khai báo `static class`, `static field` (lưu state/cache) hoặc `static method` helper. Mọi phương thức và trường dữ liệu bắt buộc là thể hiện đối tượng (`instance`).
4. **Kiến trúc MVC Thuần Khiết:** Tầng View chỉ nhận input từ bàn phím và in kết quả ra màn hình. Tuyệt đối không gọi trực tiếp Repository và không tự đọc/ghi file CSV.
5. **Class Diagram là Chân Lý:** Chỉ được code các phương thức có trong sơ đồ `mermaid-class-diagram.docx`.

---

### IV. BÁO CÁO KIỂM TOÁN CHÉO (TỪ SKILL LAB211-COMPREHENSIVE-REVIEWER)
1. 📊 **Class Diagram & Syntax Audit:** 100% các lớp View và phương thức phân công đều khớp chính xác với sơ đồ lớp chuẩn.
2. 🏛️ **MVC & Rules Compliance:** View tách biệt hoàn toàn với tầng lưu trữ file, tuân thủ nghiêm ngặt cấm `static` và bắt buộc `this.`.
3. 🗃️ **Data/CSV Sync:** Định dạng hiển thị khớp hoàn hảo với các trường dữ liệu CSV.
4. 🔗 **Cross-layer Sync:** Đấu nối mạch lạc với các Controller đã xây dựng ở Tuần 5.
### 👉 Phán quyết: 🟢 **[APPROVE - ĐẠT CHUẨN XUẤT SẮC 100%]**
