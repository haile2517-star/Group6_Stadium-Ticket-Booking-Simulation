---
trigger: always_on
---

# Quy Tắc Bắt Buộc — Dự Án LAB211 Nhóm 6

---

## QUY TẮC 1: MODELS KHÔNG ĐƯỢC TỰ COMMIT CODE

Các model agents KHÔNG được tự động chỉnh sửa code và commit lên GitHub.
Nhiệm vụ của AI là CHỈ hiển thị code trong khung chat — người làm sẽ là người ghi lại code đó.

---

## QUY TẮC 2: CODE PHẢI KHỚP CHÍNH XÁC CLASS DIAGRAM — KHÔNG THÊM, KHÔNG BỚT

**Đây là quy tắc cao nhất, không có ngoại lệ.**

### Nguyên tắc cốt lõi:
- Class Diagram là **nguồn chân lý duy nhất (Single Source of Truth)** của dự án.
- Code Java phải phản ánh CHÍNH XÁC Class Diagram: đúng tên class, đúng tên field, đúng kiểu dữ liệu, đúng tên method, đúng access modifier, đúng quan hệ kế thừa.
- **Kể cả khi Class Diagram có sai sót hoặc bất hợp lý**, code vẫn PHẢI theo diagram — không được tự ý sửa để "đúng hơn".

### AI phải làm gì khi phát hiện diagram có vấn đề:
- ✅ Code đúng theo diagram trước.
- ✅ Sau đó báo cáo vào mục **[GỢI Ý SỬA DIAGRAM]** để Leader quyết định.
- ❌ KHÔNG tự ý thêm field mà diagram không có.
- ❌ KHÔNG tự ý đổi tên method dù tên cũ trông lạ.
- ❌ KHÔNG tự ý bỏ bớt method dù thấy không cần thiết.
- ❌ KHÔNG thêm implements, extends ngoài diagram.

### Ví dụ cụ thể:
```
Diagram có: -version: int trong Seat
→ Code PHẢI là: private int version;   (không được đổi sang long dù int có thể overflow)

Diagram có: +updateMatchDetails(title: String, start: String, end: String): void
→ Code PHẢI có đúng method này với đúng 3 tham số String  (không được thêm tham số thứ 4)

Diagram KHÔNG có constructor trong Fan
→ Code KHÔNG được tự thêm constructor có tham số (chỉ thêm nếu diagram ghi rõ)
```

### Khi nhận được yêu cầu code:
1. Đọc Class Diagram hoặc tham chiếu `.information/mermaid-class-diagram.docx`.
2. Implement đúng 100% theo diagram.
3. Nếu phát hiện bất kỳ điểm nào trong diagram có vẻ sai → ghi chú cuối bài, KHÔNG tự sửa.

---

## QUY TẮC 3: THỨ TỰ ƯU TIÊN KHI CÓ XUNG ĐỘT THÔNG TIN

```
Class Diagram  >  README.md  >  File CSV thực tế  >  Suy luận của AI
```

Nếu có mâu thuẫn giữa các nguồn, AI phải hỏi Leader để xác nhận — không tự quyết định.