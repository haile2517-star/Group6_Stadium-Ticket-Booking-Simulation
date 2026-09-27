---
name: lab211-code-sync-checker
description: Kiem tra tinh dong bo (consistency) cua toan bo code du an LAB211 Stadium Ticket Booking: dam bao ten bien, kieu du lieu, gia tri Enum nhat quan xuyen suot tat ca cac tang Model, Repository, Controller, View. Kich hoat khi nghe "kiem tra dong bo", "co xung dot gi khong", "code cac tang khop nhau chua", "review toan bo", "consistency check".
---

# LAB211 — Code Consistency / Synchronization Checker

Skill nay kiem tra su nhat quan (consistency) XUYEN SUOT toan bo du an — khong phai tung file don le ma la su an khop giua cac tang voi nhau.

---

## 🎯 Khi Nao Kich Hoat
- Nguoi dung gui nhieu file Java cung luc de kiem tra cheo.
- Cau hoi nhu: "code cac ban co khop nhau khong", "co bi xung dot gi khong", "review truoc khi merge", "toan bo model co dong bo voi repository chua".
- Leader gop code tu nhieu branch va muon kiem tra truoc khi merge vao main.

---

## 🔍 Cac Tieu Chi Dong Bo Kiem Tra

### TIEU CHI 1 — Nhat Quan Ten & Kieu Du Lieu Giua Cac Tang
- Ten field trong Model phai khop CHINH XAC voi:
  - Ten cot trong file CSV tuong ung (trong thu muc data/).
  - Cach parse trong fromCsvLine() (thu tu cac cot).
  - Cach truy van trong Repository (findById, findByUsername, v.v.).
  - Cach su dung trong Controller.
- Vi du: Neu Seat co field `sectionId` (String) thi SeatRepository.findBySectionId(String sectionId) phai dung dung ten tham so va kieu nay.

### TIEU CHI 2 — Thu Tu Cot CSV Nhat Quan
- Thu tu truong trong toCsvLine() phai khop 100% voi:
  - Thu tu cot tieu de trong file CSV thuc te (dong 1 trong file data/).
  - Thu tu parse trong fromCsvLine(): line.split(",", -1)[0], [1], [2]... phai gan dung thu tu.
- Bao loi neu co entity nao parse sai thu tu hoac thua/thieu cot.

### TIEU CHI 3 — Enum Values Nhat Quan
- Gia tri String duoc ghi vao CSV phai khop voi ten hang so Enum (thuong dung .name() hoac .toString()).
- Kiem tra: file seats.csv co gia tri "AVAILABLE", "LOCKED", "BOOKED" -> SeatStatus enum phai co dung 3 hang so nay, khong them "RESERVED" hay doi thanh "FREE".
- Neu 2 enum khac nhau cung bieu dien 1 khai niem -> goi y gop thanh 1 enum chung.

### TIEU CHI 4 — Repository Khop Model
- Moi Repository phai co cac method tim kiem phu hop voi Model no quan ly.
- Khong duoc co method tim theo field khong ton tai trong Model.
- CsvRepository<T> generic phai su dung dung T extends BaseEntity.

### TIEU CHI 5 — Controller Khong Luu State Rieng
- Controller KHONG duoc co field List<T> hay Map<K,V> de luu cache du lieu rieng.
- Moi truy cap du lieu phai di qua Repository.
- View chi goi Controller, khong goi Repository truc tiep.

### TIEU CHI 6 — Dong Bo ID Format
- Format ID phai nhat quan xuyen suot:
  - Fan: "FAN001", "FAN002" (theo fans.csv).
  - Seat: "SEC_STD01_A_R01_S01" (theo seats.csv).
  - Match: "MTH01" (theo matches.csv).
- Neu code dung ID format khac voi CSV -> bao loi.

---

## 📊 Dinh Dang Bao Cao

```
## Ket Qua Kiem Tra Dong Bo Toan Du An

### TIEU CHI 1 — Ten & Kieu Du Lieu Xuyen Tang
✅/❌ [Mo ta ket qua]
Chi tiet loi (neu co):
  - [Tang A] dung `seatId` nhung [Tang B] dung `seat_id` -> XUNG DOT

### TIEU CHI 2 — Thu Tu Cot CSV
✅/❌ [Mo ta ket qua]

### TIEU CHI 3 — Enum Values
✅/❌ [Mo ta ket qua]

### TIEU CHI 4 — Repository Khop Model
✅/❌ [Mo ta ket qua]

### TIEU CHI 5 — Controller Sach
✅/❌ [Mo ta ket qua]

### TIEU CHI 6 — ID Format
✅/❌ [Mo ta ket qua]

---
### 🔴 Danh Sach Loi Nghiem Trong (Phai sua truoc khi merge)
1. [Mo ta loi, vi tri, cach sua cu the]

### 🟡 Canh Bao (Nen sua)
1. [Mo ta canh bao]

### 🟢 Tot (Giu nguyen)
- [Danh sach diem da dong bo]

### Tong: X/6 tieu chi dat | Trang thai: ✅ PASS / ❌ FAIL
```

---

## 📁 File CSV Tham Chieu (Thu Muc data/)
| File | Entity | So Dong |
|---|---|---|
| fans.csv | Fan | ~250 dong |
| organizers.csv | Organizer | 6 dong |
| admins.csv | Admin | 2 dong |
| stadiums.csv | Stadium | 3 dong |
| sections.csv | SeatSection | 6 dong |
| seats.csv | Seat | ~12.600 dong |
| matches.csv | Match | 5 dong |
| ticket_pricings.csv | TicketPricing | 14 dong |
| transactions.csv | BookingTransaction | ~60 dong |
| tickets.csv | Ticket | ~152 dong |
| simulation_results.csv | SimulationResult | 4 dong |
