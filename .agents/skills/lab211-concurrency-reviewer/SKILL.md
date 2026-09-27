---
name: lab211-concurrency-reviewer
description: Kiem tra va review code da luong (concurrency) cua du an LAB211 Stadium Ticket Booking. Tap trung vao 4 co che dong bo hoa (NO_LOCK, FILE_LOCK, SYNCHRONIZED, OPTIMISTIC) va xac minh rang chung chung minh duoc hien tuong Double Booking mot cach dung dan. Kich hoat khi nghe "kiem tra lock", "double booking", "concurrency", "da luong", "benchmark", "simulation", "review co che khoa".
---

# LAB211 — Concurrency & Double Booking Reviewer

Skill nay chuyen kiem tra phan **trong tam cua diem so cao nhat doan an**: Co che dong bo hoa va mo phong Double Booking.

---

## 🎯 Khi Nao Kich Hoat
- Nguoi dung nop code cac class lien quan: SimulationService, cac LockStrategy, SimulationController.
- Cau hoi nhu: "code khoa co dung khong", "NO_LOCK co chung minh duoc double booking khong", "OPTIMISTIC co an toan khong", "kiem tra throughput".

---

## 🔍 Quy Trinh Kiem Tra (5 Tang Kiem Tra)

### TANG 1 — Kiem Tra NO_LOCK (Baseline Chung Minh Loi)
Day la phan QUAN TRONG NHAT — phai chung minh duoc Double Booking xay ra:
- NO_LOCK phai KHONG CO bat ky co che dong bo nao (khong synchronized, khong lock, khong atomic).
- Chay nhieu thread dong thoi, thuat toan doc-kiem tra-ghi phai la non-atomic.
- Ket qua do duoc phai co doubleBookingCount > 0.
- ❌ Neu NO_LOCK khong phat sinh double booking -> FAIL nghiem trong (mat diem lon).
- ✅ Neu NO_LOCK co > 0 double bookings -> PASS tang nay.

### TANG 2 — Kiem Tra FILE_LOCK
- Phai su dung java.nio.channels.FileLock hoac FileChannel.
- Khoa phai duoc giai phong trong khoi finally {} de tranh treo file (deadlock tai tang file).
- Phai kiem tra: Neu lock khong lay duoc (null hoac exception) thi xu ly ra sao.
- Ket qua: doubleBookingCount phai = 0 (neu dung).

### TANG 3 — Kiem Tra SYNCHRONIZED
- Phai su dung synchronized tren dung doi tuong chia se (cung monitor object).
- KHONG duoc synchronized tren doi tuong khac nhau cho cung 1 tai nguyen (se vo hieu hoa).
- Kiem tra scope: synchronized method hay synchronized block — co bao phu du vung xung dot khong.
- Ket qua: doubleBookingCount phai = 0 (neu dung).

### TANG 4 — Kiem Tra OPTIMISTIC (Version-based CAS)
Day la co che phuc tap nhat — kiem tra ky:
1. Doc seat hien tai tu CSV + lay version hien tai.
2. Kiem tra seat.isAvailable() TRUOC khi ghi.
3. Khi ghi, PHAI kiem tra lai: version doc ra == version trong file hien tai (Check-And-Set).
4. Neu version khac (thread khac da ghi truoc) -> FAIL giao dich, khong ghi de.
5. Neu version khop -> Ghi + tang version len 1.
- ❌ Neu buoc 3 bi bo qua hoac khong atomic -> van co the bi double booking.
- ✅ Neu thuc hien du 5 buoc -> PASS.

### TANG 5 — Kiem Tra SimulationService (Dieu Phoi)
- ThreadPool phai duoc tao dung: ExecutorService voi so luong thread theo tham so.
- Phai do chinh xac thoi gian thuc thi: System.nanoTime() hoac System.currentTimeMillis().
- Throughput = successCount / (executionTimeMs / 1000.0) — don vi: tickets/second.
- doubleBookingRate = (doubleBookingCount / totalRequests) * 100 — don vi: %.
- Ket qua phai duoc luu vao SimulationResult va ghi ra simulation_results.csv.

---

## 📊 Dinh Dang Bao Cao

```
## Ket Qua Review Concurrency

### Tang 1 — NO_LOCK: ✅/❌
  Double Booking xay ra: Co/Khong
  Van de: [Mo ta neu co]

### Tang 2 — FILE_LOCK: ✅/❌
  Co FileLock dung cach: Co/Khong
  finally block giai phong lock: Co/Khong
  Van de: [Mo ta neu co]

### Tang 3 — SYNCHRONIZED: ✅/❌
  Dung dung monitor object: Co/Khong
  Scope bao phu du: Co/Khong
  Van de: [Mo ta neu co]

### Tang 4 — OPTIMISTIC (Version CAS): ✅/❌
  Doc version truoc: Co/Khong
  Kiem tra lai truoc khi ghi: Co/Khong
  Tang version sau khi ghi: Co/Khong
  Van de: [Mo ta neu co]

### Tang 5 — SimulationService: ✅/❌
  ThreadPool dung cach: Co/Khong
  Do thoi gian chinh xac: Co/Khong
  Tinh throughput & rate: Co/Khong
  Ghi ket qua ra CSV: Co/Khong

---
🔴 Loi nghiem trong: [...]
🟡 Canh bao hieu nang: [...]
🟢 Dat chuan: [...]

Du kien ket qua so sanh (mong doi):
| Co Che | Double Booking | Throughput |
|---|---|---|
| NO_LOCK | Cao (> 0) | Cao nhat |
| FILE_LOCK | 0 | Thap nhat |
| SYNCHRONIZED | 0 | Trung binh |
| OPTIMISTIC | 0 | Cao (gan NO_LOCK) |
```
