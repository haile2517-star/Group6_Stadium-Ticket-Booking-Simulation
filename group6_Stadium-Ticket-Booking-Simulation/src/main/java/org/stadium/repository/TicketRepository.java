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

public class TicketRepository extends CsvRepository<Ticket> {

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

    public synchronized String generateNextTicketId() {
        this.currentMaxIdNumber++;
        String candidateId = String.format("TCK%06d", this.currentMaxIdNumber);
        while (this.findById(candidateId) != null) {
            this.currentMaxIdNumber++;
            candidateId = String.format("TCK%06d", this.currentMaxIdNumber);
        }
        return candidateId;
    }

    @Override
    public synchronized boolean save(Ticket entity) {
        if (entity == null) {
            return false;
        }
        this.getItems().add(entity);
        this.saveToFile();
        return true;
    }

    public synchronized boolean saveAll(Collection<Ticket> tickets) {
        if (tickets == null || tickets.isEmpty()) {
            return false;
        }
        this.getItems().addAll(tickets);
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

        Path sourcePath = Paths.get(tempPathStr);
        Path destPath = Paths.get(targetPathStr);
        try {
            try {
                Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException atomicEx) {

                Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            System.err.println("[TicketRepository] Loi thay the file chinh: " + targetPathStr + " - " + e.getMessage());
        }
    }

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
        List<Ticket> matches = this.findByCondition(t -> seatId != null && seatId.equals(t.getSeatId())
                && matchId != null && matchId.equals(t.getMatchId()));
        for (Ticket t : matches) {
            if (t.isAvailable()) {
                return t;
            }
        }
        return matches.isEmpty() ? null : matches.get(matches.size() - 1);
    }
}
