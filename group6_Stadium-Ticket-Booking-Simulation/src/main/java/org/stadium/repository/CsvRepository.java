package org.stadium.repository;

import org.stadium.model.BaseEntity;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public abstract class CsvRepository<T extends BaseEntity> {
    private String filePath;
    private List<T> items = new ArrayList<>();

    public CsvRepository(String filePath) {
        this.filePath = filePath;
        this.loadFromFile();
    }

    public abstract T createEntity();

    public void loadFromFile() {
        this.items.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.isEmpty()) {
                    continue;
                }
                T entity = this.createEntity();
                entity.fromCsvLine(line);
                this.items.add(entity);
            }
        } catch (IOException e) {
            System.err.println("[CsvRepository] Loi doc file: " + this.filePath + "-" + e.getMessage());
        }
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(this.filePath))) {
            bw.write(this.getHeader());
            bw.newLine();
            for (T item : this.items) {
                bw.write(item.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[CsvRepository] Loi ghi file " + this.filePath + "-" + e.getMessage());
        }
    }

    public abstract String getHeader();

    public boolean save(T entity) {
        this.items.add(entity);
        this.saveToFile();
        return true;
    }

    public T findById(String id) {
        return this.items.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<T> findAll() {
        return new ArrayList<>(this.items);
    }

    public List<T> findByCondition(Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : this.items) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    public boolean deleteById(String id) {
        boolean removed = this.items.removeIf(e -> e.getId().equals(id));
        if (removed) {
            this.saveToFile();
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
