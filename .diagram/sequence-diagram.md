# Sequence Diagram — Stadium Ticket Booking Simulation

## Code Mermaid hoàn chỉnh

```mermaid
sequenceDiagram
    autonumber
    actor User as User (Fan/Org/Admin)
    participant Main
    participant MainController
    participant MainView
    participant AuthController
    participant AdminRepository
    participant OrganizerRepository
    participant FanRepository
    participant MatchController
    participant MatchView
    participant TicketController
    participant MatchRepository
    participant SectionRepository
    participant StadiumController
    participant StadiumView
    participant BookingView
    participant BookingController
    participant SeatRepository
    participant TicketRepository
    participant TicketPricingRepository
    participant TransactionRepository
    participant OrganizerView
    participant AdminView
    participant AdminController

    %% ===== 1. KHOI DONG UNG DUNG =====
    Note over User,AdminController: 1. Khởi động ứng dụng
    Main->>MainController: new MainController()
    MainController->>MainController: initializeDependencies()
    Note right of MainController: Tạo Repository, Controller, View<br/>và MainView(scanner, this)
    Main->>MainController: start()
    MainController->>MainView: displayWelcomeBanner()

    %% ===== 2. DANG NHAP =====
    Note over User,AdminController: 2. Đăng nhập (Guest chọn 7 - Login)
    MainController->>MainView: displayGuestMenu()
    MainView-->>User: Hiển thị Guest Menu
    User->>MainView: Nhập lựa chọn "7"
    MainView->>MainController: handleGuestChoice("7")
    MainController-->>User: Yêu cầu nhập Username, Password
    User->>MainController: username, password
    MainController->>AuthController: login(username, pass)
    AuthController->>AdminRepository: findByUsername(username)
    AdminRepository-->>AuthController: Admin hoặc null
    AuthController->>FanRepository: findByUsername(username)
    FanRepository-->>AuthController: Fan hoặc null
    AuthController->>OrganizerRepository: findByUsername(username)
    OrganizerRepository-->>AuthController: Organizer hoặc null
    alt Tìm thấy tài khoản ACTIVE & đúng mật khẩu
        AuthController-->>MainController: Account (Fan/Organizer/Admin)
        MainController->>MainController: currentUser = acc
        MainController-->>User: [SUCCESS] Login successful
    else Không tìm thấy hoặc sai mật khẩu hoặc không ACTIVE
        AuthController-->>MainController: null
        MainController-->>User: [FAILED] Login failed
    end

    %% ===== 3. DAT VE (FAN) =====
    Note over User,AdminController: 3. Đặt vé (Fan Menu chọn 5)
    MainController->>MainView: displayFanMenu()
    MainView-->>User: Hiển thị Fan Menu
    User->>MainView: Nhập lựa chọn "5"
    MainView->>MainController: handleFanChoice("5")
    MainController->>MainController: handleFanBookingFlow()

    MainController->>MatchController: viewAllMatches()
    MatchController->>MatchRepository: findAll()
    MatchRepository-->>MatchController: List of Match
    MatchController-->>MainController: List of Match
    MainController->>MatchView: displayMatches(matches)
    MainController-->>User: Yêu cầu nhập match ID
    User->>MainController: requestedMatchId

    alt Không tìm thấy trận đấu (selectedMatch == null)
        MainController-->>User: [FAILED] Match not found
    else Tìm thấy trận đấu
        MainController->>TicketController: generateTickets(matchId)
        TicketController->>MatchRepository: findById(matchId)
        MatchRepository-->>TicketController: Match
        alt Match tồn tại và chưa CANCELLED
            TicketController->>TicketRepository: findByMatchId(matchId)
            TicketRepository-->>TicketController: existing tickets
            TicketController->>SectionRepository: findByStadiumId(stadiumId)
            SectionRepository-->>TicketController: List of SeatSection
            loop Mỗi SeatSection
                TicketController->>TicketPricingRepository: findByMatchAndSection(matchId, sectionId)
                TicketPricingRepository-->>TicketController: TicketPricing
                alt pricing != null
                    TicketController->>SeatRepository: findBySectionId(sectionId)
                    SeatRepository-->>TicketController: List of Seat
                    loop Mỗi Seat chưa có Ticket
                        TicketController->>TicketRepository: generateNextTicketId()
                        TicketRepository-->>TicketController: nextTicketId
                    end
                end
            end
            alt newTickets không rỗng
                TicketController->>TicketRepository: saveAll(newTickets)
            else có Ticket AVAILABLE đổi giá
                TicketController->>TicketRepository: saveToFile()
            end
        end
        TicketController-->>MainController: int (số vé mới)

        MainController->>StadiumController: getSections(stadiumId)
        StadiumController-->>MainController: List of SeatSection
        MainController->>StadiumView: displaySectionList(stadiumId, sections)
        MainController-->>User: Yêu cầu nhập section ID
        User->>MainController: sectionId

        MainController->>TicketController: getAvailableSeatsForMatch(matchId, sectionId)
        TicketController->>MatchRepository: findById(matchId)
        TicketController->>SectionRepository: findById(sectionId)
        TicketController->>TicketRepository: findByMatchId(matchId)
        TicketController->>SeatRepository: findAvailableBySection(sectionId)
        SeatRepository-->>TicketController: List of Seat
        TicketController-->>MainController: availableSeats

        alt availableSeats rỗng
            MainController-->>User: [FAILED] No available tickets
        else availableSeats không rỗng
            MainController->>BookingView: displayBookingForm(availableSeats, fanId, matchId)
            BookingView-->>User: Hiển thị bảng ghế, yêu cầu nhập seat IDs (tối đa 4)
            User->>BookingView: seatInput (seat IDs cách nhau bởi dấu phẩy)
            BookingView->>BookingView: Kiểm tra seatIds (không rỗng, tối đa 4, không trùng)

            alt seatIds không hợp lệ
                BookingView-->>User: showBookingResult(false, message)
            else seatIds hợp lệ
                BookingView->>BookingController: bookTickets(fanId, matchId, seatIds, NO_LOCK)

                alt mech == OPTIMISTIC
                    loop Mỗi seatId (thử lại tối đa 5 lần)
                        BookingController->>SeatRepository: findById(seatId)
                        SeatRepository-->>BookingController: Seat
                        BookingController->>SeatRepository: updateWithOptimisticLock(booked, expectedVersion)
                        SeatRepository-->>BookingController: claimed (true/false)
                        alt Seat null, không AVAILABLE hoặc claim thất bại hết 5 lần
                            BookingController-->>BookingView: return false
                        end
                        BookingController->>TicketRepository: findBySeatAndMatch(seatId, matchId)
                        TicketRepository-->>BookingController: Ticket
                        opt Ticket == null hoặc CANCELLED
                            opt Ticket == null
                                BookingController->>SeatRepository: findById(seatId)
                                BookingController->>TicketPricingRepository: findByMatchAndSection(matchId, sectionId)
                            end
                            BookingController->>TicketRepository: generateNextTicketId()
                            BookingController->>TicketRepository: save(newTicket)
                        end
                        alt Ticket tồn tại nhưng không AVAILABLE và không CANCELLED
                            BookingController-->>BookingView: return false
                        end
                    end
                    Note over BookingController,TransactionRepository: synchronized(transactionRepository)
                    BookingController->>TransactionRepository: generateNextTransactionId()
                    BookingController->>BookingController: Ticket set fanId, transactionId, bookingDate, BOOKED
                    BookingController->>TicketRepository: saveToFile()
                    BookingController->>TransactionRepository: save(BookingTransaction SUCCESS)
                    opt Thất bại giữa chừng (finally, completed == false)
                        BookingController->>SeatRepository: updateWithOptimisticLock(released, bookedVersion)
                    end
                else mech là SYNCHRONIZED, FILE_LOCK hoặc NO_LOCK
                    opt mech == SYNCHRONIZED
                        Note over BookingController,SeatRepository: synchronized(seatRepository)
                    end
                    opt mech == FILE_LOCK
                        BookingController->>SeatRepository: acquireFileLock()
                    end
                    loop Mỗi seatId
                        BookingController->>SeatRepository: findById(seatId)
                        SeatRepository-->>BookingController: Seat
                        alt Seat == null hoặc không AVAILABLE
                            BookingController-->>BookingView: return false
                        end
                        BookingController->>TicketRepository: findBySeatAndMatch(seatId, matchId)
                        TicketRepository-->>BookingController: Ticket
                        opt Ticket == null hoặc CANCELLED
                            opt Ticket == null
                                BookingController->>TicketPricingRepository: findByMatchAndSection(matchId, sectionId)
                            end
                            BookingController->>TicketRepository: generateNextTicketId()
                            BookingController->>TicketRepository: save(newTicket)
                        end
                        alt Ticket tồn tại nhưng không AVAILABLE và không CANCELLED
                            BookingController-->>BookingView: return false
                        end
                    end
                    BookingController->>TransactionRepository: generateNextTransactionId()
                    BookingController->>BookingController: Seat set BOOKED + incrementVersion, Ticket set BOOKED
                    BookingController->>SeatRepository: saveToFile()
                    BookingController->>TicketRepository: saveToFile()
                    BookingController->>TransactionRepository: save(BookingTransaction SUCCESS)
                    opt mech == FILE_LOCK
                        BookingController->>SeatRepository: releaseFileLock()
                    end
                end

                BookingController-->>BookingView: boolean success

                alt success == false
                    BookingView-->>User: showBookingResult(false, Booking failed)
                else success == true
                    BookingView->>BookingController: viewBookingHistory(fanId)
                    BookingController->>TransactionRepository: findByFanId(fanId)
                    TransactionRepository-->>BookingController: List of BookingTransaction
                    BookingController-->>BookingView: history
                    BookingView->>BookingController: getTicketDetails(latest.getId())
                    BookingController->>TicketRepository: findAll()
                    TicketRepository-->>BookingController: List of Ticket
                    BookingController-->>BookingView: tickets
                    BookingView-->>User: showBookingResult(true, Booking completed successfully)
                    BookingView->>BookingView: displayBookingHistory(history)
                    loop Mỗi BookingTransaction
                        BookingView->>BookingController: getTicketDetails(txn.getId())
                        BookingController->>TicketRepository: findAll()
                    end
                    BookingView-->>User: Hiển thị BOOKING TRANSACTIONS và MY TICKETS
                end
            end
        end
    end

    %% ===== 4. HUY VE (FAN) =====
    Note over User,AdminController: 4. Hủy vé (Fan Menu chọn 7)
    MainController->>MainView: displayFanMenu()
    User->>MainView: Nhập lựa chọn "7"
    MainView->>MainController: handleFanChoice("7")
    MainController->>BookingView: displayCancelTicketForm()
    BookingView-->>User: Yêu cầu nhập Fan ID và Ticket ID
    User->>BookingView: fanId, ticketId
    BookingView->>BookingController: cancelTicket(ticketId, fanId)
    BookingController->>TicketRepository: findById(ticketId)
    TicketRepository-->>BookingController: Ticket
    alt Ticket null, sai fanId hoặc status không phải BOOKED
        BookingController-->>BookingView: false
        BookingView-->>User: showBookingResult(false, Could not cancel ticket)
    else Ticket hợp lệ
        BookingController->>BookingController: ticket.setStatus(CANCELLED)
        BookingController->>TicketRepository: saveToFile()
        BookingController->>TransactionRepository: findById(originalTransactionId)
        TransactionRepository-->>BookingController: BookingTransaction
        opt Transaction tồn tại
            BookingController->>BookingController: Cập nhật ticketCount, totalAmount và status
            BookingController->>TransactionRepository: saveToFile()
        end
        BookingController->>SeatRepository: findById(ticket.getSeatId())
        SeatRepository-->>BookingController: Seat
        opt Seat tồn tại
            BookingController->>BookingController: seat.setStatus(AVAILABLE) và incrementVersion
            BookingController->>SeatRepository: saveToFile()
        end
        BookingController-->>BookingView: true
        BookingView-->>User: showBookingResult(true, Ticket has been cancelled)
    end

    %% ===== 5. DANG XUAT (FAN) =====
    Note over User,AdminController: 5. Đăng xuất (Fan Menu chọn 0)
    User->>MainView: Nhập lựa chọn "0"
    MainView->>MainController: handleFanChoice("0")
    MainController->>AuthController: logout(currentUser.getId())
    MainController->>MainController: currentUser = null
    MainController-->>User: [INFO] Logged out successfully

    %% ===== 6. ORGANIZER: XEM DANH SACH TRAN =====
    Note over User,AdminController: 6. Organizer — Xem danh sách trận (Org Menu chọn 1)
    MainController->>MainView: displayOrganizerMenu()
    User->>MainView: Nhập lựa chọn "1"
    MainView->>MainController: handleOrganizerChoice("1")
    MainController->>OrganizerView: setCurrentOrgId(currentUser.getId())
    MainController->>MatchView: setCurrentOrgId(currentUser.getId())
    MainController->>MatchController: viewAllMatches()
    MatchController->>MatchRepository: findAll()
    MatchRepository-->>MatchController: List of Match
    MatchController-->>MainController: List of Match
    MainController->>MatchView: displayMatches(List of Match)
    MatchView-->>User: Hiển thị danh sách trận đấu

    %% ===== 7. ORGANIZER: TAO TRAN DAU =====
    Note over User,AdminController: 7. Organizer — Tạo trận đấu (Org Menu chọn 2)
    User->>MainView: Nhập lựa chọn "2"
    MainView->>MainController: handleOrganizerChoice("2")
    MainController->>MatchView: displayCreateMatchForm()
    MatchView-->>User: Yêu cầu nhập title, teamA, teamB, stadiumId, startTime, endTime, orgId
    User->>MatchView: title, teamA, teamB, stadiumId, startTime, endTime, orgId
    alt orgId không khớp currentOrgId
        MatchView-->>User: [FAILED] You can only create matches for your own organization
    else orgId hợp lệ
        MatchView->>MatchController: createMatch(title, teamA, teamB, stadiumId, start, end, orgId)
        MatchController->>MatchRepository: findAll()
        MatchRepository-->>MatchController: List of Match (để tính nextId)
        MatchController->>MatchRepository: findById(matchId)
        MatchRepository-->>MatchController: null (kiểm tra ID chưa tồn tại)
        MatchController->>MatchRepository: save(newMatch - status SCHEDULED)
        MatchRepository-->>MatchController: boolean
        MatchController-->>MatchView: boolean
        alt success
            MatchView-->>User: [SUCCESS] New match created successfully!
        else fail
            MatchView-->>User: [FAILED] Failed to create match
        end
    end

    %% ===== 8. ORGANIZER: CAP NHAT TRAN DAU =====
    Note over User,AdminController: 8. Organizer — Cập nhật trận đấu (Org Menu chọn 3)
    User->>MainView: Nhập lựa chọn "3"
    MainView->>MainController: handleOrganizerChoice("3")
    MainController->>MatchView: displayUpdateMatchForm()
    MatchView-->>User: Yêu cầu nhập matchId
    User->>MatchView: matchId
    MatchView->>MatchController: viewAllMatches()
    MatchController->>MatchRepository: findAll()
    MatchRepository-->>MatchController: List of Match
    MatchController-->>MatchView: List of Match
    alt targetMatch == null
        MatchView-->>User: [FAILED] Match not found
    else targetMatch tồn tại
        alt currentOrgId không khớp targetMatch.getOrganizerId()
            MatchView-->>User: [FAILED] You do not have permission to update this match
        else có quyền
            MatchView-->>User: Yêu cầu nhập thông tin cập nhật (title, teamA, teamB, start, end, status)
            User->>MatchView: thông tin mới
            MatchView->>MatchController: updateMatch(matchId, title, teamA, teamB, start, end, status)
            MatchController->>MatchRepository: findById(matchId)
            MatchRepository-->>MatchController: Match
            MatchController->>MatchController: match.updateMatchDetails(title, teamA, teamB, start, end)
            MatchController->>MatchController: match.setStatus(status)
            MatchController->>MatchRepository: saveToFile()
            MatchController-->>MatchView: true
            MatchView-->>User: [SUCCESS] Match updated successfully
        end
    end

    %% ===== 9. ORGANIZER: XOA TRAN DAU =====
    Note over User,AdminController: 9. Organizer — Xóa trận đấu (Org Menu chọn 4)
    User->>MainView: Nhập lựa chọn "4"
    MainView->>MainController: handleOrganizerChoice("4")
    MainController-->>User: Yêu cầu nhập matchId
    User->>MainController: matchId
    MainController->>MatchController: viewAllMatches()
    MatchController->>MatchRepository: findAll()
    MatchRepository-->>MatchController: List of Match
    MatchController-->>MainController: List of Match
    alt match == null
        MainController-->>User: [FAILED] Match not found
    else match tồn tại
        alt currentUser.getId() không khớp match.getOrganizerId()
            MainController-->>User: [FAILED] You do not have permission to delete this match
        else có quyền
            MainController->>TicketController: cancelTickets(matchId)
            TicketController->>TicketRepository: findByMatchId(matchId)
            TicketRepository-->>TicketController: List of Ticket
            loop Mỗi Ticket chưa CANCELLED
                alt Ticket là BOOKED
                    TicketController->>SeatRepository: findById(ticket.getSeatId())
                    SeatRepository-->>TicketController: Seat
                    TicketController->>TicketController: seat.setStatus(AVAILABLE) + incrementVersion()
                end
                TicketController->>TicketController: ticket.setStatus(CANCELLED)
            end
            loop Mỗi transactionId liên quan
                TicketController->>TransactionRepository: findById(transactionId)
                TransactionRepository-->>TicketController: BookingTransaction
                TicketController->>TicketController: txn.setStatus(CANCELLED)
            end
            TicketController->>TicketRepository: getItems().removeIf(AVAILABLE tickets)
            TicketController->>TicketRepository: saveToFile()
            TicketController->>SeatRepository: saveToFile()
            TicketController->>TransactionRepository: saveToFile()
            TicketController-->>MainController: boolean
            MainController->>MatchController: deleteMatch(matchId)
            MatchController->>MatchRepository: deleteById(matchId)
            MatchRepository-->>MatchController: boolean
            MatchController-->>MainController: boolean
            MainController-->>User: [SUCCESS] Match deleted successfully
        end
    end

    %% ===== 10. ORGANIZER: CAU HINH GIA VE =====
    Note over User,AdminController: 10. Organizer — Cấu hình giá vé (Org Menu chọn 5)
    User->>MainView: Nhập lựa chọn "5"
    MainView->>MainController: handleOrganizerChoice("5")
    MainController->>OrganizerView: displayPricingConfigForm()
    OrganizerView-->>User: Yêu cầu nhập matchId, sectionId, price
    User->>OrganizerView: matchId, sectionId, price
    OrganizerView->>TicketController: getMatchRepository()
    TicketController-->>OrganizerView: MatchRepository
    OrganizerView->>MatchRepository: findById(matchId)
    MatchRepository-->>OrganizerView: Match
    alt match == null hoặc match.getOrganizerId() không khớp currentOrgId
        OrganizerView-->>User: [FAILED] You do not have permission
    else có quyền
        alt price <= 0 hoặc NaN hoặc Infinite
            OrganizerView-->>User: [FAILED] Invalid price
        else price hợp lệ
            OrganizerView->>TicketController: configureTicketPricing(matchId, sectionId, price)
            TicketController->>MatchRepository: findById(matchId)
            TicketController->>SectionRepository: findById(sectionId)
            TicketController->>TicketPricingRepository: findByMatchAndSection(matchId, sectionId)
            TicketPricingRepository-->>TicketController: TicketPricing hoặc null
            alt pricing đã tồn tại
                TicketController->>TicketController: pricing.setPrice(price)
                TicketController->>TicketPricingRepository: saveToFile()
            else pricing chưa có
                TicketController->>TicketPricingRepository: save(new TicketPricing)
            end
            TicketController->>SeatRepository: findBySectionId(sectionId)
            SeatRepository-->>TicketController: List of Seat
            loop Mỗi Seat — cập nhật giá vé AVAILABLE
                TicketController->>TicketRepository: findBySeatAndMatch(seatId, matchId)
                TicketRepository-->>TicketController: Ticket
                opt Ticket != null && status == AVAILABLE
                    TicketController->>TicketController: ticket.setPrice(price)
                end
            end
            TicketController->>TicketRepository: saveToFile()
            TicketController-->>OrganizerView: true
            OrganizerView-->>User: [SUCCESS] Price set + [INFO] Price applied to AVAILABLE tickets
        end
    end

    %% ===== 11. ORGANIZER: SINH VE TU DONG =====
    Note over User,AdminController: 11. Organizer — Sinh vé tự động (Org Menu chọn 6)
    User->>MainView: Nhập lựa chọn "6"
    MainView->>MainController: handleOrganizerChoice("6")
    MainController->>OrganizerView: displayGenerateTicketsForm()
    OrganizerView-->>User: Yêu cầu nhập matchId
    User->>OrganizerView: matchId
    OrganizerView->>TicketController: getMatchRepository()
    TicketController-->>OrganizerView: MatchRepository
    OrganizerView->>MatchRepository: findById(matchId)
    MatchRepository-->>OrganizerView: Match
    alt Không có quyền sở hữu
        OrganizerView-->>User: [FAILED] No permission
    else Có quyền
        OrganizerView->>TicketController: viewTicketSalesReport(matchId)
        TicketController->>MatchRepository: findById(matchId)
        TicketController->>TicketRepository: findByMatchId(matchId)
        TicketRepository-->>TicketController: List of Ticket
        TicketController-->>OrganizerView: Map (Total tickets, Sold, Available...)
        OrganizerView->>TicketController: generateTickets(matchId)
        TicketController->>MatchRepository: findById(matchId)
        alt match == null hoặc CANCELLED
            TicketController-->>OrganizerView: 0
        else match hợp lệ
            TicketController->>TicketRepository: findByMatchId(matchId)
            TicketController->>SectionRepository: findByStadiumId(stadiumId)
            loop Mỗi Section
                TicketController->>TicketPricingRepository: findByMatchAndSection(matchId, sectionId)
                TicketController->>SeatRepository: findBySectionId(sectionId)
                loop Mỗi Seat
                    alt Ticket đã BOOKED hoặc CANCELLED
                        TicketController->>TicketController: skip (không sinh thêm)
                    else Chưa có Ticket
                        TicketController->>TicketRepository: generateNextTicketId()
                        TicketController->>TicketController: new Ticket(AVAILABLE)
                    end
                end
            end
            alt newTickets không rỗng
                TicketController->>TicketRepository: saveAll(newTickets)
            else updatedExisting
                TicketController->>TicketRepository: saveToFile()
            end
            TicketController-->>OrganizerView: int (số vé mới)
        end
        alt count > 0
            OrganizerView-->>User: [SUCCESS] Generated X new tickets
        else totalTickets > 0
            OrganizerView-->>User: [INFO] All tickets already generated (Y tickets exist)
        else
            OrganizerView-->>User: [FAILED] No tickets generated - Ensure pricing is configured
        end
    end

    %% ===== 12. ORGANIZER: HUY TAT CA VE =====
    Note over User,AdminController: 12. Organizer — Hủy tất cả vé (Org Menu chọn 7)
    User->>MainView: Nhập lựa chọn "7"
    MainView->>MainController: handleOrganizerChoice("7")
    MainController->>OrganizerView: displayCancelTicketsForm()
    OrganizerView-->>User: Yêu cầu nhập matchId
    User->>OrganizerView: matchId
    OrganizerView->>TicketController: getMatchRepository()
    TicketController-->>OrganizerView: MatchRepository
    OrganizerView->>MatchRepository: findById(matchId)
    MatchRepository-->>OrganizerView: Match
    alt Không có quyền sở hữu
        OrganizerView-->>User: [FAILED] No permission
    else Có quyền
        OrganizerView->>TicketController: cancelTickets(matchId)
        TicketController->>TicketRepository: findByMatchId(matchId)
        loop Mỗi Ticket chưa CANCELLED
            alt Ticket là BOOKED
                TicketController->>SeatRepository: findById(seatId)
                TicketController->>TicketController: seat.setStatus(AVAILABLE) + incrementVersion
            end
            TicketController->>TicketController: ticket.setStatus(CANCELLED)
        end
        loop Mỗi transactionId
            TicketController->>TransactionRepository: findById(transactionId)
            TicketController->>TicketController: txn.setStatus(CANCELLED)
        end
        TicketController->>TicketRepository: saveToFile()
        TicketController->>SeatRepository: saveToFile()
        TicketController->>TransactionRepository: saveToFile()
        TicketController-->>OrganizerView: boolean
        alt success
            OrganizerView-->>User: [SUCCESS] Cancelled all tickets for match
        else
            OrganizerView-->>User: [FAILED]
        end
    end

    %% ===== 13. ORGANIZER: XEM BAO CAO DOANH THU =====
    Note over User,AdminController: 13. Organizer — Xem báo cáo doanh thu (Org Menu chọn 8)
    User->>MainView: Nhập lựa chọn "8"
    MainView->>MainController: handleOrganizerChoice("8")
    MainController-->>User: Yêu cầu nhập matchId
    User->>MainController: matchId
    MainController->>MatchController: viewAllMatches()
    MatchController->>MatchRepository: findAll()
    MatchRepository-->>MatchController: List of Match
    MatchController-->>MainController: List of Match
    alt match == null
        MainController-->>User: [FAILED] Match ID does not exist
    else match tồn tại
        alt currentUser.getId() không khớp match.getOrganizerId()
            MainController-->>User: [FAILED] No permission to view report
        else có quyền
            MainController->>TicketController: viewTicketSalesReport(matchId)
            TicketController->>MatchRepository: findById(matchId)
            TicketController->>TicketRepository: findByMatchId(matchId)
            TicketRepository-->>TicketController: List of Ticket
            TicketController-->>MainController: Map (Total tickets, Sold, Available, Revenue...)
            MainController->>OrganizerView: displaySalesReport(Map sales)
            OrganizerView-->>User: Hiển thị bảng TICKET SALES REPORT
        end
    end

    %% ===== 14. ADMIN: QUAN LY TAI KHOAN FAN =====
    Note over User,AdminController: 14. Admin — Duyệt/Từ chối tài khoản Fan (Admin Menu chọn 1)
    MainController->>MainView: displayAdminMenu()
    User->>MainView: Nhập lựa chọn "1"
    MainView->>MainController: handleAdminChoice("1")
    MainController->>AdminView: displayFanManagementMenu()
    AdminView-->>User: Hiển thị sub-menu quản lý Fan
    User->>AdminView: Nhập lựa chọn "1" (Approve/Reject)
    AdminView->>AdminController: getPendingFans()
    AdminController->>FanRepository: findByStatus(PENDING)
    FanRepository-->>AdminController: List of Fan
    AdminController-->>AdminView: List of Fan
    AdminView-->>User: Hiển thị danh sách Fan đang chờ duyệt
    User->>AdminView: fanId, decision ("A" hoặc "R")
    alt decision == "A"
        AdminView->>AdminController: approveFanAccount(fanId)
        AdminController->>FanRepository: findById(fanId)
        FanRepository-->>AdminController: Fan
        AdminController->>AdminController: fan.setStatus(ACTIVE)
        AdminController->>FanRepository: save(fan)
        AdminController->>FanRepository: saveToFile()
        AdminController-->>AdminView: boolean
        AdminView-->>User: [SUCCESS] Fan account approved
    else decision == "R"
        AdminView->>AdminController: rejectFanAccount(fanId)
        AdminController->>FanRepository: findById(fanId)
        FanRepository-->>AdminController: Fan
        AdminController->>AdminController: fan.setStatus(REJECTED)
        AdminController->>FanRepository: save(fan)
        AdminController->>FanRepository: saveToFile()
        AdminController-->>AdminView: boolean
        AdminView-->>User: [SUCCESS] Fan account rejected
    end

    %% ===== 15. ADMIN: QUAN LY TAI KHOAN ORGANIZER =====
    Note over User,AdminController: 15. Admin — Duyệt/Từ chối tài khoản Organizer (Admin Menu chọn 2)
    User->>MainView: Nhập lựa chọn "2"
    MainView->>MainController: handleAdminChoice("2")
    MainController->>AdminView: displayOrganizerManagementMenu()
    User->>AdminView: Nhập lựa chọn "1" (Approve/Reject)
    AdminView->>AdminController: getPendingOrganizers()
    AdminController->>OrganizerRepository: findPendingOrganizers()
    OrganizerRepository-->>AdminController: List of Organizer
    AdminController-->>AdminView: List of Organizer
    AdminView-->>User: Hiển thị danh sách Organizer đang chờ duyệt
    User->>AdminView: orgId, decision ("A" hoặc "R")
    alt decision == "A"
        AdminView->>AdminController: approveOrganizerAccount(orgId)
        AdminController->>OrganizerRepository: findById(orgId)
        OrganizerRepository-->>AdminController: Organizer
        AdminController->>AdminController: org.setStatus(ACTIVE)
        AdminController->>OrganizerRepository: save(org)
        AdminController->>OrganizerRepository: saveToFile()
        AdminView-->>User: [SUCCESS] Organizer account approved
    else decision == "R"
        AdminView->>AdminController: rejectOrganizerAccount(orgId)
        AdminController->>OrganizerRepository: findById(orgId)
        OrganizerRepository-->>AdminController: Organizer
        AdminController->>AdminController: org.setStatus(REJECTED)
        AdminController->>OrganizerRepository: save(org)
        AdminController->>OrganizerRepository: saveToFile()
        AdminView-->>User: [SUCCESS] Organizer account rejected
    end

    %% ===== 16. ADMIN: QUAN LY GHE NGOI =====
    Note over User,AdminController: 16. Admin — Tạo ghế ngồi (Admin Menu chọn 3)
    User->>MainView: Nhập lựa chọn "3"
    MainView->>MainController: handleAdminChoice("3")
    MainController->>AdminView: displaySeatManagementMenu()
    User->>AdminView: Nhập lựa chọn "1" (Create Seat)
    AdminView-->>User: Yêu cầu nhập sectionId, rowNumber, seatNum, seatType
    User->>AdminView: sectionId, rowNumber, seatNum, seatType
    AdminView->>AdminController: createSeat(sectionId, rowNumber, seatNum, seatType)
    AdminController->>SeatRepository: findById(seatId)
    SeatRepository-->>AdminController: null (kiểm tra không trùng)
    AdminController->>AdminController: new Seat(seatId, ..., SeatStatus.AVAILABLE, 0)
    AdminController->>SeatRepository: save(seat)
    AdminController->>SeatRepository: saveToFile()
    AdminController-->>AdminView: boolean
    AdminView-->>User: [SUCCESS] Seat created successfully

    %% ===== 17. GUEST: DANG KY FAN =====
    Note over User,AdminController: 17. Guest — Đăng ký tài khoản Fan (Guest Menu chọn 8)
    User->>MainView: Nhập lựa chọn "8"
    MainView->>MainController: handleGuestChoice("8")
    MainController-->>User: Yêu cầu nhập username, password, fullName, email, phone
    User->>MainController: username, password, fullName, email, phone
    MainController->>AuthController: registerFan(username, pass, fullName, email, phone)
    AuthController->>FanRepository: findByUsername(username)
    AuthController->>OrganizerRepository: findByUsername(username)
    AuthController->>AdminRepository: findByUsername(username)
    alt Username đã tồn tại
        AuthController-->>MainController: false
        MainController-->>User: [FAILED] Username already exists
    else Username chưa tồn tại
        AuthController->>FanRepository: findAll()
        FanRepository-->>AuthController: List of Fan (tính nextFanId)
        AuthController->>AuthController: new Fan(newFanId, ..., status=ACTIVE)
        AuthController->>FanRepository: save(newFan)
        AuthController-->>MainController: true
        MainController-->>User: [SUCCESS] Fan registered successfully! You can login now.
    end

    %% ===== 18. GUEST: DANG KY ORGANIZER =====
    Note over User,AdminController: 18. Guest — Đăng ký tài khoản Organizer (Guest Menu chọn 9)
    User->>MainView: Nhập lựa chọn "9"
    MainView->>MainController: handleGuestChoice("9")
    MainController-->>User: Yêu cầu nhập username, password, orgName, email, phone
    User->>MainController: username, password, orgName, email, phone
    MainController->>AuthController: registerOrganizer(username, pass, orgName, email, phone)
    AuthController->>FanRepository: findByUsername(username)
    AuthController->>OrganizerRepository: findByUsername(username)
    AuthController->>AdminRepository: findByUsername(username)
    alt Username đã tồn tại
        AuthController-->>MainController: false
        MainController-->>User: [FAILED] Username already exists
    else Username chưa tồn tại
        AuthController->>OrganizerRepository: findAll()
        OrganizerRepository-->>AuthController: List of Organizer (tính nextOrgId)
        AuthController->>AuthController: new Organizer(newOrgId, ..., status=PENDING)
        AuthController->>OrganizerRepository: save(newOrg)
        AuthController-->>MainController: true
        MainController-->>User: [SUCCESS] Organizer registered! Status PENDING — awaiting Admin approval.
    end
```

