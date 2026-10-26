package org.stadium.model;

// Lớp thực thể trừu tượng gốc (Base Entity) quy định giao tiếp CSV cho mọi Model
public abstract class BaseEntity {
    // Trả về khóa chính (ID duy nhất) của thực thể
    public abstract String getId();

    // Chuyển đổi đối tượng thành một dòng chuỗi định dạng CSV để ghi vào file
    public abstract String toCsvLine();

    // Bóc tách một dòng chuỗi từ file CSV và gán vào các thuộc tính của đối tượng
    public abstract void fromCsvLine(String line);
}
