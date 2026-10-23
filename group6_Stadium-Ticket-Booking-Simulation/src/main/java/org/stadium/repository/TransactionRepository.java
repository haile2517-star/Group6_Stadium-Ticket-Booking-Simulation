package org.stadium.repository;

import org.stadium.model.BookingTransaction;
import org.stadium.model.enums.TransactionStatus;
import java.util.List;

/**
 * Repository quản lý giao dịch (BookingTransaction) với cơ chế thread-safe.
 * <p>
 * Tuần 7 - Kiệt: Đảm bảo an toàn dữ liệu đa luồng cho TransactionRepository:
 * <ul>
 *   <li>Sinh transactionId (TXN00001, TXN00002, ...) không bị trùng khi nhiều thread cùng gọi</li>
 *   <li>Ghi file transactions.csv không bị hỏng dòng (corrupted lines) khi ghi dồn dập</li>
 *   <li>Toàn bộ thao tác save / generateNextId được bảo vệ bởi monitor instance (synchronized(this))</li>
 * </ul>
 * </p>
 *
 * @author Kiệt — Account & Data Safety
 */
public class TransactionRepository extends CsvRepository<BookingTransaction> {

    /**
     * Bộ đếm nội bộ theo dõi số thứ tự transactionId lớn nhất đã được cấp phát.
     * Được khởi tạo một lần khi load file và tăng dần atomic qua synchronized.
     */
    private int currentMaxIdNumber;

    public TransactionRepository(String filePath) {
        super(filePath);
        this.currentMaxIdNumber = this.scanMaxIdNumber();
    }

    @Override
    public BookingTransaction createEntity() {
        return new BookingTransaction();
    }

    @Override
    public String getHeader() {
        return "transactionId,fanId,matchId,totalAmount,ticketCount,transactionDate,status";
    }

    /**
     * Quét toàn bộ danh sách giao dịch hiện tại để tìm số thứ tự lớn nhất
     * trong các transactionId có dạng TXNxxxxx.
     *
     * @return số thứ tự lớn nhất tìm được, hoặc 0 nếu chưa có giao dịch nào
     */
    private int scanMaxIdNumber() {
        int maxId = 0;
        for (BookingTransaction txn : this.findAll()) {
            String id = txn.getId();
            if (id != null && id.toUpperCase().startsWith("TXN")) {
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
     * Sinh mã giao dịch kế tiếp, đảm bảo thread-safe bằng synchronized trên instance monitor.
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
     * @return transactionId duy nhất có định dạng TXN00001, TXN00002, ...
     */
    public synchronized String generateNextTransactionId() {
        this.currentMaxIdNumber++;
        String candidateId = String.format("TXN%05d", this.currentMaxIdNumber);
        // Vòng lặp an toàn: đề phòng trường hợp ID đã tồn tại (dữ liệu sửa tay bên ngoài)
        while (this.findById(candidateId) != null) {
            this.currentMaxIdNumber++;
            candidateId = String.format("TXN%05d", this.currentMaxIdNumber);
        }
        return candidateId;
    }

    /**
     * Lưu entity vào danh sách và ghi file — thread-safe.
     * <p>
     * Synchronized trên instance monitor để đảm bảo:
     * <ul>
     *   <li>Không có 2 thread cùng thêm entity vào danh sách items đồng thời</li>
     *   <li>File transactions.csv không bị ghi đè lẫn nhau gây hỏng dòng</li>
     * </ul>
     * </p>
     *
     * @param entity giao dịch cần lưu
     * @return true nếu lưu thành công
     */
    @Override
    public synchronized boolean save(BookingTransaction entity) {
        return super.save(entity);
    }

    /**
     * Ghi toàn bộ danh sách giao dịch ra file — thread-safe.
     * Bảo vệ bằng synchronized để tránh nhiều thread ghi đè file đồng thời
     * gây hỏng dòng (corrupted lines) trong transactions.csv.
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

    public List<BookingTransaction> findByFanId(String fanId) {
        return this.findByCondition(t -> fanId != null && fanId.equals(t.getFanId()));
    }

    public List<BookingTransaction> findByStatus(TransactionStatus status) {
        return this.findByCondition(t -> t.getStatus() == status);
    }
}
