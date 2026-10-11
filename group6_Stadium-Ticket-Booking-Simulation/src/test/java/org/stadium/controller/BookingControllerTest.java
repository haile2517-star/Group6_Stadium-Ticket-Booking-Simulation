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

  private static int testsRun;
  private static int testsPassed;
  private static String fanId;
  private static String matchId;
  private static double ticketPrice;
  private static List<Fixture> fixtures = new ArrayList<>();

  public static void main(String[] args) {
    runTests();
  }

  private static void runTests() {
    testsRun = 0;
    testsPassed = 0;
    fixtures = new ArrayList<>();
    setUp();
    System.out.println("Running BookingController tests...\n");

    runTest("Books one ticket", () -> booksOneTicketAndRecordsHistory());
    runTest("Books four tickets", () -> booksFourTicketsInOneTransaction());
    runTest("Rejects an already-booked seat", () -> rejectsAlreadyBookedSeat());
    runTest("Rejects invalid booking input", () -> rejectsInvalidBookingInput());
    runTest("Rejects an unknown seat", () -> rejectsUnknownSeat());
    runTest("Rejects a seat with no available ticket", () -> rejectsSeatWithoutAvailableTicket());
    runTest(
        "Rejects unsupported synchronization mechanisms",
        () -> rejectsUnsupportedSynchronizationMechanism());

    cleanUpFixtures();
    System.out.println("\n------------------------------");
    System.out.printf("Passed: %d/%d%n", testsPassed, testsRun);
    System.out.println("------------------------------");

    if (testsPassed != testsRun) {
      throw new AssertionError("Some test cases failed");
    }
  }

  private static void setUp() {
    fanId = "FAN0001";
    matchId = "MTH01";
    ticketPrice = 100000.0;
  }

  private static void booksOneTicketAndRecordsHistory() {
    Fixture fixture = createFixture("single-booking", 1, 1);
    fixture.transactionRepository.save(
        new BookingTransaction(
            "TXN00001",
            "FAN0002",
            matchId,
            "2026-10-10 10:00:00",
            50000.0,
            1,
            TransactionStatus.SUCCESS));

    boolean booked =
        fixture.controller.bookTickets(
            fanId, matchId, Collections.singletonList(seatId(1)), SynchronizationMechanism.NO_LOCK);

    assertTrue("booking result: " + booked, booked);

    Seat seat = fixture.seatRepository.findById(seatId(1));
    assertEquals("seat status", SeatStatus.BOOKED, seat.getStatus());
    assertEquals("seat version", 1, seat.getVersion());

    Ticket ticket = fixture.ticketRepository.findById(ticketId(1));
    assertEquals("ticket status", TicketStatus.BOOKED, ticket.getStatus());
    assertEquals("ticket fan ID", fanId, ticket.getFanId());
    assertNotNull("ticket transaction ID", ticket.getTransactionId());
    assertFalse("ticket booking date is empty", ticket.getBookingDate().isEmpty());

    List<BookingTransaction> history = fixture.controller.viewBookingHistory(fanId);
    assertEquals("transaction count", 1, history.size());

    BookingTransaction transaction = history.get(0);
    assertEquals("transaction fan ID", fanId, transaction.getFanId());
    assertEquals("transaction match ID", matchId, transaction.getMatchId());
    assertEquals("ticket count", 1, transaction.getTicketCount());
    assertEquals("transaction total", ticketPrice, transaction.getTotalAmount(), 0.0);
    assertEquals("transaction status", TransactionStatus.SUCCESS, transaction.getStatus());
  }

  private static void booksFourTicketsInOneTransaction() {
    Fixture fixture = createFixture("four-ticket-booking", 4, 4);
    List<String> requestedSeats = Arrays.asList(seatId(1), seatId(2), seatId(3), seatId(4));

    boolean booked =
        fixture.controller.bookTickets(
            fanId, matchId, requestedSeats, SynchronizationMechanism.NO_LOCK);

    assertTrue("booking result: " + booked, booked);
    for (int index = 1; index <= 4; index++) {
      Seat seat = fixture.seatRepository.findById(seatId(index));
      Ticket ticket = fixture.ticketRepository.findById(ticketId(index));
      assertEquals("seat status", SeatStatus.BOOKED, seat.getStatus());
      assertEquals("ticket status", TicketStatus.BOOKED, ticket.getStatus());
    }

    List<BookingTransaction> history = fixture.controller.viewBookingHistory(fanId);
    assertEquals("transaction count", 1, history.size());
    assertEquals("ticket count", 4, history.get(0).getTicketCount());
    assertEquals("transaction total", 4 * ticketPrice, history.get(0).getTotalAmount(), 0.0);
  }

  private static void rejectsAlreadyBookedSeat() {
    Fixture fixture = createFixture("already-booked", 1, 1);
    Seat seat = fixture.seatRepository.findById(seatId(1));
    seat.setStatus(SeatStatus.BOOKED);
    fixture.seatRepository.saveToFile();

    boolean booked =
        fixture.controller.bookTickets(
            fanId, matchId, Collections.singletonList(seatId(1)), SynchronizationMechanism.NO_LOCK);

    assertFalse("booking result: " + booked, booked);
    assertTrue("transaction count", fixture.transactionRepository.findAll().isEmpty());
    assertEquals(
        "ticket status",
        TicketStatus.AVAILABLE,
        fixture.ticketRepository.findById(ticketId(1)).getStatus());
  }

  private static void rejectsInvalidBookingInput() {
    Fixture fixture = createFixture("invalid-input", 5, 5);

    assertFalse(
        "null fan ID was accepted",
        fixture.controller.bookTickets(
            null, matchId, Collections.singletonList(seatId(1)), SynchronizationMechanism.NO_LOCK));
    assertFalse(
        "empty fan ID was accepted",
        fixture.controller.bookTickets(
            "", matchId, Collections.singletonList(seatId(1)), SynchronizationMechanism.NO_LOCK));
    assertFalse(
        "null match ID was accepted",
        fixture.controller.bookTickets(
            fanId, null, Collections.singletonList(seatId(1)), SynchronizationMechanism.NO_LOCK));
    assertFalse(
        "empty match ID was accepted",
        fixture.controller.bookTickets(
            fanId, "", Collections.singletonList(seatId(1)), SynchronizationMechanism.NO_LOCK));
    assertFalse(
        "null seatIds was accepted",
        fixture.controller.bookTickets(fanId, matchId, null, SynchronizationMechanism.NO_LOCK));
    assertFalse(
        "empty seatIds was accepted",
        fixture.controller.bookTickets(
            fanId, matchId, Collections.<String>emptyList(), SynchronizationMechanism.NO_LOCK));
    assertFalse(
        "more than four seat IDs were accepted",
        fixture.controller.bookTickets(
            fanId,
            matchId,
            Arrays.asList(seatId(1), seatId(2), seatId(3), seatId(4), seatId(5)),
            SynchronizationMechanism.NO_LOCK));
    assertFalse(
        "duplicate seat IDs were accepted",
        fixture.controller.bookTickets(
            fanId, matchId, Arrays.asList(seatId(1), seatId(1)), SynchronizationMechanism.NO_LOCK));

    assertTrue("transaction count", fixture.transactionRepository.findAll().isEmpty());
    for (int index = 1; index <= 5; index++) {
      assertEquals(
          "seat status",
          SeatStatus.AVAILABLE,
          fixture.seatRepository.findById(seatId(index)).getStatus());
    }
  }

  private static void runTest(String name, Runnable testBody) {
    testsRun++;
    try {
      testBody.run();
      testsPassed++;
      System.out.println("[PASS] " + name);
    } catch (AssertionError | RuntimeException exception) {
      System.err.println("[FAIL] " + name + " -> " + exception.getMessage());
    }
  }

  private static void assertTrue(String message, boolean condition) {
    assertCondition(condition, message);
  }

  private static void assertFalse(String message, boolean condition) {
    assertCondition(!condition, message);
  }

  private static void assertEquals(String message, Object expectedValue, Object actualValue) {
    boolean equal = expectedValue == null ? actualValue == null : expectedValue.equals(actualValue);
    if (!equal) {
      throw new AssertionError(message + ": " + actualValue);
    }
  }

  private static void assertEquals(String message, int expectedValue, int actualValue) {
    if (expectedValue != actualValue) {
      throw new AssertionError(message + ": " + actualValue);
    }
  }

  private static void assertEquals(
      String message, double expectedValue, double actualValue, double delta) {
    if (Math.abs(expectedValue - actualValue) > delta) {
      throw new AssertionError(message + ": " + actualValue);
    }
  }

  private static void assertNotNull(String message, Object value) {
    if (value == null) {
      throw new AssertionError(message + " is null");
    }
  }

  private static void assertCondition(boolean condition, String message) {
    if (!condition) {
      throw new AssertionError(message);
    }
  }

  private static void cleanUpFixtures() {
    for (Fixture fixture : fixtures) {
      fixture.close();
    }
  }

  private static void rejectsUnknownSeat() {
    Fixture fixture = createFixture("missing-seat", 1, 1);

    boolean booked =
        fixture.controller.bookTickets(
            fanId, matchId, Collections.singletonList("UNKNOWN"), SynchronizationMechanism.NO_LOCK);

    assertFalse("booking result: " + booked, booked);
    assertTrue("transaction count", fixture.transactionRepository.findAll().isEmpty());
  }

  private static void rejectsSeatWithoutAvailableTicket() {
    Fixture fixture = createFixture("missing-ticket", 1, 0);

    boolean booked =
        fixture.controller.bookTickets(
            fanId, matchId, Collections.singletonList(seatId(1)), SynchronizationMechanism.NO_LOCK);

    assertFalse("booking result: " + booked, booked);
    assertEquals(
        "seat status",
        SeatStatus.AVAILABLE,
        fixture.seatRepository.findById(seatId(1)).getStatus());
    assertTrue("transaction count", fixture.transactionRepository.findAll().isEmpty());
  }

  private static void rejectsUnsupportedSynchronizationMechanism() {
    Fixture fixture = createFixture("unsupported-mechanism", 1, 1);

    try {
      fixture.controller.bookTickets(
          fanId,
          matchId,
          Collections.singletonList(seatId(1)),
          SynchronizationMechanism.SYNCHRONIZED);
      throw new AssertionError("Unsupported mechanism was accepted");
    } catch (UnsupportedOperationException exception) {
      assertTrue("transaction count", fixture.transactionRepository.findAll().isEmpty());
    }
  }

  private static Fixture createFixture(String name, int seatCount, int ticketCount) {
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
            .append(seatId(index))
            .append(",SEC01,1,S")
            .append(index)
            .append(",STANDARD,AVAILABLE,0\n");
      }
      writeCsv(seatsFile, seatsCsv.toString());

      StringBuilder ticketsCsv =
          new StringBuilder(
              "ticketId,matchId,seatId,transactionId,fanId,price,bookingDate,status\n");
      for (int index = 1; index <= ticketCount; index++) {
        ticketsCsv
            .append(ticketId(index))
            .append(',')
            .append(matchId)
            .append(',')
            .append(seatId(index))
            .append(",,,")
            .append(ticketPrice)
            .append(",,AVAILABLE\n");
      }
      writeCsv(ticketsFile, ticketsCsv.toString());
      writeCsv(
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
      fixtures.add(fixture);
      return fixture;
    } catch (IOException exception) {
      throw new UncheckedIOException(exception);
    }
  }

  private static String seatId(int index) {
    return String.format("SEAT%03d", index);
  }

  private static String ticketId(int index) {
    return String.format("TCK%06d", index);
  }

  private static void writeCsv(File file, String content) throws IOException {
    try (FileWriter writer = new FileWriter(file)) {
      writer.write(content);
    }
  }

  private static class Fixture {
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
      seatsFile.delete();
      ticketsFile.delete();
      transactionsFile.delete();
      directory.delete();
    }
  }
}
