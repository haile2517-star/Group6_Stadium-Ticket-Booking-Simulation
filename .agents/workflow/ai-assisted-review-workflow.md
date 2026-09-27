# Quy Trình Review Code Với AI (Antigravity)

Hướng dẫn Leader (Siêu) sử dụng AI để nghiệm thu code từng thành viên.

---

## 🤖 Nguyên Tắc Cơ Bản

AI **chỉ biết những gì bạn đưa vào trong phiên chat đang mở**.
- Mở file Java trong IDE trước khi hỏi AI.
- Cung cấp file CSV tương ứng nếu muốn test parse.
- Mỗi phiên chat mới = AI quên tất cả → cần cung cấp lại context.

---

## 📋 Quy Trình Review PR Của Từng Thành Viên

### A. Review Model Layer (Tuần 3)

**Bước 1 — Chuẩn bị:**
```
1. Mở file Java cần review trong IDE (ví dụ: Fan.java của Kiệt)
2. Mở README.md (để AI có context về CSV headers)
```

**Bước 2 — Hỏi AI:**
```
Lần lượt hỏi:

"Check Fan.java của Kiệt đúng diagram chưa"
→ AI dùng skill lab211-class-diagram-checker

"Test parse Fan.java với fans.csv"
→ AI dùng skill lab211-csv-model-tester

"Review PR của Kiệt có merge được chưa"
→ AI dùng skill lab211-pr-merge-review
```

**Bước 3 — Ghi kết quả vào PR comment:**
```markdown
## Kết quả AI Review (Leader)

✅/❌ Class Diagram: [kết quả]
✅/❌ CSV Parsing: [kết quả]
✅/❌ Checklist PR: [X/17 tiêu chí]

Quyết định: APPROVE / REQUEST CHANGES
Cần sửa: [liệt kê cụ thể nếu có]
```

---

### B. Review Concurrency Code (Tuần 8-9)

**Bước 1 — Chuẩn bị:**
```
1. Mở SimulatorController.java, các LockStrategy files
2. Mở simulation_results.csv để đối chiếu
```

**Bước 2 — Hỏi AI:**
```
"Review NO_LOCK strategy có chứng minh được double booking không"
→ AI dùng skill lab211-concurrency-reviewer

"Kiểm tra OPTIMISTIC lock có implement đúng version CAS không"
→ AI dùng skill lab211-concurrency-reviewer

"Kiểm tra toàn bộ các tầng có đồng bộ với nhau không"
→ AI dùng skill lab211-code-sync-checker
```

---

## 🗓 Lịch Review Hàng Tuần (Leader)

| Thời điểm | Việc cần làm | Skill dùng |
|---|---|---|
| Thứ 2 sáng | Kiểm tra main branch ổn định | Thủ công (compile check) |
| Thứ 6 sáng | Review từng PR vừa nộp | `lab211-pr-merge-review` + `lab211-csv-model-tester` |
| Thứ 6 chiều | Merge PR đã pass, request changes PR fail | Thủ công trên GitHub |
| Cuối mỗi milestone | Kiểm tra đồng bộ toàn dự án | `lab211-code-sync-checker` |

---

## 💬 Câu Lệnh AI Mẫu Theo Từng Tình Huống

```
# Kiểm tra 1 class theo diagram
"Review Seat.java của Nam, kiểm tra có đúng class diagram không"

# Test CSV parsing
"Test Seat.java với seats.csv, fromCsvLine có đúng thứ tự cột không"

# Kiểm tra đồng bộ toàn dự án (sau khi merge nhiều PR)
"Kiểm tra toàn bộ model layer có đồng bộ với nhau và với CSV không"

# Trước khi merge
"Review PR của Huy — Ticket.java và BookingTransaction.java — có merge được chưa"

# Review concurrency
"Review code SimulatorController — NO_LOCK có tạo ra double booking không"
```

---

## 📁 Tài Liệu Hỗ Trợ AI Nên Mở Khi Review

Để AI có context tốt nhất, hãy đảm bảo các file này đang mở trong IDE:
- `README.md` — quy ước package, ID, CSV headers
- File Java đang review
- File CSV tương ứng (trong `data/`)
- `.information/mermaid-class-diagram.docx` — nếu cần đối chiếu diagram chính xác
