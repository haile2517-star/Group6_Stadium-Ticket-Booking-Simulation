package org.stadium.repository;

import org.stadium.model.BookingTransaction;
import org.stadium.model.enums.TransactionStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Repository quản lý giao dịch (BookingTransaction) với cơ chế thread-safe toàn diện.
 * <p>
 * Tuần 7 - Kiệt (Account & Data Safety):
 * <ul>
 *   <li>Rà soát quá trình sinh mã transactionId khi nhiều luồng cùng gọi đồng thời, tránh trùng mã ID.</li>
 *   <li>Đảm bảo file transactions.csv không bị hỏng dòng (corrupted lines) khi ghi dồn dập qua cơ chế Atomic File Write.</li>
 *   <li>Toàn bộ phương thức đọc/ghi và sinh ID đều được đồng bộ hóa qua instance monitor (synchronized(this)).</li>
 * </ul>
 * </p>
 *
 * @author Kiệt — Account & Data Safety
 */
public class TransactionRepository extends CsvRepository<BookingTransaction> {

    /**
     * Bộ đếm nội bộ theo dõi số thứ tự transactionId lớn nhất đã được cấp phát.
     * Được khởi tạo một lần khi nạp dữ liệu và tăng dần atomic trong khối synchronized.
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
    private synchronized int scanMaxIdNumber() {
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
     * Sinh mã giao dịch kế tiếp, đảm bảo thread-safe tuyệt đối bằng synchronized trên instance monitor.
     * <p>
     * Cơ chế chống trùng ID:
     * <ol>
     *   <li>Tăng bộ đếm nội bộ {@code currentMaxIdNumber} lên 1 một cách nguyên tử.</li>
     *   <li>Tạo mã dự kiến định dạng TXN%05d.</li>
     *   <li>Kiểm tra candidateId có bị trùng trong repository không (phòng dữ liệu bị sửa tay bên ngoài).</li>
     *   <li>Nếu trùng, tiếp tục tịnh tiến cho đến khi tìm được ID duy nhất chưa từng dùng.</li>
     * </ol>
     * </p>
     *
     * @return transactionId duy nhất có định dạng TXN00001, TXN00002, ...
     */
    public synchronized String generateNextTransactionId() {
        this.currentMaxIdNumber++;
        String candidateId = String.format("TXN%05d", this.currentMaxIdNumber);
        while (this.findById(candidateId) != null) {
            this.currentMaxIdNumber++;
            candidateId = String.format("TXN%05d", this.currentMaxIdNumber);
        }
        return candidateId;
    }

    /**
     * Lưu entity vào danh sách và ghi file — thread-safe.
     *
     * @param entity giao dịch cần lưu
     * @return true nếu lưu thành công
     */
    @Override
    public synchronized boolean save(BookingTransaction entity) {
        if (entity == null) {
            return false;
        }
        this.getItems().add(entity);
        this.saveToFile();
        return true;
    }

    /**
     * Ghi toàn bộ danh sách giao dịch ra file — thread-safe và chống hỏng file (Atomic File Replacement).
     * <p>
     * Cơ chế chống hỏng dòng khi ghi dồn dập đa luồng:
     * <ol>
     *   <li>Toàn bộ phương thức được khóa bằng {@code synchronized(this)}.</li>
     *   <li>Ghi dữ liệu tạm vào file {@code [filePath].tmp}.</li>
     *   <li>Flush và đóng stream hoàn tất.</li>
     *   <li>Đổi tên file tạm ghi đè file chính qua {@code StandardCopyOption.REPLACE_EXISTING}.</li>
     * </ol>
     * Nhờ đó, file CSV không bao giờ rơi vào trạng thái rỗng hoặc bị cắt ngang dòng do lỗi I/O.
     * </p>
     */
    @Override
    public synchronized void saveToFile() {
        String targetPathStr = this.getFilePath();
        if (targetPathStr == null || targetPathStr.trim().isEmpty()) {
            return;
        }

        File targetFile = new File(targetPathStr);
        File parentDir = targetFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        String tempPathStr = targetPathStr + ".tmp";
        File tempFile = new File(tempPathStr);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempFile))) {
            bw.write(this.getHeader());
            bw.newLine();
            for (BookingTransaction item : this.getItems()) {
                if (item != null) {
                    bw.write(item.toCsvLine());
                    bw.newLine();
                }
            }
            bw.flush();
        } catch (IOException e) {
            System.err.println("[TransactionRepository] Loi ghi file tam: " + tempPathStr + " - " + e.getMessage());
            return;
        }

        // Đổi tên file tạm thành file chính thức an toàn
        Path sourcePath = Paths.get(tempPathStr);
        Path destPath = Paths.get(targetPathStr);
        try {
            try {
                Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicEx) {
                // Nếu filesystem không hỗ trợ ATOMIC_MOVE, chuyển sang copy/move thường
                Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("[TransactionRepository] Loi thay the file chinh: " + targetPathStr + " - " + e.getMessage());
        }
    }

    /**
     * Nạp lại dữ liệu từ file và cập nhật lại bộ đếm ID — thread-safe.
     */
    @Override
    public synchronized void loadFromFile() {
        super.loadFromFile();
        this.currentMaxIdNumber = this.scanMaxIdNumber();
    }

    @Override
    public synchronized BookingTransaction findById(String id) {
        if (id == null) {
            return null;
        }
        for (BookingTransaction item : this.getItems()) {
            if (id.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    @Override
    public synchronized List<BookingTransaction> findAll() {
        return new ArrayList<>(this.getItems());
    }

    @Override
    public synchronized List<BookingTransaction> findByCondition(Predicate<BookingTransaction> predicate) {
        List<BookingTransaction> result = new ArrayList<>();
        if (predicate == null) {
            return result;
        }
        for (BookingTransaction item : this.getItems()) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public synchronized boolean deleteById(String id) {
        if (id == null) {
            return false;
        }
        boolean removed = this.getItems().removeIf(e -> id.equals(e.getId()));
        if (removed) {
            this.saveToFile();
        }
        return removed;
    }

    public synchronized List<BookingTransaction> findByFanId(String fanId) {
        return this.findByCondition(t -> fanId != null && fanId.equals(t.getFanId()));
    }

    public synchronized List<BookingTransaction> findByStatus(TransactionStatus status) {
        return this.findByCondition(t -> t.getStatus() == status);
    }
}
