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
        loadFromFile();
    }

    protected abstract T createEntity();

    public void loadFromFile() {
        items.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                if (line.isEmpty())
                    continue;
                T entity = createEntity();
                entity.fromCsvLine(line);
                items.add(entity);
            }

        } catch (IOException e) {
            System.err.println("[CsvRepository] Loi doc file: " + filePath + "-" + e.getMessage());
        }
    }

    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            bw.write(getHeader());
            bw.newLine();
            for (T item : items) {
                bw.write(item.toCsvLine());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("[CsvRepository] Loi ghi file " + filePath + "-" + e.getMessage());
        }
    }

    protected abstract String getHeader();

    public boolean save(T entity) {
        items.add(entity);
        saveToFile();
        return true;
    }

    public T findById(String id) {
        return items.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public List<T> findAll() {
        return new ArrayList<>(items);
    }

    public List<T> findByCondition(Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (predicate.test(item))
                result.add(item);
        }
        return result;
    }

    public boolean deleteById(String id) {
        boolean removed = items.removeIf(e -> e.getId().equals(id));
        if (removed)
            saveToFile();
        return removed;
    }
}
