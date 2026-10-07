# Bảng Phân Công Nhiệm Vụ Tuần 5 (Controller Layer - Baseline NO_LOCK)

Dựa trên sơ đồ lớp chuẩn `mermaid-class-diagram.docx` và cấu trúc MVC, dưới đây là phân công code Tuần 5 cho các thành viên.

| Thành viên | Domain phụ trách | Tầng (Layer) | Class & Các hàm phân công (Chuẩn Sơ đồ) | Ghi chú |
| :--- | :--- | :--- | :--- | :--- |
| **Kiệt** | Account / Auth | Controller | **`AuthController`**<br>- `login(username, pass)`<br>- `registerFan(...)` | Đảm bảo tính năng đăng nhập và đăng ký Fan hoạt động tốt. (Tương đương yêu cầu FanController). |
| **Nam** | Sân & Ghế | Controller | **`StadiumController`**<br>- `getSections(stadiumId)`<br>- `buildSeatMap(sectionId)` | Lấy danh sách khu vực và render sơ đồ ghế (dạng ASCII map) để chuẩn bị cho giao diện. |
| **Huy** | Vé & Giao dịch | Controller | **`BookingController`**<br>- `bookTickets(...)` (Baseline: mechanism = NO_LOCK)<br>- `viewBookingHistory(fanId)` | Code logic đặt vé cơ bản chưa cần xử lý đụng độ. Hàm xem lịch sử đóng vai trò `getMyTickets`. |
| **Văn** | Simulation / Test | Test | Viết Test Case Đơn luồng | Viết Unit Test đảm bảo `BookingController` hoạt động chính xác với 1 thread duy nhất. Test case phải pass toàn bộ. |
| **Siêu** | Core / Reviewer | Reviewer | *Toàn bộ mã nguồn T5* | Review code các Controller, đảm bảo đúng chuẩn MVC và logic NO_LOCK cơ bản hoàn hảo trước khi sang đa luồng. |

---
**Lưu ý chung:**
- Tuân thủ tuyệt đối cấu trúc package: `org.stadium.controller`.
- Tầng Controller gọi xuống Tầng Repository (`CsvRepository`), tuyệt đối không tự đọc file CSV trực tiếp trong Controller.
