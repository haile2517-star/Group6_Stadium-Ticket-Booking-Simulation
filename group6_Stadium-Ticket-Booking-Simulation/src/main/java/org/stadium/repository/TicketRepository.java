package org.stadium.repository;

import org.stadium.model.Ticket;

import java.util.List;

/**
 * Repository quản lý vé (Ticket) với cơ chế thread-safe.
 * <p>
 * Tuần 7 - Kiệt: Đảm bảo an toàn dữ liệu đa luồng cho TicketRepository:
 * <ul>
 *   <li>Sinh ticketId (TCK000001, TCK000002, ...) không bị trùng khi nhiều thread cùng gọi</li>
 *   <li>Ghi file tickets.csv không bị hỏng dòng (corrupted lines) khi ghi dồn dập</li>
 *   <li>Toàn bộ thao tác save / generateNextId được bảo vệ bởi monitor instance (synchronized(this))</li>
 * </ul>
 * </p>
 *
 * @author Kiệt — Account & Data Safety
 */
public class TicketRepository extends CsvRepository<Ticket> {

    /**
     * Bộ đếm nội bộ theo dõi số thứ tự ticketId lớn nhất đã được cấp phát.
     * Được khởi tạo một lần khi load file và tăng dần atomic qua synchronized.
     */
    private int currentMaxIdNumber;

    public TicketRepository(String filePath) {
        super(filePath);
        this.currentMaxIdNumber = this.scanMaxIdNumber();
    }

    @Override
    public Ticket createEntity() {
        return new Ticket();
    }

    @Override
    public String getHeader() {
        return "ticketId,matchId,seatId,transactionId,fanId,price,bookingDate,status";
    }

    /**
     * Quét toàn bộ danh sách vé hiện tại để tìm số thứ tự lớn nhất
     * trong các ticketId có dạng TCKxxxxxx.
     *
     * @return số thứ tự lớn nhất tìm được, hoặc 0 nếu chưa có vé nào
     */
    private int scanMaxIdNumber() {
        int maxId = 0;
        for (Ticket ticket : this.findAll()) {
            String id = ticket.getId();
            if (id != null && id.toUpperCase().startsWith("TCK")) {
                try {
                    int num = Integer.parseInt(id.substring(3).trim());
                    if (num > maxId) {
                        maxId = num;
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return maxId;
    }

    /**
     * Sinh mã vé kế tiếp, đảm bảo thread-safe bằng synchronized trên instance monitor.
     * <p>
     * Cơ chế chống trùng ID:
     * <ol>
     *   <li>Tăng bộ đếm nội bộ {@code currentMaxIdNumber} lên 1</li>
     *   <li>Kiểm tra candidateId có tồn tại trong repository không (phòng trường hợp dữ liệu
     *       bị chỉnh sửa thủ công bên ngoài)</li>
     *   <li>Nếu trùng, tiếp tục tăng cho đến khi tìm được ID chưa sử dụng</li>
     * </ol>
     * </p>
     *
     * @return ticketId duy nhất có định dạng TCK000001, TCK000002, ...
     */
    public synchronized String generateNextTicketId() {
        this.currentMaxIdNumber++;
        String candidateId = String.format("TCK%06d", this.currentMaxIdNumber);
        // Vòng lặp an toàn: đề phòng trường hợp ID đã tồn tại (dữ liệu sửa tay bên ngoài)
        while (this.findById(candidateId) != null) {
            this.currentMaxIdNumber++;
            candidateId = String.format("TCK%06d", this.currentMaxIdNumber);
        }
        return candidateId;
    }

    /**
     * Lưu entity vào danh sách và ghi file — thread-safe.
     * <p>
     * Synchronized trên instance monitor để đảm bảo:
     * <ul>
     *   <li>Không có 2 thread cùng thêm entity vào danh sách items đồng thời</li>
     *   <li>File tickets.csv không bị ghi đè lẫn nhau gây hỏng dòng</li>
     * </ul>
     * </p>
     *
     * @param entity vé cần lưu
     * @return true nếu lưu thành công
     */
    @Override
    public synchronized boolean save(Ticket entity) {
        return super.save(entity);
    }

    /**
     * Ghi toàn bộ danh sách vé ra file — thread-safe.
     * Bảo vệ bằng synchronized để tránh nhiều thread ghi đè file đồng thời
     * gây hỏng dòng (corrupted lines) trong tickets.csv.
     */
    @Override
    public synchronized void saveToFile() {
        super.saveToFile();
    }

    /**
     * Nạp lại dữ liệu từ file và cập nhật lại bộ đếm ID — thread-safe.
     * Đảm bảo sau khi reload, bộ đếm phản ánh đúng trạng thái mới nhất của file.
     */
    @Override
    public synchronized void loadFromFile() {
        super.loadFromFile();
        this.currentMaxIdNumber = this.scanMaxIdNumber();
    }

    public List<Ticket> findByMatchId(String matchId) {
        return this.findByCondition(t -> t.getMatchId().equals(matchId));
    }

    public List<Ticket> findByFanId(String fanId) {
        return this.findByCondition(t -> t.getFanId().equals(fanId));
    }

    public Ticket findBySeatAndMatch(String seatId, String matchId) {
        return this.findByCondition(t -> t.getSeatId().equals(seatId) && t.getMatchId().equals(matchId))
                .stream()
                .findFirst()
                .orElse(null);
    }
}