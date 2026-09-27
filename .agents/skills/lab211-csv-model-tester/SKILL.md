---
name: lab211-csv-model-tester
description: Kiem tra va nghiem thu tang Model cua du an LAB211 bam sat vao 11 file CSV thuc te trong thu muc data/. Xac nhan rang ham toCsvLine() va fromCsvLine() cua moi Entity hoat dong chinh xac voi du lieu thuc. Kich hoat khi co yeu cau "test model", "kiem tra parse CSV", "nghiem thu Tuan 3", "fromCsvLine dung chua", "thu tu cot co khop khong".
---

# LAB211 — CSV Model Tester (Nghiem Thu Tang 3)

Skill nay duoc thiet ke de **nghiem thu chinh thuc Milestone Tuan 3** (DoD): Dam bao 100% Entity parse & serialize khop voi 11 file CSV thuc te trong thu muc `data/`.

---

## 🎯 Khi Nao Kich Hoat
- Thanh vien nop code Model (VD: Nam nop Seat.java) va Leader muon kiem tra.
- Cau hoi nhu: "Seat.java cua Nam dung chua", "kiem tra parse seats.csv", "nghiem thu model Tuan 3".
- Sau khi toan nhom nop xong, kiem tra tong the truoc khi merge vao main.

---

## 🔍 Quy Trinh Kiem Tra (6 Buoc)

### Buoc 1 — Kiem Tra Thu Tu Cot CSV vs fromCsvLine()
So sanh thu tu cot trong dong tieu de cua file CSV tuong ung voi thu tu parse trong fromCsvLine():

Vi du cho Seat:
- CSV header: `seatId,sectionId,rowNumber,seatNumber,seatType,status,version`
- fromCsvLine() phai parse: parts[0]=seatId, parts[1]=sectionId, parts[2]=rowNumber (int), parts[3]=seatNumber, parts[4]=seatType (enum), parts[5]=status (enum), parts[6]=version (long/int)
- Bao loi neu thu tu sai hoac ep kieu sai (vi du parts[2] duoc gan vao String thay vi int).

### Buoc 2 — Kiem Tra Quy Tac Split
- PHAI su dung `line.split(",", -1)` (voi -1 de giu cac truong rong cuoi dong).
- KHONG duoc dung `line.split(",")` don thuan (se bo sot truong rong).
- Bao canh bao neu thieu tham so -1.

### Buoc 3 — Kiem Tra toCsvLine() Xuat Dung Dinh Dang
- Chuoi CSV xuat ra phai:
  - Co dung so luong cot (dau phay phan cach).
  - Co thu tu khop voi header file CSV.
  - Khong co dau phay thua o cuoi dong.
  - Enum duoc xuat bang .name() (cho ra "AVAILABLE" khong phai "SeatStatus.AVAILABLE").

### Buoc 4 — Kiem Tra Kieu Du Lieu & Ep Kieu
- int/long: Phai dung Integer.parseInt() / Long.parseLong() — khong duoc de lai la String.
- double: Phai dung Double.parseDouble().
- Enum: Phai dung SeatStatus.valueOf(parts[N]) hoac SeatStatus.fromString() — khong so sanh String thu cong.
- LocalDateTime / Date: Neu co, kiem tra dinh dang da tong nhat chua.

### Buoc 5 — Do An Toan (Null Safety / Exception Handling)
- fromCsvLine() co xu ly truong hop dong CSV bi loi (it cot hon mong doi) khong?
- Co nen them try-catch hoac kiem tra do dai truoc khi truy cap parts[N].

### Buoc 6 — Chay Thu Voi Du Lieu Thuc (Neu Co File CSV Dinh Kem)
Neu nguoi dung dinh kem file CSV hoac dan du lieu CSV mau, thi:
1. Lay dong dau tien (khong phai tieu de) lam du lieu test.
2. Chay thu parse qua fromCsvLine() (giai lap bang tay tung buoc).
3. Kiem tra xem toCsvLine() tao lai duoc chuoi giong dong goc hay khong.
4. Bao kha nang Round-Trip: original_line == entity.toCsvLine().

---

## 📊 Dinh Dang Bao Cao

```
## Ket Qua Nghiem Thu Model: [TEN_CLASS.java] ↔ [ten_file.csv]

### Buoc 1 — Thu Tu Cot: ✅/❌
  CSV header: col0, col1, col2, ...
  fromCsvLine(): parts[0]→field0(kieu), parts[1]→field1(kieu), ...
  [Neu sai: mo ta ro lech o cot nao]

### Buoc 2 — Quy Tac Split: ✅/❌
  [Dung split(",", -1) hay khong]

### Buoc 3 — toCsvLine() Dinh Dang: ✅/❌
  [Kiem tra thu tu, dau phay, khong thua cuoi dong]

### Buoc 4 — Kieu Du Lieu: ✅/❌
  [Liet ke tung truong va kieu ep kieu tương ung]

### Buoc 5 — An Toan: ✅/❌
  [Co xu ly loi hay khong]

### Buoc 6 — Round-Trip Test (neu co CSV data): ✅/❌
  Du lieu goc: "..."
  Sau parse+serialize: "..."
  Khop: Co/Khong

---
🔴 Loi can sua: [...]
🟡 Canh bao: [...]
🟢 Dat chuan: [...]

TRANG THAI NGHIEM THU: ✅ PASS — Du dieu kien merge / ❌ FAIL — Can sua truoc khi merge
```

---

## 📁 Bang Ket Noi Entity ↔ CSV ↔ Thanh Vien
| Entity | File CSV | Header (Thu tu cot) | Nguoi code |
|---|---|---|---|
| Seat | seats.csv | seatId,sectionId,rowNumber,seatNumber,seatType,status,version | Nam |
| SeatSection | sections.csv | sectionId,stadiumId,sectionName,sectionType,totalRows,seatsPerRow | Nam |
| Stadium | stadiums.csv | stadiumId,name,address,capacity | Nam |
| Fan | fans.csv | fanId,username,passwordHash,email,phone,status,fullName,fanType | Kiet |
| Organizer | organizers.csv | organizerId,username,passwordHash,email,phone,status,orgName | Kiet |
| Admin | admins.csv | adminId,username,passwordHash,fullName,email,phone,status | Kiet |
| Match | matches.csv | matchId,stadiumId,organizerId,homeTeam,awayTeam,matchDate,status | Sieu |
| TicketPricing | ticket_pricings.csv | pricingId,matchId,seatType,price | Huy |
| BookingTransaction | transactions.csv | transactionId,fanId,matchId,totalAmount,ticketCount,transactionDate,status | Huy |
| Ticket | tickets.csv | ticketId,matchId,seatId,transactionId,fanId,price,bookingDate,status | Huy |
| SimulationResult | simulation_results.csv | resultId,mechanism,threadCount,totalRequests,successCount,failedCount,doubleBookingCount,throughput,doubleBookingRate,executionTimeMs | Van |
