package org.stadium.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.stadium.model.BookingTransaction;
import org.stadium.model.Seat;
import org.stadium.model.Ticket;
import org.stadium.model.enums.SeatStatus;
import org.stadium.model.enums.SynchronizationMechanism;
import org.stadium.model.enums.TicketStatus;
import org.stadium.model.enums.TransactionStatus;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.TicketRepository;
import org.stadium.repository.TransactionRepository;

public class BookingControllerTest {

  private int testsRun;
  private int testsPassed;
  private String fanId;
  private String matchId;
  private double ticketPrice;
  private List<Fixture> fixtures = new ArrayList<>();

  public static void main(String[] args) {
    new BookingControllerTest().runTests();
  }

  public void runTests() {
    this.testsRun = 0;
    this.testsPassed = 0;
    this.fixtures = new ArrayList<>();
    this.setUp();
    System.out.println("Running BookingController tests...\n");

    this.runTest("Books one ticket", () -> this.booksOneTicketAndRecordsHistory());
    this.runTest("Books four tickets", () -> this.booksFourTicketsInOneTransaction());
    this.runTest("Rejects an already-booked seat", () -> this.rejectsAlreadyBookedSeat());
    this.runTest("Rejects invalid booking input", () -> this.rejectsInvalidBookingInput());
    this.runTest("Rejects an unknown seat", () -> this.rejectsUnknownSeat());
    this.runTest("Rejects a seat with no available ticket", () -> this.rejectsSeatWithoutAvailableTicket());
    this.runTest(
        "Rejects unsupported synchronization mechanisms",
        () -> this.rejectsUnsupportedSynchronizationMechanism());

    this.cleanUpFixtures();
    System.out.println("\n------------------------------");
    System.out.printf("Passed: %d/%d%n", this.testsPassed, this.testsRun);
    System.out.println("------------------------------");

    if (this.testsPassed != this.testsRun) {
      throw new AssertionError("Some test cases failed");
    }
  }

  private void setUp() {
    this.fanId = "FAN0001";
    this.matchId = "MTH01";
    this.ticketPrice = 100000.0;
  }

  private void booksOneTicketAndRecordsHistory() {
    Fixture fixture = this.createFixture("single-booking", 1, 1);
    fixture.transactionRepository.save(
        new BookingTransaction(
            "TXN00001",
            "FAN0002",
            this.matchId,
            "2026-10-10 10:00:00",
            50000.0,
            1,
            TransactionStatus.SUCCESS));

    boolean booked =
        fixture.controller.bookTickets(
            this.fanId, this.matchId, Collections.singletonList(this.seatId(1)), SynchronizationMechanism.NO_LOCK);

    this.assertTrue("booking result: " + booked, booked);

    Seat seat = fixture.seatRepository.findById(this.seatId(1));
    this.assertEquals("seat status", SeatStatus.BOOKED, seat.getStatus());
    this.assertEquals("seat version", 1, seat.getVersion());

    Ticket ticket = fixture.ticketRepository.findById(this.ticketId(1));
    this.assertEquals("ticket status", TicketStatus.BOOKED, ticket.getStatus());
    this.assertEquals("ticket fan ID", this.fanId, ticket.getFanId());
    this.assertNotNull("ticket transaction ID", ticket.getTransactionId());
    this.assertFalse("ticket booking date is empty", ticket.getBookingDate().isEmpty());

    List<BookingTransaction> history = fixture.controller.viewBookingHistory(this.fanId);
    this.assertEquals("transaction count", 1, history.size());

    BookingTransaction transaction = history.get(0);
    this.assertEquals("transaction fan ID", this.fanId, transaction.getFanId());
    this.assertEquals("transaction match ID", this.matchId, transaction.getMatchId());
    this.assertEquals("ticket count", 1, transaction.getTicketCount());
    this.assertEquals("transaction total", this.ticketPrice, transaction.getTotalAmount(), 0.0);
    this.assertEquals("transaction status", TransactionStatus.SUCCESS, transaction.getStatus());
  }

  private void booksFourTicketsInOneTransaction() {
    Fixture fixture = this.createFixture("four-ticket-booking", 4, 4);
    List<String> requestedSeats = Arrays.asList(this.seatId(1), this.seatId(2), this.seatId(3), this.seatId(4));

    boolean booked =
        fixture.controller.bookTickets(
            this.fanId, this.matchId, requestedSeats, SynchronizationMechanism.NO_LOCK);

    this.assertTrue("booking result: " + booked, booked);
    for (int index = 1; index <= 4; index++) {
      Seat seat = fixture.seatRepository.findById(this.seatId(index));
      Ticket ticket = fixture.ticketRepository.findById(this.ticketId(index));
      this.assertEquals("seat status", SeatStatus.BOOKED, seat.getStatus());
      this.assertEquals("ticket status", TicketStatus.BOOKED, ticket.getStatus());
    }

    List<BookingTransaction> history = fixture.controller.viewBookingHistory(this.fanId);
    this.assertEquals("transaction count", 1, history.size());
    this.assertEquals("ticket count", 4, history.get(0).getTicketCount());
    this.assertEquals("transaction total", 4 * this.ticketPrice, history.get(0).getTotalAmount(), 0.0);
  }

  private void rejectsAlreadyBookedSeat() {
    Fixture fixture = this.createFixture("already-booked", 1, 1);
    Seat seat = fixture.seatRepository.findById(this.seatId(1));
    seat.setStatus(SeatStatus.BOOKED);
    fixture.seatRepository.saveToFile();

    boolean booked =
        fixture.controller.bookTickets(
            this.fanId, this.matchId, Collections.singletonList(this.seatId(1)), SynchronizationMechanism.NO_LOCK);

    this.assertFalse("booking result: " + booked, booked);
    this.assertTrue("transaction count", fixture.transactionRepository.findAll().isEmpty());
    this.assertEquals(
        "ticket status",
        TicketStatus.AVAILABLE,
        fixture.ticketRepository.findById(this.ticketId(1)).getStatus());
  }

  private void rejectsInvalidBookingInput() {
    Fixture fixture = this.createFixture("invalid-input", 5, 5);

    this.assertFalse(
        "null fan ID was accepted",
        fixture.controller.bookTickets(
            null, this.matchId, Collections.singletonList(this.seatId(1)), SynchronizationMechanism.NO_LOCK));
    this.assertFalse(
        "empty fan ID was accepted",
        fixture.controller.bookTickets(
            "", this.matchId, Collections.singletonList(this.seatId(1)), SynchronizationMechanism.NO_LOCK));
    this.assertFalse(
        "null match ID was accepted",
        fixture.controller.bookTickets(
            this.fanId, null, Collections.singletonList(this.seatId(1)), SynchronizationMechanism.NO_LOCK));
    this.assertFalse(
        "empty match ID was accepted",
        fixture.controller.bookTickets(
            this.fanId, "", Collections.singletonList(this.seatId(1)), SynchronizationMechanism.NO_LOCK));
    this.assertFalse(
        "null seatIds was accepted",
        fixture.controller.bookTickets(this.fanId, this.matchId, null, SynchronizationMechanism.NO_LOCK));
    this.assertFalse(
        "empty seatIds was accepted",
        fixture.controller.bookTickets(
            this.fanId, this.matchId, Collections.<String>emptyList(), SynchronizationMechanism.NO_LOCK));
    this.assertFalse(
        "more than four seat IDs were accepted",
        fixture.controller.bookTickets(
            this.fanId,
            this.matchId,
            Arrays.asList(this.seatId(1), this.seatId(2), this.seatId(3), this.seatId(4), this.seatId(5)),
            SynchronizationMechanism.NO_LOCK));
    this.assertFalse(
        "duplicate seat IDs were accepted",
        fixture.controller.bookTickets(
            this.fanId, this.matchId, Arrays.asList(this.seatId(1), this.seatId(1)), SynchronizationMechanism.NO_LOCK));

    this.assertTrue("transaction count", fixture.transactionRepository.findAll().isEmpty());
    for (int index = 1; index <= 5; index++) {
      this.assertEquals(
          "seat status",
          SeatStatus.AVAILABLE,
          fixture.seatRepository.findById(this.seatId(index)).getStatus());
    }
  }

  private void runTest(String name, Runnable testBody) {
    this.testsRun++;
    try {
      testBody.run();
      this.testsPassed++;
      System.out.println("[PASS] " + name);
    } catch (AssertionError | RuntimeException exception) {
      System.err.println("[FAIL] " + name + " -> " + exception.getMessage());
    }
  }

  private void assertTrue(String message, boolean condition) {
    this.assertCondition(condition, message);
  }

  private void assertFalse(String message, boolean condition) {
    this.assertCondition(!condition, message);
  }

  private void assertEquals(String message, Object expectedValue, Object actualValue) {
    boolean equal = expectedValue == null ? actualValue == null : expectedValue.equals(actualValue);
    if (!equal) {
      throw new AssertionError(message + ": " + actualValue);
    }
  }

  private void assertEquals(String message, int expectedValue, int actualValue) {
    if (expectedValue != actualValue) {
      throw new AssertionError(message + ": " + actualValue);
    }
  }

  private void assertEquals(
      String message, double expectedValue, double actualValue, double delta) {
    if (Math.abs(expectedValue - actualValue) > delta) {
      throw new AssertionError(message + ": " + actualValue);
    }
  }

  private void assertNotNull(String message, Object value) {
    if (value == null) {
      throw new AssertionError(message + " is null");
    }
  }

  private void assertCondition(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }

  private void cleanUpFixtures() {
    for (Fixture fixture : this.fixtures) {
      fixture.close();
    }
  }

  private void rejectsUnknownSeat() {
    Fixture fixture = this.createFixture("missing-seat", 1, 1);

    boolean booked =
        fixture.controller.bookTickets(
            this.fanId, this.matchId, Collections.singletonList("UNKNOWN"), SynchronizationMechanism.NO_LOCK);

    this.assertFalse("booking result: " + booked, booked);
    this.assertTrue("transaction count", fixture.transactionRepository.findAll().isEmpty());
  }

  private void rejectsSeatWithoutAvailableTicket() {
    Fixture fixture = this.createFixture("missing-ticket", 1, 0);

    boolean booked =
        fixture.controller.bookTickets(
            this.fanId, this.matchId, Collections.singletonList(this.seatId(1)), SynchronizationMechanism.NO_LOCK);

    this.assertFalse("booking result: " + booked, booked);
    this.assertEquals(
        "seat status",
        SeatStatus.AVAILABLE,
        fixture.seatRepository.findById(this.seatId(1)).getStatus());
    this.assertTrue("transaction count", fixture.transactionRepository.findAll().isEmpty());
  }

  private void rejectsUnsupportedSynchronizationMechanism() {
    Fixture fixture = this.createFixture("unsupported-mechanism", 1, 1);

    try {
      fixture.controller.bookTickets(
          this.fanId,
          this.matchId,
          Collections.singletonList(this.seatId(1)),
          SynchronizationMechanism.SYNCHRONIZED);
      throw new AssertionError("Unsupported mechanism was accepted");
    } catch (UnsupportedOperationException exception) {
      this.assertTrue("transaction count", fixture.transactionRepository.findAll().isEmpty());
    }
  }

  private Fixture createFixture(String name, int seatCount, int ticketCount) {
    try {
      File rootDirectory = new File("target/test-data/booking-controller");
      if (!rootDirectory.mkdirs() && !rootDirectory.isDirectory()) {
        throw new IOException("Could not create test directory: " + rootDirectory);
      }
      File directory = new File(rootDirectory, name);
      if (!directory.mkdirs() && !directory.isDirectory()) {
        throw new IOException("Could not create test directory: " + directory);
      }
      File seatsFile = new File(directory, "seats.csv");
      File ticketsFile = new File(directory, "tickets.csv");
      File transactionsFile = new File(directory, "transactions.csv");

      StringBuilder seatsCsv =
          new StringBuilder("seatId,sectionId,rowNumber,seatNumber,seatType,status,version\n");
      for (int index = 1; index <= seatCount; index++) {
        seatsCsv
            .append(this.seatId(index))
            .append(",SEC01,1,S")
            .append(index)
            .append(",STANDARD,AVAILABLE,0\n");
      }
      this.writeCsv(seatsFile, seatsCsv.toString());

      StringBuilder ticketsCsv =
          new StringBuilder(
              "ticketId,matchId,seatId,transactionId,fanId,price,bookingDate,status\n");
      for (int index = 1; index <= ticketCount; index++) {
        ticketsCsv
            .append(this.ticketId(index))
            .append(',')
            .append(this.matchId)
            .append(',')
            .append(this.seatId(index))
            .append(",,,")
            .append(this.ticketPrice)
            .append(",,AVAILABLE\n");
      }
      this.writeCsv(ticketsFile, ticketsCsv.toString());
      this.writeCsv(
          transactionsFile,
          "transactionId,fanId,matchId,totalAmount,ticketCount,transactionDate,status\n");

      SeatRepository seatRepository = new SeatRepository(seatsFile.getPath());
      TicketRepository ticketRepository = new TicketRepository(ticketsFile.getPath());
      TransactionRepository transactionRepository =
          new TransactionRepository(transactionsFile.getPath());
      BookingController controller =
          new BookingController(seatRepository, ticketRepository, transactionRepository);

      Fixture fixture =
          new Fixture(
              directory,
              seatsFile,
              ticketsFile,
              transactionsFile,
              seatRepository,
              ticketRepository,
              transactionRepository,
              controller);
      this.fixtures.add(fixture);
      return fixture;
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }

  private String seatId(int index) {
    return String.format("SEAT%03d", index);
  }

  private String ticketId(int index) {
    return String.format("TCK%06d", index);
  }

  private void writeCsv(File file, String content) throws IOException {
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(content);
    }
  }

  private class Fixture {
    private File directory;
    private File seatsFile;
    private File ticketsFile;
    private File transactionsFile;
    private SeatRepository seatRepository;
    private TicketRepository ticketRepository;
    private TransactionRepository transactionRepository;
    private BookingController controller;

    private Fixture(
        File directory,
        File seatsFile,
        File ticketsFile,
        File transactionsFile,
        SeatRepository seatRepository,
        TicketRepository ticketRepository,
        TransactionRepository transactionRepository,
        BookingController controller) {
      this.directory = directory;
      this.seatsFile = seatsFile;
      this.ticketsFile = ticketsFile;
      this.transactionsFile = transactionsFile;
      this.seatRepository = seatRepository;
      this.ticketRepository = ticketRepository;
      this.transactionRepository = transactionRepository;
      this.controller = controller;
    }

    private void close() {
      this.seatsFile.delete();
      this.ticketsFile.delete();
      this.transactionsFile.delete();
      this.directory.delete();
    }
  }
}
