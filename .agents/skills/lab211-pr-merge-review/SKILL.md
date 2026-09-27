---
name: lab211-pr-merge-review
description: Kiem tra nhanh cac dieu kien can thiet truoc khi Leader chap nhan merge mot Pull Request (PR) tu branch cua thanh vien vao main trong du an LAB211 Stadium Ticket Booking. Kich hoat khi nghe "review PR", "co the merge chua", "kiem tra truoc khi merge", "chap nhan code", "duyet code".
---

# LAB211 — PR / Merge Review Checklist

Skill nay la cong cu **gach dau dong (checklist) nghiem thu nhanh** giup Leader quyet dinh merge hay yeu cau sua them truoc khi tich hop code vao nhanh main.

---

## 🎯 Khi Nao Kich Hoat
- Leader nhan duoc thong bao tu thanh vien rang ho da hoan thanh code.
- Cau hoi nhu: "Van xong Seat.java roi, co merge duoc chua", "review PR cua Kiet", "duyet code Tuan 3".

---

## ✅ Checklist Nghiem Thu PR (Phai Pass 100%)

### NHOM A — Chat Luong Code Co Ban
- [ ] A1: Code bien dich thanh cong, khong co loi compile (red underline).
- [ ] A2: Khong con code mau IntelliJ (IO.println, dong TIP comment, vong lap demo i=1..5).
- [ ] A3: Package khai bao dung: `package com.group6.stadium.[tang];`
- [ ] A4: Khong co package org.example, org.stadium, v.v. (phai la com.group6.stadium).

### NHOM B — Tuan Thu Class Diagram
- [ ] B1: Ten class, ten field, kieu du lieu khop Class Diagram (dung skill lab211-class-diagram-checker de kiem tra).
- [ ] B2: Quan he ke thua dung (extends, implements).
- [ ] B3: Tat ca method trong diagram da duoc implement du (khong con throw new UnsupportedOperationException).

### NHOM C — Nghiem Thu CSV Model (Chi ap dung cho Tuan 3)
- [ ] C1: fromCsvLine() dung split(",", -1).
- [ ] C2: Thu tu cot khop header file CSV trong data/.
- [ ] C3: Ep kieu dung (int, double, Enum.valueOf()) khong de la String.
- [ ] C4: toCsvLine() xuat dung so cot, khong thua dau phay cuoi.

### NHOM D — Tuan Thu Kien Truc MVC
- [ ] D1: Model KHONG co method viet file CSV truc tiep (khong co FileWriter, BufferedWriter trong Entity).
- [ ] D2: Repository ke thua CsvRepository<T>, khong tu mo BufferedReader/Writer rieng.
- [ ] D3: Controller KHONG co field `private List<T> data = new ArrayList<>()` tu quan ly vong doi.
- [ ] D4: View KHONG import hoac goi truc tiep Repository.

### NHOM E — An Toan & Code Sach
- [ ] E1: Khong de lai TODO comment khong co ke hoach thuc hien.
- [ ] E2: Khong co magic number (dung hang so hoac Enum thay vi "0", "1", "AVAILABLE" dang String).
- [ ] E3: Getter/Setter day du cho tat ca field non-private (theo yeu cau su dung tu cac tang khac).

---

## 📊 Dinh Dang Bao Cao

```
## Ket Qua Review PR: [Ten Branch / Ten File] — [Ten Thanh Vien]

| Nhom | Tieu Chi | Ket Qua |
|---|---|---|
| A | Code bien dich OK | ✅/❌ |
| A | Sach code mau IntelliJ | ✅/❌ |
| A | Package dung | ✅/❌ |
| B | Khop Class Diagram | ✅/❌ |
| B | Ke thua dung | ✅/❌ |
| B | Method day du | ✅/❌ |
| C | split(",", -1) | ✅/❌ |
| C | Thu tu cot CSV | ✅/❌ |
| C | Ep kieu dung | ✅/❌ |
| C | toCsvLine() dung dinh dang | ✅/❌ |
| D | Model sach | ✅/❌ |
| D | Repository ke thua dung | ✅/❌ |
| D | Controller khong luu state | ✅/❌ |
| D | View khong goi Repository | ✅/❌ |
| E | Khong TODO khong ke hoach | ✅/❌ |
| E | Khong magic number | ✅/❌ |
| E | Getter/Setter du | ✅/❌ |

---
Tong: X/17 tieu chi dat

### 🔴 Van De Nghiem Trong (Block merge):
- [...]

### 🟡 Can Sua (Khong block nhung nen sua trong sprint nay):
- [...]

### ✅ QUYET DINH:
- [ ] CHAP NHAN MERGE vao main
- [ ] TU CHOI — Yeu cau sua: [danh sach cu the]
```
