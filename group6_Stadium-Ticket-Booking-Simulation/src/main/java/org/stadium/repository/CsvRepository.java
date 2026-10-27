package org.stadium.repository;

import org.stadium.model.BaseEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

// Lớp cơ sở trừu tượng Generic cho tầng Repository thao tác đọc/ghi file CSV
public abstract class CsvRepository<T extends BaseEntity> { // Generic ràng buộc T phải kế thừa từ BaseEntity
    private String filePath;
    private List<T> items = new ArrayList<>();

    // Khởi tạo Repository với đường dẫn file và tự động nạp dữ liệu từ file lên RAM
    public CsvRepository(String filePath) {
        this.filePath = filePath;
        this.loadFromFile(); // Tự động nạp dữ liệu ngay khi khởi tạo
    }

    // Factory method trừu tượng để lớp con tạo instance thực thể rỗng
    public abstract T createEntity();

    // Đọc toàn bộ dữ liệu từ file CSV vào danh sách items trong RAM
    public void loadFromFile() {
        this.items.clear(); // Xóa sạch dữ liệu cũ trong bộ nhớ
        // Sử dụng try-with-resources để tự động đóng file an toàn
        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            String line;
            boolean firstLine = true;
            // Đọc từng dòng cho đến hết file
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false; // Bỏ qua dòng tiêu đề (header) đầu tiên
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                T entity = this.createEntity(); // Khởi tạo thực thể rỗng
                entity.fromCsvLine(line); // Parse dữ liệu từ dòng CSV vào thực thể
                this.items.add(entity); // Thêm vào danh sách bộ nhớ
            }
        } catch (IOException e) {
            System.err.println("[CsvRepository] Loi doc file: " + this.filePath + "-" + e.getMessage());
        }
    }

    // Ghi toàn bộ dữ liệu từ danh sách items trong RAM xuống file CSV
    public void saveToFile() {
        // Mở file và ghi đè nội dung mới bằng BufferedWriter
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.filePath))) {
            bw.write(this.getHeader()); // Ghi dòng tiêu đề cột
            bw.newLine();
            // Ghi từng đối tượng dưới dạng dòng CSV
            for (T item : this.items) {
                bw.write(item.toCsvLine()); // Gọi hàm chuyển đối tượng thành chuỗi CSV
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[CsvRepository] Loi ghi file " + this.filePath + "-" + e.getMessage());
        }
    }

    // Trả về dòng tiêu đề header của file CSV (do từng Repository con định nghĩa)
    public abstract String getHeader();

    // Thêm hoặc lưu một thực thể vào bộ nhớ và đồng bộ xuống file CSV
    public boolean save(T entity) {
        if (entity == null) {
            return false;
        }
        int existingIndex = -1;
        for (int i = 0; i < this.items.size(); i++) {
            T item = this.items.get(i);
            if (item == entity || (item.getId() != null && item.getId().equals(entity.getId()))) {
                existingIndex = i;
                break;
            }
        }
        if (existingIndex >= 0) {
            this.items.set(existingIndex, entity);
        } else {
            this.items.add(entity);
        }
        this.saveToFile(); // Lưu ngay xuống đĩa cứng
        return true;
    }

    // Tìm kiếm thực thể theo mã ID duy nhất
    public T findById(String id) {
        // Sử dụng Java Stream API để tìm phần tử đầu tiên khớp ID
        return this.items.stream()
                .filter(e -> e.getId().equals(id)) // Lọc theo getId()
                .findFirst()
                .orElse(null); // Trả về null nếu không tìm thấy
    }

    // Trả về bản sao danh sách tất cả các thực thể hiện có
    public List<T> findAll() {
        return new ArrayList<>(this.items); // Trả về bản sao để bảo vệ tính đóng gói
    }

    // Tìm kiếm các thực thể thỏa mãn điều kiện lọc qua Predicate Lambda
    public List<T> findByCondition(Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        // Duyệt danh sách và kiểm tra điều kiện test
        for (T item : this.items) {
            if (predicate.test(item)) { // Kiểm tra điều kiện truyền vào qua functional interface
                result.add(item);
            }
        }
        return result;
    }

    // Xóa thực thể khỏi danh sách theo ID và lưu lại file CSV
    public boolean deleteById(String id) {
        boolean removed = this.items.removeIf(e -> e.getId().equals(id)); // removeIf xóa phần tử khớp điều kiện
        if (removed) {
            this.saveToFile(); // Đồng bộ lại file CSV sau khi xóa
        }
        return removed;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public List<T> getItems() {
        return this.items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
