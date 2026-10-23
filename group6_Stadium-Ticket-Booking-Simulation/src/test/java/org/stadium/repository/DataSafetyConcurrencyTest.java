package org.stadium.repository;

import org.stadium.model.BookingTransaction;
import org.stadium.model.Ticket;
import org.stadium.model.enums.TicketStatus;
import org.stadium.model.enums.TransactionStatus;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Bộ kiểm thử toàn diện tính an toàn dữ liệu đa luồng (Tuần 7 - Kiệt: Account & Data Safety).
 * <p>
 * Kiểm chứng các yêu cầu cốt lõi trong phân công Tuần 7:
 * <ol>
 *   <li>Rà soát quá trình sinh mã transactionId và ticketId khi nhiều luồng cùng gọi đồng thời, tránh trùng mã ID.</li>
 *   <li>Đảm bảo file transactions.csv và tickets.csv không bị hỏng dòng (corrupted lines) khi ghi dồn dập.</li>
 *   <li>Deliverable: File CSV dữ liệu toàn vẹn sau khi thử nghiệm luồng.</li>
 * </ol>
 * </p>
 *
 * @author Kiệt — Account & Data Safety
 */
public class DataSafetyConcurrencyTest {

    private int testsRun;
    private int testsPassed;

    public DataSafetyConcurrencyTest() {
        this.testsRun = 0;
        this.testsPassed = 0;
    }

    public static void main(String[] args) {
        DataSafetyConcurrencyTest runner = new DataSafetyConcurrencyTest();
        runner.runAllTests();
    }

    public void runAllTests() {
        System.out.println("=== BAT DAU KIEM THU AN TOAN DU LIEU DONG BO HOA (TUAN 7 - KIET) ===\n");

        this.test("1. Kiem thu sinh ma transactionId da luong khong trung lap (20 threads)", () -> {
            this.testConcurrentTransactionIdGeneration();
        });

        this.test("2. Kiem thu sinh ma ticketId da luong khong trung lap (20 threads)", () -> {
            this.testConcurrentTicketIdGeneration();
        });

        this.test("3. Kiem thu ghi don dap vao transactions.csv khong hong dong (10 threads)", () -> {
            this.testConcurrentTransactionSavingAndFileIntegrity();
        });

        this.test("4. Kiem thu ghi don dap vao tickets.csv khong hong dong (10 threads)", () -> {
            this.testConcurrentTicketSavingAndFileIntegrity();
        });

        this.test("5. Kiem thu vua doc vua ghi dong thoi (Mixed Read/Write Concurrency)", () -> {
            this.testMixedReadWriteConcurrency();
        });

        this.test("6. Kiem thu khoi phuc bo dem sau khi reload file CSV (Persistence & Continuity)", () -> {
            this.testPersistenceAndReload();
        });

        System.out.println("\n--------------------------------------------------");
        System.out.printf("KET QUA DATA SAFETY TEST: %d/%d test cases DA PASS!%n", this.testsPassed, this.testsRun);
        System.out.println("--------------------------------------------------");

        if (this.testsPassed < this.testsRun) {
            throw new AssertionError("Co test case bi FAIL trong bo kiem thu!");
        }
    }

    private void test(String testName, Runnable task) {
        this.testsRun++;
        try {
            task.run();
            this.testsPassed++;
            System.out.println("[PASS] " + testName);
        } catch (Throwable t) {
            System.err.println("[FAIL] " + testName + " -> " + t.getMessage());
            t.printStackTrace(System.err);
        }
    }

    /**
     * Test 1: Cho 20 thread chạy đồng thời sinh transactionId.
     * Kiểm tra: không có bất kỳ ID nào bị trùng, đúng định dạng TXNxxxxx.
     */
    private void testConcurrentTransactionIdGeneration() {
        File tempFile = new File("target/test-data/data-safety/txn_id_test.csv");
        this.prepareCleanFile(tempFile, "transactionId,fanId,matchId,totalAmount,ticketCount,transactionDate,status");

        TransactionRepository repo = new TransactionRepository(tempFile.getPath());
        int threadCount = 20;
        int idsPerThread = 10;
        int totalExpectedIds = threadCount * idsPerThread;

        List<String> generatedIds = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < idsPerThread; j++) {
                        String id = repo.generateNextTransactionId();
                        generatedIds.add(id);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        this.awaitLatch(doneLatch);
        executor.shutdown();

        this.assertCondition(generatedIds.size() == totalExpectedIds,
                "Tong so ID sinh ra phai la " + totalExpectedIds + ", thuc te: " + generatedIds.size());

        Set<String> uniqueIds = new HashSet<>(generatedIds);
        this.assertCondition(uniqueIds.size() == totalExpectedIds,
                "Phat hien trung ma transactionId! Unique: " + uniqueIds.size() + "/" + totalExpectedIds);

        for (String id : uniqueIds) {
            this.assertCondition(id.startsWith("TXN") && id.length() == 8,
                    "Dinh dang transactionId khong hop le: " + id);
        }
    }

    /**
     * Test 2: Cho 20 thread chạy đồng thời sinh ticketId.
     * Kiểm tra: không có bất kỳ ID nào bị trùng, đúng định dạng TCKxxxxxx.
     */
    private void testConcurrentTicketIdGeneration() {
        File tempFile = new File("target/test-data/data-safety/ticket_id_test.csv");
        this.prepareCleanFile(tempFile, "ticketId,matchId,seatId,transactionId,fanId,price,bookingDate,status");

        TicketRepository repo = new TicketRepository(tempFile.getPath());
        int threadCount = 20;
        int idsPerThread = 10;
        int totalExpectedIds = threadCount * idsPerThread;

        List<String> generatedIds = Collections.synchronizedList(new ArrayList<>());
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < idsPerThread; j++) {
                        String id = repo.generateNextTicketId();
                        generatedIds.add(id);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        this.awaitLatch(doneLatch);
        executor.shutdown();

        this.assertCondition(generatedIds.size() == totalExpectedIds,
                "Tong so ID sinh ra phai la " + totalExpectedIds + ", thuc te: " + generatedIds.size());

        Set<String> uniqueIds = new HashSet<>(generatedIds);
        this.assertCondition(uniqueIds.size() == totalExpectedIds,
                "Phat hien trung ma ticketId! Unique: " + uniqueIds.size() + "/" + totalExpectedIds);

        for (String id : uniqueIds) {
            this.assertCondition(id.startsWith("TCK") && id.length() == 9,
                    "Dinh dang ticketId khong hop le: " + id);
        }
    }

    /**
     * Test 3: Ghi dồn dập vào transactions.csv với 10 threads chạy song song.
     * Kiểm tra: File CSV không bị hỏng dòng, số dòng khớp chính xác, các trường dữ liệu nguyên vẹn.
     */
    private void testConcurrentTransactionSavingAndFileIntegrity() {
        File tempFile = new File("target/test-data/data-safety/transactions_stress.csv");
        this.prepareCleanFile(tempFile, "transactionId,fanId,matchId,totalAmount,ticketCount,transactionDate,status");

        TransactionRepository repo = new TransactionRepository(tempFile.getPath());
        int threadCount = 10;
        int recordsPerThread = 10;
        int totalExpected = threadCount * recordsPerThread;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadIdx = t;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < recordsPerThread; i++) {
                        String txnId = repo.generateNextTransactionId();
                        BookingTransaction txn = new BookingTransaction(
                                txnId,
                                "FAN_" + threadIdx,
                                "MTC01",
                                "2026-10-23 20:00:00",
                                250000.0,
                                1,
                                TransactionStatus.SUCCESS
                        );
                        repo.save(txn);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        this.awaitLatch(doneLatch);
        executor.shutdown();

        // Kiểm tra bộ nhớ trong repo
        this.assertCondition(repo.findAll().size() == totalExpected,
                "So items trong repo phai la " + totalExpected + ", thuc te: " + repo.findAll().size());

        // Kiểm tra tính toàn vẹn của file CSV thực tế trên đĩa
        List<String> lines = this.readFileLines(tempFile);
        this.assertCondition(lines.size() == totalExpected + 1,
                "File CSV phai co dung " + (totalExpected + 1) + " dong (gom header), thuc te: " + lines.size());
        this.assertCondition(lines.get(0).equals("transactionId,fanId,matchId,totalAmount,ticketCount,transactionDate,status"),
                "Header file CSV bi hong: " + lines.get(0));

        // Kiểm tra từng dòng không bị hỏng cấu trúc (đủ 7 cột phân tách bởi dấu phẩy)
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",", -1);
            this.assertCondition(parts.length == 7,
                    "Dong thu " + i + " trong transactions.csv bi hong dong (so cot != 7): " + line);
            this.assertCondition(parts[0].startsWith("TXN"),
                    "Dong thu " + i + " co transactionId khong hop le: " + parts[0]);
        }
    }

    /**
     * Test 4: Ghi dồn dập vào tickets.csv với 10 threads chạy song song.
     * Kiểm tra: File CSV không bị hỏng dòng, số dòng khớp chính xác, các trường dữ liệu nguyên vẹn.
     */
    private void testConcurrentTicketSavingAndFileIntegrity() {
        File tempFile = new File("target/test-data/data-safety/tickets_stress.csv");
        this.prepareCleanFile(tempFile, "ticketId,matchId,seatId,transactionId,fanId,price,bookingDate,status");

        TicketRepository repo = new TicketRepository(tempFile.getPath());
        int threadCount = 10;
        int recordsPerThread = 10;
        int totalExpected = threadCount * recordsPerThread;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int threadIdx = t;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < recordsPerThread; i++) {
                        String tckId = repo.generateNextTicketId();
                        Ticket ticket = new Ticket(
                                tckId,
                                "MTC01",
                                "SEC_A_R01_S" + (threadIdx * 10 + i),
                                "TXN00001",
                                "FAN_" + threadIdx,
                                200000.0,
                                "2026-10-23 20:00:00",
                                TicketStatus.BOOKED
                        );
                        repo.save(ticket);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        this.awaitLatch(doneLatch);
        executor.shutdown();

        this.assertCondition(repo.findAll().size() == totalExpected,
                "So items trong repo phai la " + totalExpected + ", thuc te: " + repo.findAll().size());

        List<String> lines = this.readFileLines(tempFile);
        this.assertCondition(lines.size() == totalExpected + 1,
                "File CSV phai co dung " + (totalExpected + 1) + " dong (gom header), thuc te: " + lines.size());
        this.assertCondition(lines.get(0).equals("ticketId,matchId,seatId,transactionId,fanId,price,bookingDate,status"),
                "Header file CSV bi hong: " + lines.get(0));

        // Kiểm tra từng dòng đủ 8 cột
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] parts = line.split(",", -1);
            this.assertCondition(parts.length == 8,
                    "Dong thu " + i + " trong tickets.csv bi hong dong (so cot != 8): " + line);
            this.assertCondition(parts[0].startsWith("TCK"),
                    "Dong thu " + i + " co ticketId khong hop le: " + parts[0]);
        }
    }

    /**
     * Test 5: Vừa đọc vừa ghi đồng thời (không xảy ra ConcurrentModificationException).
     */
    private void testMixedReadWriteConcurrency() {
        File tempFile = new File("target/test-data/data-safety/mixed_test.csv");
        this.prepareCleanFile(tempFile, "transactionId,fanId,matchId,totalAmount,ticketCount,transactionDate,status");

        TransactionRepository repo = new TransactionRepository(tempFile.getPath());
        int writeThreads = 5;
        int readThreads = 5;
        int opsPerThread = 20;

        ExecutorService executor = Executors.newFixedThreadPool(writeThreads + readThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(writeThreads + readThreads);

        // Luồng ghi
        for (int i = 0; i < writeThreads; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < opsPerThread; j++) {
                        String id = repo.generateNextTransactionId();
                        repo.save(new BookingTransaction(id, "FAN_" + idx, "MTC01", "now", 100000.0, 1, TransactionStatus.SUCCESS));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        // Luồng đọc
        for (int i = 0; i < readThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < opsPerThread; j++) {
                        repo.findAll();
                        repo.findByFanId("FAN_0");
                        repo.findByStatus(TransactionStatus.SUCCESS);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        this.awaitLatch(doneLatch);
        executor.shutdown();

        this.assertCondition(repo.findAll().size() == writeThreads * opsPerThread,
                "Tong so record sau mixed test phai dung: " + (writeThreads * opsPerThread));
    }

    /**
     * Test 6: Kiểm tra khôi phục bộ đếm ID khi tải lại file CSV (Persistence).
     */
    private void testPersistenceAndReload() {
        File tempFile = new File("target/test-data/data-safety/reload_test.csv");
        this.prepareCleanFile(tempFile, "transactionId,fanId,matchId,totalAmount,ticketCount,transactionDate,status");

        TransactionRepository repo1 = new TransactionRepository(tempFile.getPath());
        repo1.save(new BookingTransaction("TXN00001", "FAN1", "M1", "now", 100.0, 1, TransactionStatus.SUCCESS));
        repo1.save(new BookingTransaction("TXN00002", "FAN2", "M1", "now", 100.0, 1, TransactionStatus.SUCCESS));
        repo1.save(new BookingTransaction("TXN00050", "FAN3", "M1", "now", 100.0, 1, TransactionStatus.SUCCESS));

        // Khởi tạo instance mới từ cùng file đã có dữ liệu
        TransactionRepository repo2 = new TransactionRepository(tempFile.getPath());
        String nextId = repo2.generateNextTransactionId();

        this.assertCondition("TXN00051".equals(nextId),
                "Sau khi load lai file, transactionId tiep theo phai la TXN00051, thuc te: " + nextId);
    }

    private void prepareCleanFile(File file, String header) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        try {
            java.nio.file.Files.write(file.toPath(), (header + "\n").getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Loi tao file test: " + file.getPath(), e);
        }
    }

    private List<String> readFileLines(File file) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Loi doc file test: " + file.getPath(), e);
        }
        return lines;
    }

    private void awaitLatch(CountDownLatch latch) {
        try {
            boolean completed = latch.await(10, TimeUnit.SECONDS);
            if (!completed) {
                throw new AssertionError("Timeout 10s cho xu ly da luong!");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread bi gian doan!", e);
        }
    }

    private void assertCondition(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
