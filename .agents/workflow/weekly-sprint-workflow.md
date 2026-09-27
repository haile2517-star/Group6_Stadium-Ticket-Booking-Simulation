# Quy Trình Làm Việc Hàng Tuần (Sprint Workflow)

Áp dụng cho toàn bộ giai đoạn T3 → T10 của dự án LAB211 Nhóm 6.

---

## 📅 Lịch Sprint Chuẩn (Mỗi Tuần = 1 Sprint)

```
Thứ 2 (Đầu tuần)   → Họp kickoff: Leader giao nhiệm vụ, thống nhất interface
Thứ 2 - Thứ 5      → Thành viên code trên branch riêng
Thứ 5 (Cuối ngày)  → Hard deadline nộp PR
Thứ 6              → Leader review PR bằng AI Skills + quyết định merge
Thứ 7 - CN (nếu cần) → Fix lỗi sau review, chuẩn bị cho tuần sau
```

---

## 🔄 Quy Trình 1 Sprint (Chi Tiết Từng Bước)

### BƯỚC 1 — Kickoff (Thứ 2, 30 phút)
**Người thực hiện:** Leader (Siêu)
- [ ] Kiểm tra nhánh `main` đã ổn định chưa (không có conflict, compile được).
- [ ] Thông báo nhiệm vụ tuần mới: ai code class gì, interface (method signature) cần thống nhất.
- [ ] Ghi rõ **interface công khai** giữa các tầng vào chat nhóm (ví dụ: `FanRepository.findByUsername(String): Fan`).
- [ ] Mỗi thành viên checkout branch mới: `git checkout -b feat/t[số tuần]-[tên mảng]`.

### BƯỚC 2 — Coding (Thứ 2 → Thứ 5)
**Người thực hiện:** Từng thành viên
- [ ] Code trong đúng file/class được giao (theo README.md > Phân công).
- [ ] KHÔNG sửa code của người khác mà không báo trước.
- [ ] Commit thường xuyên với message rõ ràng: `feat(Fan): implement toCsvLine and fromCsvLine`.
- [ ] Khi xong, tự kiểm tra nhanh bằng AI: *"check [ClassName].java đúng diagram chưa"*.

### BƯỚC 3 — Tạo Pull Request (Deadline Thứ 5, 23:59)
**Người thực hiện:** Từng thành viên
- [ ] `git push origin feat/t[N]-[tên]`
- [ ] Tạo PR trên GitHub với tiêu đề: `[T3] Kiệt - Account Layer: Fan, Organizer, Admin`
- [ ] Trong mô tả PR, điền checklist tự đánh giá (xem template bên dưới).
- [ ] Tag Leader (Siêu) để review.

### BƯỚC 4 — AI Review PR (Thứ 6, sáng)
**Người thực hiện:** Leader dùng Antigravity AI
1. Mở file Java từ PR trong IDE.
2. Hỏi AI: *"review PR của [tên] có merge được chưa"* → kích hoạt skill `lab211-pr-merge-review`.
3. Nếu có lỗi CSV: *"test parse fans.csv với Fan.java này"* → kích hoạt skill `lab211-csv-model-tester`.
4. Ghi kết quả vào comment PR trên GitHub.

### BƯỚC 5 — Merge hoặc Request Changes (Thứ 6, chiều)
**Người thực hiện:** Leader
- **Nếu PASS:** `Squash and merge` vào `main` → xóa branch đã merge.
- **Nếu FAIL:** Comment chi tiết điểm cần sửa, tag người code → họ fix và push thêm commit lên branch cũ.

---

## 📝 Template Mô Tả PR (Dán vào khi tạo PR)

```markdown
## PR: [Tuần] — [Tên] — [Mảng Code]

### Danh sách class đã code:
- [ ] ClassName1.java
- [ ] ClassName2.java

### Tự kiểm tra trước khi nộp:
- [ ] Code compile không lỗi đỏ
- [ ] Package đúng: com.group6.stadium.[tang]
- [ ] Không còn code mẫu IntelliJ (IO.println, vòng for demo)
- [ ] fromCsvLine() dùng split(",", -1)
- [ ] Thứ tự cột CSV đã đối chiếu với README.md
- [ ] Enum dùng .valueOf() không dùng String thủ công
- [ ] Getter/Setter đầy đủ

### Ghi chú thêm (nếu có):
[Điền ở đây]
```

---

## 🚨 Quy Tắc Không Được Vi Phạm

1. **KHÔNG push trực tiếp lên `main`** — mọi thay đổi phải qua PR.
2. **KHÔNG merge PR của chính mình** — phải chờ Leader review.
3. **KHÔNG đổi tên class/field/column** mà không thông báo — ảnh hưởng toàn bộ codebase.
4. **KHÔNG để PR tồn tại > 48 tiếng mà không có phản hồi** — Leader phải xử lý trong ngày Thứ 6.
