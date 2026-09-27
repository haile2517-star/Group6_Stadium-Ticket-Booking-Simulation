---
name: lab211-class-diagram-checker
description: Kiem tra xem code Java cua du an LAB211 Stadium Ticket Booking co khop CHINH XAC voi Class Diagram (drawio/Mermaid) hay khong. Nguyen tac: Class Diagram la nguon chan ly duy nhat - du diagram co sai thi code van phai theo dung diagram, khong tu y thay doi. Kich hoat khi co yeu cau "kiem tra code khop diagram khong", "code dung chua", "review class", "check model".
---

# LAB211 — Class Diagram Checker

Skill nay kiem tra tinh tuan thu **mot chieu nghiem ngat**: Code Java phai phan anh dung Class Diagram.

> ⚠️ **Nguyen tac bat di bat dich**: Class Diagram la nguon chan ly (Source of Truth) duy nhat.
> - Neu Class Diagram co sai sot, code van phai theo diagram.
> - Neu ban nghi diagram sai, **bao cao ra muc [GOI Y SUA DIAGRAM]** sau khi da hoan thanh kiem tra — KHONG tu sua code theo y kien ca nhan.

---

## 🎯 Khi Nao Kich Hoat
Kich hoat khi nguoi dung dua vao:
- File Java (.java) thuoc cac tang Model, Repository, Controller, View.
- Anh / text mo ta Class Diagram (Mermaid classDiagram, drawio, van ban).
- Cau hoi nhu: "code nay dung chua", "check class nay", "model co khop diagram khong", "review Fan.java".

---

## 🔍 Quy Trinh Kiem Tra (Thuc Hien Du 5 Diem)

### Diem 1 — Ten Class & Cau Truc Ke Thua
- Ten class phai khop chinh xac tung ky tu voi ten trong diagram (case-sensitive).
- Quan he ke thua (extends) va thuc thi interface (implements) phai dung y chang diagram.
- Kiem tra: Fan extends Account, Account extends BaseEntity, CsvRepository<T> implements IRepository<T>.

### Diem 2 — Thuoc Tinh (Fields/Attributes)
- Ten bien phai khop chinh xac (accountId khong duoc viet thanh id hay userId).
- Kieu du lieu phai khop: int khong thay bang long, double khong thay bang float.
- Access modifier phai dung: private, protected, public.
- Enum type phai dung ten class Enum (SeatStatus status khong duoc la String status).

### Diem 3 — Phuong Thuc (Methods)
- Ten method phai khop chinh xac (toCsvLine() khong duoc la toCsv()).
- Kieu tham so va kieu tra ve phai dung diagram.
- Abstract method phai co abstract trong code.
- Method nghiep vu (isAvailable(), incrementVersion(), markAsBooked()) phai du va dung class.

### Diem 4 — Enum Values
- Ten enum phai khop: SeatStatus, MatchStatus, AccountStatus, UserRole, SynchronizationMechanism.
- Hang so enum phai day du: AVAILABLE, LOCKED, BOOKED — khong thieu, khong thua.

### Diem 5 — Package & Dat File Dung Cho
- Kiem tra package dung: com.group6.stadium.model, ...controller, ...repository, ...view.
- Class nao nam sai package bao loi ro.

---

## 📊 Dinh Dang Bao Cao Dau Ra

```
## Ket Qua Kiem Tra: [TEN_FILE.java]

### ✅ Dat Chuan (Khop 100% Diagram)
- [Liet ke cac diem da dung]

### 🔴 [PHAI SUA — Vi Pham Diagram]
| # | Vi tri | Diagram yeu cau | Code hien tai | Cach sua |
|---|--------|-----------------|---------------|----------|
| 1 | Field status trong Seat | SeatStatus status | String status | Doi kieu thanh SeatStatus |

### 🟡 [GOI Y SUA DIAGRAM] (Khong bat sua code)
- [Diem nao trong diagram co ve sai logic — ghi nhan de leader quyet dinh]

### 📋 Tong Ket
- So diem dat: X/5
- Trang thai: ✅ PASS / ❌ FAIL (can sua truoc khi merge)
```

---

## 📌 Bang Phan Cong Code Chuan Nhom 6 (Tham Khao)
| Thanh vien | Class chiu trach nhiem |
|---|---|
| Sieu (Leader) | BaseEntity, CsvRepository<T>, Match, MatchFilter, MatchStatus |
| Kiet | Account, Fan, Organizer, Admin, AccountStatus, UserRole |
| Nam | Stadium, SeatSection, Seat, SeatType, SeatStatus |
| Huy | TicketPricing, BookingTransaction, Ticket, TicketStatus, TransactionStatus |
| Van | SimulationResult, AccountSearchCriteria, SynchronizationMechanism |
