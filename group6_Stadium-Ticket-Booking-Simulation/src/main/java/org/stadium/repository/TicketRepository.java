package org.stadium.repository;

import org.stadium.model.Ticket;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Repository quản lý vé (Ticket) với cơ chế thread-safe toàn diện.
 * <p>
 * Tuần 7 - Kiệt (Account & Data Safety):
 * <ul>
 *   <li>Rà soát quá trình sinh mã ticketId khi nhiều luồng cùng gọi đồng thời, tránh trùng mã ID.</li>
 *   <li>Đảm bảo file tickets.csv không bị hỏng dòng (corrupted lines) khi ghi dồn dập qua cơ chế Atomic File Write.</li>
 *   <li>Toàn bộ phương thức đọc/ghi và sinh ID đều được đồng bộ hóa qua instance monitor (synchronized(this)).</li>
 * </ul>
 * </p>
 *
 * @author Kiệt — Account & Data Safety
 */
public class TicketRepository extends CsvRepository<Ticket> {

    /**
     * Bộ đếm nội bộ theo dõi số thứ tự ticketId lớn nhất đã được cấp phát.
     * Được khởi tạo một lần khi nạp dữ liệu và tăng dần atomic trong khối synchronized.
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
    private synchronized int scanMaxIdNumber() {
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
     * Sinh mã vé kế tiếp, đảm bảo thread-safe tuyệt đối bằng synchronized trên instance monitor.
     * <p>
     * Cơ chế chống trùng ID:
     * <ol>
     *   <li>Tăng bộ đếm nội bộ {@code currentMaxIdNumber} lên 1 một cách nguyên tử.</li>
     *   <li>Tạo mã dự kiến định dạng TCK%06d.</li>
     *   <li>Kiểm tra candidateId có bị trùng trong repository không (phòng dữ liệu bị sửa tay bên ngoài).</li>
     *   <li>Nếu trùng, tiếp tục tịnh tiến cho đến khi tìm được ID duy nhất chưa từng dùng.</li>
     * </ol>
     * </p>
     *
     * @return ticketId duy nhất có định dạng TCK000001, TCK000002, ...
     */
    public synchronized String generateNextTicketId() {
        this.currentMaxIdNumber++;
        String candidateId = String.format("TCK%06d", this.currentMaxIdNumber);
        while (this.findById(candidateId) != null) {
            this.currentMaxIdNumber++;
            candidateId = String.format("TCK%06d", this.currentMaxIdNumber);
        }
        return candidateId;
    }

    /**
     * Lưu entity vào danh sách và ghi file — thread-safe.
     *
     * @param entity vé cần lưu
     * @return true nếu lưu thành công
     */
    @Override
    public synchronized boolean save(Ticket entity) {
        if (entity == null) {
            return false;
        }
        this.getItems().add(entity);
        this.saveToFile();
        return true;
    }

    /**
     * Thêm danh sách vé và ghi file một lần an toàn đa luồng.
     *
     * @param tickets danh sách vé cần thêm
     * @return true nếu thêm thành công
     */
    public synchronized boolean saveAll(Collection<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            return false;
        }
        this.getItems().addAll(tickets);
        this.saveToFile();
        return true;
    }

    /**
     * Ghi toàn bộ danh sách vé ra file — thread-safe và chống hỏng file (Atomic File Replacement).
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
            for (Ticket item : this.getItems()) {
                if (item != null) {
                    bw.write(item.toCsvLine());
                    bw.newLine();
                }
            }
            bw.flush();
        } catch (IOException e) {
            System.err.println("[TicketRepository] Loi ghi file tam: " + tempPathStr + " - " + e.getMessage());
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
            System.err.println("[TicketRepository] Loi thay the file chinh: " + targetPathStr + " - " + e.getMessage());
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
    public synchronized Ticket findById(String id) {
        if (id == null) {
            return null;
        }
        for (Ticket item : this.getItems()) {
            if (id.equals(item.getId())) {
                return item;
            }
        }
        return null;
    }

    @Override
    public synchronized List<Ticket> findAll() {
        return new ArrayList<>(this.getItems());
    }

    @Override
    public synchronized List<Ticket> findByCondition(Predicate<Ticket> predicate) {
        List<Ticket> result = new ArrayList<>();
        if (predicate == null) {
            return result;
        }
        for (Ticket item : this.getItems()) {
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

    public synchronized List<Ticket> findByMatchId(String matchId) {
        return this.findByCondition(t -> matchId != null && matchId.equals(t.getMatchId()));
    }

    public synchronized List<Ticket> findByFanId(String fanId) {
        return this.findByCondition(t -> fanId != null && fanId.equals(t.getFanId()));
    }

    public synchronized Ticket findBySeatAndMatch(String seatId, String matchId) {
        return this.findByCondition(t -> seatId != null && seatId.equals(t.getSeatId())
                && matchId != null && matchId.equals(t.getMatchId()))
                .stream()
                .findFirst()
                .orElse(null);
    }
}