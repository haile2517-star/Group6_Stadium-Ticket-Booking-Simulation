🎫 Stadium Ticket Booking Simulation
Danh sách thành viên nhóm 6: 
Huỳnh Hoàng Siêu (Leader), MSSV: QE210005
Lê Chấn Huy, MSSV:
Nguyễn Tuấn Kiệt, MSSV:
Đinh Trọng Nam MSSV:
Phạm Vũ Gia Văn. MSSV:

Hệ thống mô phỏng bán vé trực tuyến cho các trận đấu bóng đá tại sân vận động, tập trung giải quyết bài toán đồng bộ hóa dữ liệu khi có hàng nghìn người dùng đặt vé cùng lúc.

📌 Bối cảnh dự án
Hệ thống mô phỏng bán vé trực tuyến cho các trận đấu bóng đá tại sân vận động, tập trung giải quyết bài toán đồng bộ hóa dữ liệu khi có hàng nghìn người dùng đặt vé cùng lúc.
Trong các hệ thống bán vé thực tế (TicketMaster, Shopee Tickets...), Double Booking là rủi ro nghiêm trọng nhất: cùng một ghế được bán cho hai người khác nhau vì thao tác "đọc trạng thái → kiểm tra → ghi vé" không phải là một thao tác atomic khi nhiều luồng (thread) cùng truy cập file dữ liệu.

Dự án xây dựng một hệ thống mô phỏng đầy đủ quy trình bán vé sân vận động — từ tạo trận đấu, đặt vé, cho đến xử lý hàng nghìn giao dịch đồng thời — với toàn bộ dữ liệu (sân vận động, khu vực, ghế, fan, vé, giao dịch) được lưu trữ dưới dạng file CSV, không sử dụng database.

Câu hỏi nghiên cứu (Big Question)

Cơ chế đồng bộ hóa nào đảm bảo không xảy ra Double Booking khi hàng nghìn Fan Threads cùng đặt vé cùng một lúc — và sự đánh đổi (trade-off) về throughput là gì?

Nhóm sẽ implement và benchmark thực nghiệm 4 cơ chế đồng bộ hóa:

Cơ chế	Mô tả ngắn
NO_LOCK	Baseline, không khóa — dùng để chứng minh hiện tượng double booking
FILE_LOCK	Dùng Java NIO FileLock để khóa ở mức file
SYNCHRONIZED	Dùng khối synchronized trong tầng Repository
OPTIMISTIC	Optimistic Locking dựa trên trường version của Seat — thread chỉ được ghi nếu version đọc ra khớp với version hiện tại

Kết quả được đo và so sánh trên hai tiêu chí: throughput (vé/giây) và tỷ lệ double booking (%).

Bối cảnh nghiệp vụ
Sân vận động gồm nhiều khu vực (Section): VIP, Thường, Phổ thông, Đứng.
Mỗi khu vực chia thành nhiều Hàng (Row), mỗi hàng có nhiều Ghế (Seat).
Mỗi ghế có 3 trạng thái: AVAILABLE → LOCKED (đang chọn) → BOOKED.
Một ghế đã BOOKED không thể bán lại — đây là ràng buộc toàn vẹn cốt lõi của hệ thống.
Fan đăng ký tài khoản và có thể đặt tối đa 4 vé/lần giao dịch.
⚙️ Chức năng hệ thống (Use Case)

Hệ thống có 3 tác nhân (actor) chính, tương ứng với 4 nhóm chức năng:

Actor	Vai trò
Ticket Seller (Organizer)	Người tổ chức trận đấu, quản lý thông tin & giá vé
Ticket Buyer (Fan)	Người mua vé xem trận đấu
System Admin (Tester/Admin)	Người sinh dữ liệu, chạy mô phỏng và xem báo cáo benchmark
1️⃣ Seller Operations — Quản lý trận đấu
Nhập thông tin trận đấu & thiết lập giá vé (Fill Match Info & Set Ticket Pricing)
Chỉnh sửa thông tin trận đấu (Edit Match Information)
Hủy trận đấu (Cancel Match Event)
2️⃣ Buyer Operations — Luồng đặt vé cốt lõi
Đăng ký & đăng nhập (Register & Login)
Xem danh sách trận đấu & giá vé (View Matches & Pricing)
Xem sơ đồ ghế (View Seat Map)
Đặt vé, 1–4 ghế/lần (Book Tickets)
Xem vé đã mua (View Purchased Tickets)
3️⃣ Booking Execution — Xử lý ở tầng backend
Kiểm tra tình trạng ghế còn trống (Check Seat Availability)
Cập nhật trạng thái & version của ghế (Update Seat Status & Version)
Lưu vé và bản ghi giao dịch (Save Ticket & Transaction Record)
4️⃣ Admin & Simulation Management — Sinh dữ liệu & mô phỏng
Sinh dữ liệu hệ thống dạng CSV, tối thiểu 10.000 dòng (Generate System CSV Data)
Chạy công cụ mô phỏng đồng thời, benchmark 4 cơ chế khóa (Run Concurrency Simulator)
Xem báo cáo benchmark: throughput & tỷ lệ xung đột (View Benchmark Reports)
🏗 Kiến trúc hệ thống

Toàn bộ hệ thống tuân thủ nghiêm ngặt mô hình MVC (Model – View – Controller):

Lớp	Trách nhiệm
Model	Dữ liệu + nghiệp vụ cốt lõi — đọc/ghi CSV, validate business rules, quản lý trạng thái Seat
View	Hiển thị + nhận input từ người dùng, gọi Controller
Controller	Điều phối flow — nhận request từ View, gọi Model, trả kết quả về View

⚠️ Không được truy cập file CSV trực tiếp từ Controller — mọi thao tác dữ liệu phải đi qua tầng Repository.

Entity chính: Stadium, Section, Seat, Match, Fan, Ticket, BookingTransaction — mỗi entity tương ứng một file CSV riêng.
