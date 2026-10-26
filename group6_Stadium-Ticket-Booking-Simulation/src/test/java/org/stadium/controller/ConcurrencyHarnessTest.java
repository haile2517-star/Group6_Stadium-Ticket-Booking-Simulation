package org.stadium.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.stadium.model.Seat;
import org.stadium.model.enums.SeatStatus;
import org.stadium.model.enums.SynchronizationMechanism;
import org.stadium.model.enums.TicketStatus;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.TicketRepository;
import org.stadium.repository.TransactionRepository;

public class ConcurrencyHarnessTest {

  public static void main(String[] args) {
    new ConcurrencyHarnessTest().runTests();
  }

  public void runTests() {
    int testsRun = 0;
    for (SynchronizationMechanism mechanism :
        new SynchronizationMechanism[] {
          SynchronizationMechanism.FILE_LOCK,
          SynchronizationMechanism.SYNCHRONIZED,
          SynchronizationMechanism.OPTIMISTIC
        }) {
      for (int threadCount = 2; threadCount <= 10; threadCount++) {
        this.testConcurrentBooking(mechanism, threadCount);
        testsRun++;
      }
    }
    this.testStaleVersionIsRejected();
    testsRun++;
    System.out.println("Passed: " + testsRun + "/" + testsRun + " concurrency scenarios");
  }

  private void testConcurrentBooking(SynchronizationMechanism mechanism, int threadCount) {
    Fixture fixture = this.createFixture(mechanism, threadCount);
    ExecutorService executor = Executors.newFixedThreadPool(threadCount);
    CountDownLatch ready = new CountDownLatch(threadCount);
    CountDownLatch start = new CountDownLatch(1);
    List<Future<Boolean>> results = new ArrayList<>();
    try {
      for (int index = 0; index < threadCount; index++) {
        results.add(
            executor.submit(
                () -> {
                  ready.countDown();
                  start.await();
                  return fixture.controller.bookTickets(
                      "FAN0001",
                      "MTH01",
                      Collections.singletonList("SEAT001"),
                      mechanism);
                }));
      }
      if (!ready.await(10, TimeUnit.SECONDS)) {
        throw new AssertionError(
            "Threads did not become ready for " + mechanism + " with " + threadCount + " threads");
      }
      start.countDown();

      int successfulBookings = 0;
      for (Future<Boolean> result : results) {
        if (result.get(10, TimeUnit.SECONDS)) {
          successfulBookings++;
        }
      }
      this.assertEquals(
          "successful bookings for " + mechanism + " with " + threadCount + " threads",
          1,
          successfulBookings);

      SeatRepository persistedSeats = new SeatRepository(fixture.seatsFile.getPath());
      TicketRepository persistedTickets = new TicketRepository(fixture.ticketsFile.getPath());
      TransactionRepository persistedTransactions =
          new TransactionRepository(fixture.transactionsFile.getPath());
      Seat persistedSeat = persistedSeats.findById("SEAT001");
      this.assertEquals("persisted seat status", SeatStatus.BOOKED, persistedSeat.getStatus());
      this.assertEquals("persisted seat version", 1, persistedSeat.getVersion());
      this.assertEquals(
          "persisted ticket status",
          TicketStatus.BOOKED,
          persistedTickets.findById("TCK000001").getStatus());
      this.assertEquals(
          "persisted transaction count",
          1,
          persistedTransactions.findAll().size());
    } catch (Exception exception) {
      throw new AssertionError(
          "Concurrency scenario failed for " + mechanism + " with " + threadCount + " threads",
          exception);
    } finally {
      start.countDown();
      executor.shutdownNow();
      fixture.close();
    }
    System.out.println("[PASS] " + mechanism + " with " + threadCount + " threads");
  }

  private void testStaleVersionIsRejected() {
    Fixture fixture = this.createFixture(SynchronizationMechanism.OPTIMISTIC, 1);
    try {
      SeatRepository firstRepository = new SeatRepository(fixture.seatsFile.getPath());
      SeatRepository staleRepository = new SeatRepository(fixture.seatsFile.getPath());
      Seat firstUpdate = firstRepository.findById("SEAT001");
      firstUpdate.setStatus(SeatStatus.BOOKED);
      this.assertTrue(
          "first versioned update",
          firstRepository.updateWithOptimisticLock(firstUpdate, 0));

      Seat staleUpdate = staleRepository.findById("SEAT001");
      staleUpdate.setStatus(SeatStatus.AVAILABLE);
      this.assertFalse(
          "stale version update",
          staleRepository.updateWithOptimisticLock(staleUpdate, 0));
      this.assertEquals(
          "seat status after stale update",
          SeatStatus.BOOKED,
          new SeatRepository(fixture.seatsFile.getPath()).findById("SEAT001").getStatus());
      System.out.println("[PASS] stale optimistic version is rejected");
    } finally {
      fixture.close();
    }
  }

  private Fixture createFixture(SynchronizationMechanism mechanism, int threadCount) {
    try {
      File rootDirectory = new File("target/test-data/concurrency-harness");
      if (!rootDirectory.mkdirs() && !rootDirectory.isDirectory()) {
        throw new IOException("Could not create test directory: " + rootDirectory);
      }
      File directory =
          new File(rootDirectory, mechanism.name().toLowerCase() + "-" + threadCount);
      if (!directory.mkdirs() && !directory.isDirectory()) {
        throw new IOException("Could not create test directory: " + directory);
      }
      File seatsFile = new File(directory, "seats.csv");
      File ticketsFile = new File(directory, "tickets.csv");
      File transactionsFile = new File(directory, "transactions.csv");
      this.writeCsv(
          seatsFile,
          "seatId,sectionId,rowNumber,seatNumber,seatType,status,version\n"
              + "SEAT001,SEC01,1,S1,STANDARD,AVAILABLE,0\n");
      this.writeCsv(
          ticketsFile,
          "ticketId,matchId,seatId,transactionId,fanId,price,bookingDate,status\n"
              + "TCK000001,MTH01,SEAT001,,,100000.0,,AVAILABLE\n");
      this.writeCsv(
          transactionsFile,
          "transactionId,fanId,matchId,totalAmount,ticketCount,transactionDate,status\n");

      SeatRepository seatRepository = new SeatRepository(seatsFile.getPath());
      TicketRepository ticketRepository = new TicketRepository(ticketsFile.getPath());
      TransactionRepository transactionRepository =
          new TransactionRepository(transactionsFile.getPath());
      BookingController controller =
          new BookingController(seatRepository, ticketRepository, transactionRepository);
      return new Fixture(directory, seatsFile, ticketsFile, transactionsFile, controller);
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }

  private void writeCsv(File file, String content) throws IOException {
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(content);
    }
  }

  private void assertTrue(String message, boolean condition) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }

  private void assertFalse(String message, boolean condition) {
    this.assertTrue(message, !condition);
  }

  private void assertEquals(String message, Object expectedValue, Object actualValue) {
    if (!expectedValue.equals(actualValue)) {
      throw new AssertionError(
          message + ": expected " + expectedValue + " but got " + actualValue);
    }
  }

  private void assertEquals(String message, int expectedValue, int actualValue) {
    if (expectedValue != actualValue) {
      throw new AssertionError(
          message + ": expected " + expectedValue + " but got " + actualValue);
    }
  }

  private class Fixture {
    private File directory;
    private File seatsFile;
    private File ticketsFile;
    private File transactionsFile;
    private BookingController controller;

    private Fixture(
        File directory,
        File seatsFile,
        File ticketsFile,
        File transactionsFile,
        BookingController controller) {
      this.directory = directory;
      this.seatsFile = seatsFile;
      this.ticketsFile = ticketsFile;
      this.transactionsFile = transactionsFile;
      this.controller = controller;
    }

    private void close() {
      new File(this.seatsFile.getPath() + ".lock").delete();
      this.seatsFile.delete();
      this.ticketsFile.delete();
      this.transactionsFile.delete();
      this.directory.delete();
    }
  }
}
