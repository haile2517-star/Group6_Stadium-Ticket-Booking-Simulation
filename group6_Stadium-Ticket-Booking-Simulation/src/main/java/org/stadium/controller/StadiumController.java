package org.stadium.controller;

import org.stadium.model.Seat;
import org.stadium.model.SeatSection;
import org.stadium.model.Stadium;
import org.stadium.model.enums.SeatStatus;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.SectionRepository;
import org.stadium.repository.StadiumRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller phụ trách quản lý Sân vận động & Khán đài / Ghế ngồi (Stadium & Seat domain).
 * Thành viên phụ trách: Nam (Tuần 5 - Controller Layer).
 */
public class StadiumController {

    private final StadiumRepository stadiumRepository;
    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;

    /**
     * Khởi tạo StadiumController với đầy đủ các repository (dùng cho Dependency Injection / Unit Test).
     *
     * @param stadiumRepository repository sân vận động
     * @param sectionRepository repository khu vực / khán đài
     * @param seatRepository repository ghế ngồi
     */
    public StadiumController(StadiumRepository stadiumRepository,
                             SectionRepository sectionRepository,
                             SeatRepository seatRepository) {
        this.stadiumRepository = stadiumRepository;
        this.sectionRepository = sectionRepository;
        this.seatRepository = seatRepository;
    }

    /**
     * Khởi tạo StadiumController với SectionRepository và SeatRepository.
     */
    public StadiumController(SectionRepository sectionRepository, SeatRepository seatRepository) {
        String path = new File("data/stadiums.csv").exists() ? "data/stadiums.csv" : (new File("../data/stadiums.csv").exists() ? "../data/stadiums.csv" : "data/stadiums.csv");
        this.stadiumRepository = new StadiumRepository(path);
        this.sectionRepository = sectionRepository;
        this.seatRepository = seatRepository;
    }

    /**
     * Constructor mặc định tự động nạp dữ liệu từ thư mục data / ../data.
     */
    public StadiumController() {
        String pathStadiums = new File("data/stadiums.csv").exists() ? "data/stadiums.csv" : (new File("../data/stadiums.csv").exists() ? "../data/stadiums.csv" : "data/stadiums.csv");
        String pathSections = new File("data/sections.csv").exists() ? "data/sections.csv" : (new File("../data/sections.csv").exists() ? "../data/sections.csv" : "data/sections.csv");
        String pathSeats = new File("data/seats.csv").exists() ? "data/seats.csv" : (new File("../data/seats.csv").exists() ? "../data/seats.csv" : "data/seats.csv");
        this.stadiumRepository = new StadiumRepository(pathStadiums);
        this.sectionRepository = new SectionRepository(pathSections);
        this.seatRepository = new SeatRepository(pathSeats);
    }

    /**
     * Tìm đường dẫn file dữ liệu CSV phù hợp (hỗ trợ cả chạy từ thư mục gốc hoặc thư mục con).
     */
    public String resolvePath(String fileName) {
        if (new File("data/" + fileName).exists()) {
            return "data/" + fileName;
        } else if (new File("../data/" + fileName).exists()) {
            return "../data/" + fileName;
        }
        return "data/" + fileName;
    }

    /**
     * Lấy toàn bộ danh sách sân vận động trong hệ thống.
     * Khớp chính xác phương thức trong Class Diagram: +getAllStadiums(): List~Stadium~
     */
    public List<Stadium> getAllStadiums() {
        if (this.stadiumRepository == null) {
            return Collections.emptyList();
        }
        return this.stadiumRepository.findAll();
    }

    /**
     * Lấy danh sách các khán đài / khu vực (SeatSection) thuộc về một sân vận động cụ thể.
     * Khớp chính xác phương thức trong Class Diagram: +getSections(stadiumId: String): List~SeatSection~
     *
     * @param stadiumId mã sân vận động (ví dụ: STD01)
     * @return danh sách các SeatSection, hoặc danh sách rỗng nếu không tìm thấy
     */
    public List<SeatSection> getSections(String stadiumId) {
        if (stadiumId == null || stadiumId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        if (this.sectionRepository == null) {
            return Collections.emptyList();
        }
        return this.sectionRepository.findByStadiumId(stadiumId.trim());
    }

    /**
     * Render sơ đồ ghế của một khu vực (SeatSection) dưới dạng ASCII map
     * để hiển thị trên giao diện dòng lệnh / chuẩn bị cho UI.
     * Khớp chính xác phương thức trong Class Diagram: +buildSeatMap(sectionId: String): String
     *
     * @param sectionId mã khu vực (ví dụ: SEC_STD01_A)
     * @return chuỗi biểu diễn sơ đồ ghế dạng ASCII map
     */
    public String buildSeatMap(String sectionId) {
        if (sectionId == null || sectionId.trim().isEmpty()) {
            return "Invalid section ID.";
        }

        String targetSectionId = sectionId.trim();
        SeatSection section = this.sectionRepository != null ? this.sectionRepository.findById(targetSectionId) : null;
        if (section == null) {
            return "Section not found: " + targetSectionId;
        }

        List<Seat> seats = this.seatRepository != null ? this.seatRepository.findBySectionId(targetSectionId) : Collections.emptyList();
        if (seats == null || seats.isEmpty()) {
            return "No seats found for section: " + targetSectionId;
        }

        // Gom nhóm ghế theo từng hàng (rowNumber)
        Map<Integer, List<Seat>> rows = new TreeMap<>();
        int availableCount = 0;
        int bookedCount = 0;
        int lockedCount = 0;

        for (Seat seat : seats) {
            rows.computeIfAbsent(seat.getRowNumber(), k -> new ArrayList<>()).add(seat);
            SeatStatus status = seat.getStatus();
            if (status == SeatStatus.AVAILABLE) {
                availableCount++;
            } else if (status == SeatStatus.BOOKED) {
                bookedCount++;
            } else if (status == SeatStatus.LOCKED) {
                lockedCount++;
            }
        }

        StringBuilder map = new StringBuilder();

        // Tiêu đề khu vực
        map.append("\n======================================================================\n");
        map.append(String.format("  SECTION: %s (%s)\n", section.getSectionName(), section.getSectionId()));
        map.append(String.format("  Type: %s | Total Rows: %d | Seats/Row: %d\n",
                section.getSectionType() != null ? section.getSectionType().name() : "N/A",
                section.getTotalRows(),
                section.getSeatsPerRow()));
        map.append(String.format("  Summary: Total: %d | Available [O]: %d | Booked [X]: %d | Locked [L]: %d\n",
                seats.size(), availableCount, bookedCount, lockedCount));
        map.append("======================================================================\n");

        // Khu vực sân khấu / sân cỏ trung tâm
        map.append("                         ====================\n");
        map.append("                         |      STAGE       |\n");
        map.append("                         ====================\n\n");

        Pattern digitPattern = Pattern.compile("(\\d+)$");

        // Vẽ từng hàng ghế
        for (Map.Entry<Integer, List<Seat>> entry : rows.entrySet()) {
            int rowNumber = entry.getKey();
            List<Seat> rowSeats = entry.getValue();

            // Sắp xếp các ghế trong hàng theo số thứ tự ghế
            rowSeats.sort(Comparator.comparingInt(seat -> this.extractSeatSequence(seat.getSeatNumber(), digitPattern)));

            map.append(String.format("Row %02d: ", rowNumber));

            for (Seat seat : rowSeats) {
                map.append("[").append(this.getSeatSymbol(seat.getStatus())).append("] ");
            }

            map.append("\n");
        }

        // Chú giải trạng thái
        map.append("\nLegend: [O] Available    [X] Booked    [L] Locked    [?] Unknown\n");
        map.append("======================================================================\n");

        return map.toString();
    }

    /**
     * Trích xuất số thứ tự ghế từ mã ghế (ví dụ: "R01-S05" -> 5).
     */
    private int extractSeatSequence(String seatNumber, Pattern pattern) {
        if (seatNumber == null || seatNumber.trim().isEmpty()) {
            return 0;
        }
        Matcher matcher = pattern.matcher(seatNumber.trim());
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    /**
     * Chuyển đổi trạng thái ghế sang ký hiệu hiển thị trên sơ đồ ASCII.
     */
    private String getSeatSymbol(SeatStatus status) {
        if (status == null) {
            return "?";
        }
        switch (status) {
            case AVAILABLE:
                return "O";
            case BOOKED:
                return "X";
            case LOCKED:
                return "L";
            default:
                return "?";
        }
    }

    /**
     * Lấy danh sách toàn bộ ghế thuộc một khu vực.
     * Khớp chính xác phương thức trong Class Diagram: +getSeats(sectionId: String): List~Seat~
     */
    public List<Seat> getSeats(String sectionId) {
        if (sectionId == null || sectionId.trim().isEmpty() || this.seatRepository == null) {
            return Collections.emptyList();
        }
        return this.seatRepository.findBySectionId(sectionId.trim());
    }

    /**
     * Alias tương thích ngược cho getSeats(sectionId).
     */
    public List<Seat> getSeatsBySection(String sectionId) {
        return this.getSeats(sectionId);
    }

    /**
     * Lấy thông tin chi tiết một khu vực theo mã.
     */
    public SeatSection getSection(String sectionId) {
        if (sectionId == null || sectionId.trim().isEmpty() || this.sectionRepository == null) {
            return null;
        }
        return this.sectionRepository.findById(sectionId.trim());
    }

    /**
     * Lấy thông tin sân vận động theo mã.
     */
    public Stadium getStadium(String stadiumId) {
        if (stadiumId == null || stadiumId.trim().isEmpty() || this.stadiumRepository == null) {
            return null;
        }
        return this.stadiumRepository.findById(stadiumId.trim());
    }

    /**
     * Lấy danh sách ghế còn trống (AVAILABLE) thuộc một khu vực.
     */
    public List<Seat> getAvailableSeats(String sectionId) {
        if (sectionId == null || sectionId.trim().isEmpty() || this.seatRepository == null) {
            return Collections.emptyList();
        }
        return this.seatRepository.findAvailableBySection(sectionId.trim());
    }

    public StadiumRepository getStadiumRepository() {
        return this.stadiumRepository;
    }

    public SectionRepository getSectionRepository() {
        return this.sectionRepository;
    }

    public SeatRepository getSeatRepository() {
        return this.seatRepository;
    }
}
