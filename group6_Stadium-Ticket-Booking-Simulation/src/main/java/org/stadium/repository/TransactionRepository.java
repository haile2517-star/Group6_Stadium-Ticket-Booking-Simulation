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

public class TransactionRepository extends CsvRepository<BookingTransaction> {

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

    public synchronized String generateNextTransactionId() {
        this.currentMaxIdNumber++;
        String candidateId = String.format("TXN%05d", this.currentMaxIdNumber);
        while (this.findById(candidateId) != null) {
            this.currentMaxIdNumber++;
            candidateId = String.format("TXN%05d", this.currentMaxIdNumber);
        }
        return candidateId;
    }

    @Override
    public synchronized boolean save(BookingTransaction entity) {
        if (entity == null) {
            return false;
        }
        this.getItems().add(entity);
        this.saveToFile();
        return true;
    }

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

        Path sourcePath = Paths.get(tempPathStr);
        Path destPath = Paths.get(targetPathStr);
        try {
            try {
                Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicEx) {

                Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("[TransactionRepository] Loi thay the file chinh: " + targetPathStr + " - " + e.getMessage());
        }
    }

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
