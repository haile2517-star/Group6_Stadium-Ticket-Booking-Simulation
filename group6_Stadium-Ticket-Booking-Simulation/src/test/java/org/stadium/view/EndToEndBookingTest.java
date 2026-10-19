package org.stadium.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import org.stadium.controller.AuthController;
import org.stadium.controller.BookingController;
import org.stadium.controller.StadiumController;
import org.stadium.model.Account;
import org.stadium.model.SeatSection;
import org.stadium.repository.AdminRepository;
import org.stadium.repository.FanRepository;
import org.stadium.repository.OrganizerRepository;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.SectionRepository;
import org.stadium.repository.StadiumRepository;
import org.stadium.repository.TicketRepository;
import org.stadium.repository.TransactionRepository;

public class EndToEndBookingTest {

    private int testsRun;
    private int testsPassed;

    public static void main(String[] args) {
        new EndToEndBookingTest().runTests();
    }

    public void runTests() {
        this.testsRun = 0;
        this.testsPassed = 0;

        this.runTest(
                "Logs in, displays stadium seating, and books a ticket",
                () -> this.completesAvailableUiBookingFlow());
        this.runTest(
                "Rejects invalid login",
                () -> this.rejectsInvalidLogin());
        this.runTest(
                "Rejects duplicate seat input without changing CSV data",
                () -> this.rejectsDuplicateSeatInput());

        System.out.println();
        System.out.printf("Passed: %d/%d%n", this.testsPassed, this.testsRun);

        if (this.testsPassed != this.testsRun) {
            throw new AssertionError("Some end-to-end booking tests failed");
        }
    }

    private void completesAvailableUiBookingFlow() {
        Fixture fixture = this.createFixture();
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        PrintStream testOutput = this.createPrintStream(capturedOutput);
        String consoleOutput;

        try {
            System.setOut(testOutput);

            String input = "fan.one\nsecret\nFAN0001\nMTH01\nSEAT001\n";
            try (Scanner scanner = this.createScanner(input)) {
                AuthView authView = new AuthView(fixture.authController, scanner);
                authView.displayLoginForm();

                Account fan = fixture.authController.getCurrentUser();
                this.assertTrue("Fan login did not establish a session", fan != null);
                this.assertEquals("Logged-in fan ID", "FAN0001", fan.getId());

                fixture.stadiumView.displayStadiumList(
                        fixture.stadiumController.getAllStadiums());

                List<SeatSection> sections =
                        fixture.stadiumController.getSections("STD01");
                fixture.stadiumView.displaySectionList("STD01", sections);
                this.assertEquals(
                        "Expected one section for the test stadium",
                        1,
                        sections.size());

                SeatSection selectedSection = sections.get(0);
                String seatMap = fixture.stadiumController.buildSeatMap(
                        selectedSection.getSectionId());
                fixture.stadiumView.displaySeatMap(seatMap);
                this.assertContains(
                        "Available seat was not shown in the seat map",
                        seatMap,
                        "[O]");

                BookingView bookingView =
                        new BookingView(scanner, fixture.bookingController);
                bookingView.displayBookingForm(
                        fixture.stadiumController.getAvailableSeats(
                                selectedSection.getSectionId()));
            }

            List<String> seatRows = this.readCsv(fixture.seatsFile);
            List<String> ticketRows = this.readCsv(fixture.ticketsFile);
            List<String> transactionRows = this.readCsv(fixture.transactionsFile);

            String seatRow = this.findRow(seatRows, "SEAT001,");
            String ticketRow = this.findRow(ticketRows, "TCK000001,");
            String transactionRow = this.findRow(transactionRows, "TXN00001,");

            this.assertContains("Seat CSV was not updated", seatRow, ",BOOKED,1");
            this.assertContains(
                    "Ticket CSV was not updated",
                    ticketRow,
                    ",MTH01,SEAT001,TXN00001,FAN0001,125000.0,");
            this.assertContains(
                    "Ticket status was not updated",
                    ticketRow,
                    ",BOOKED");
            this.assertContains(
                    "Transaction CSV has incorrect booking details",
                    transactionRow,
                    "TXN00001,FAN0001,MTH01,125000.0,1,");
            this.assertContains(
                    "Transaction was not marked successful",
                    transactionRow,
                    ",SUCCESS");
            this.assertEquals(
                    "Unexpected number of transaction rows",
                    2,
                    transactionRows.size());

            consoleOutput =
                    new String(capturedOutput.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOutput);
            testOutput.close();
            fixture.close();
        }

        this.assertContains(
                "Login success was not displayed",
                consoleOutput,
                "Đăng nhập thành công!");
        this.assertContains(
                "Booking success was not displayed",
                consoleOutput,
                "[SUCCESS]");
        this.assertContains(
                "Receipt was not displayed",
                consoleOutput,
                "Booking completed successfully!");
        this.assertContains(
                "Booking history was not displayed",
                consoleOutput,
                "BOOKING HISTORY");
    }

    private void rejectsInvalidLogin() {
        Fixture fixture = this.createFixture();
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        PrintStream testOutput = this.createPrintStream(capturedOutput);
        String consoleOutput;

        try {
            System.setOut(testOutput);

            try (Scanner scanner = this.createScanner("fan.one\nwrong-password\n")) {
                AuthView authView = new AuthView(fixture.authController, scanner);
                authView.displayLoginForm();
            }

            this.assertTrue(
                    "Invalid login unexpectedly established a session",
                    fixture.authController.getCurrentUser() == null);
            consoleOutput =
                    new String(capturedOutput.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOutput);
            testOutput.close();
            fixture.close();
        }

        this.assertContains(
                "Invalid login was not reported",
                consoleOutput,
                "Đăng nhập thất bại!");
    }

    private void rejectsDuplicateSeatInput() {
        Fixture fixture = this.createFixture();
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        PrintStream originalOutput = System.out;
        PrintStream testOutput = this.createPrintStream(capturedOutput);
        String consoleOutput;

        try {
            System.setOut(testOutput);

            String input = "fan.one\nsecret\nFAN0001\nMTH01\nSEAT001,SEAT001\n";
            try (Scanner scanner = this.createScanner(input)) {
                AuthView authView = new AuthView(fixture.authController, scanner);
                authView.displayLoginForm();

                BookingView bookingView =
                        new BookingView(scanner, fixture.bookingController);
                bookingView.displayBookingForm(
                        fixture.stadiumController.getAvailableSeats("SEC01"));
            }

            this.assertEquals(
                    "Invalid input unexpectedly created a transaction", 1, this.readCsv(fixture.transactionsFile).size());
            this.assertContains(
                    "Invalid input unexpectedly booked the seat",
                    this.findRow(this.readCsv(fixture.seatsFile), "SEAT001,"), ",AVAILABLE,0");
            this.assertContains(
                    "Invalid input unexpectedly booked the ticket",
                    this.findRow(this.readCsv(fixture.ticketsFile), "TCK000001,"), ",AVAILABLE");

            consoleOutput =
                    new String(capturedOutput.toByteArray(), StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOutput);
            testOutput.close();
            fixture.close();
        }

        this.assertContains(
                "Duplicate seat input was not rejected",
                consoleOutput,
                "[FAILED]");
        this.assertContains(
                "Duplicate seat validation message was not displayed",
                consoleOutput,
                "Duplicate seat IDs are not allowed.");
    }

    private void runTest(String name, Runnable testCase) {
        this.testsRun++;

        try {
            testCase.run();
            this.testsPassed++;
            System.out.println("[PASS] " + name);
        } catch (AssertionError | RuntimeException exception) {
            System.err.println("[FAIL] " + name + " -> " + exception.getMessage());
        }
    }

    private Fixture createFixture() {
        try {
            Path directory = Files.createTempDirectory("stadium-booking-e2e-");
            Path fansFile = directory.resolve("fans.csv");
            Path organizersFile = directory.resolve("organizers.csv");
            Path adminsFile = directory.resolve("admins.csv");
            Path stadiumsFile = directory.resolve("stadiums.csv");
            Path sectionsFile = directory.resolve("sections.csv");
            Path seatsFile = directory.resolve("seats.csv");
            Path ticketsFile = directory.resolve("tickets.csv");
            Path transactionsFile = directory.resolve("transactions.csv");

            this.writeCsv(
                fansFile,
                "fanId,username,passwordHash,fullName,email,phone,fanType,status\n"
                + "FAN0001,fan.one,hash_pass_secret,Test Fan,"
                + "fan@example.com,0123456789,STANDARD,ACTIVE\n");
            this.writeCsv(
                organizersFile,
                "organizerId,username,passwordHash,orgName,email,phone,status\n");
            this.writeCsv(
                adminsFile,
                "adminId,username,passwordHash,fullName,email,phone,status\n");
            this.writeCsv(
                stadiumsFile,
                "stadiumId,stadiumName,address,capacity\n"
                + "STD01,Test Stadium,Test Address,2\n");
            this.writeCsv(
                sectionsFile,
                "sectionId,stadiumId,sectionName,sectionType,totalRows,seatsPerRow\n"
                + "SEC01,STD01,Main Stand,STANDARD,1,2\n");
            this.writeCsv(
                seatsFile,
                "seatId,sectionId,rowNumber,seatNumber,seatType,status,version\n"
                + "SEAT001,SEC01,1,S1,STANDARD,AVAILABLE,0\n"
                + "SEAT002,SEC01,1,S2,STANDARD,AVAILABLE,0\n");
            this.writeCsv(
                ticketsFile,
                "ticketId,matchId,seatId,transactionId,fanId,price,bookingDate,status\n"
                + "TCK000001,MTH01,SEAT001,,,125000.0,,AVAILABLE\n"
                + "TCK000002,MTH01,SEAT002,,,125000.0,,AVAILABLE\n");
            this.writeCsv(
                transactionsFile,
                "transactionId,fanId,matchId,totalAmount,ticketCount,"
                + "transactionDate,status\n");

            FanRepository fanRepository = new FanRepository(fansFile.toString());
            OrganizerRepository organizerRepository = new OrganizerRepository(organizersFile.toString());
            AdminRepository adminRepository = new AdminRepository(adminsFile.toString());
            AuthController authController = new AuthController(fanRepository, organizerRepository, adminRepository);
            StadiumRepository stadiumRepository = new StadiumRepository(stadiumsFile.toString());
            SectionRepository sectionRepository = new SectionRepository(sectionsFile.toString());
            SeatRepository seatRepository = new SeatRepository(seatsFile.toString());
            StadiumController stadiumController = new StadiumController(stadiumRepository, sectionRepository, seatRepository);
            TicketRepository ticketRepository = new TicketRepository(ticketsFile.toString());
            TransactionRepository transactionRepository = new TransactionRepository(transactionsFile.toString());
            BookingController bookingController = new BookingController(seatRepository, ticketRepository, transactionRepository);

            return new Fixture(
                directory,
                fansFile,
                organizersFile,
                adminsFile,
                stadiumsFile,
                sectionsFile,
                seatsFile,
                ticketsFile,
                transactionsFile,
                authController,
                stadiumController,
                bookingController,
                new StadiumView());
        } catch (IOException exception) {
                throw new UncheckedIOException("Could not create end to end test fixture", exception);
        }
    }

    private Scanner createScanner(String input) {
        return new Scanner(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8.name());
    }

    private PrintStream createPrintStream(ByteArrayOutputStream output) {
        try {
                return new PrintStream(output, true, StandardCharsets.UTF_8.name());
        } catch (IOException exception) {
                throw new UncheckedIOException("Could not capture coonsole output", exception);
        }
    }

    private void writeCsv(Path file, String contents) throws IOException {
        Files.write(file, contents.getBytes(StandardCharsets.UTF_8));
    }

    private List<String> readCsv(Path file) {
        try {
                return Files.readAllLines(file, StandardCharsets.UTF_8);
        } catch (IOException exception) {
                throw new UncheckedIOException("Could not read test CSV: " + file, exception);
        }
    }

    private String findRow(List<String> rows, String prefix) {
        for (int index = 1; index < rows.size(); index++) {
                if (rows.get(index).startsWith(prefix)) {
                        return rows.get(index);
                }
        }
        return null;
    }

    private void assertTrue(String message, boolean condition) {
        if(!condition) {
                throw new AssertionError(message);
        }
    }

    private void assertEquals(String message, Object expectedValue, Object actualValue) {
        boolean equal = expectedValue == null ? actualValue == null : expectedValue.equals(actualValue);

        if (!equal) {
                throw new AssertionError(message + ": expected " + expectedValue + ", got " + actualValue);
        }
    }

    private void assertContains(String message, String actualValue, String expectedText) {
        if (actualValue == null || !actualValue.contains(expectedText)) {
                throw new AssertionError(message + ": " + actualValue);
        }
    }

    private class Fixture {
        private Path directory;
        private Path fansFile;
        private Path organizersFile;
        private Path adminsFile;
        private Path stadiumsFile;
        private Path sectionsFile;
        private Path seatsFile;
        private Path ticketsFile;
        private Path transactionsFile;
        private AuthController authController;
        private StadiumController stadiumController;
        private BookingController bookingController;
        private StadiumView stadiumView;

        private Fixture(
                Path directory,
                Path fansFile,
                Path organizersFile,
                Path adminsFile,
                Path stadiumsFile,
                Path sectionsFile,
                Path seatsFile,
                Path ticketsFile,
                Path transactionsFile,
                AuthController authController,
                StadiumController stadiumController,
                BookingController bookingController,
                StadiumView stadiumView) {
                        this.directory = directory;
                        this.fansFile = fansFile;
                        this.organizersFile = organizersFile;
                        this.adminsFile = adminsFile;
                        this.stadiumsFile = stadiumsFile;
                        this.sectionsFile = sectionsFile;
                        this.seatsFile = seatsFile;
                        this.ticketsFile = ticketsFile;
                        this.transactionsFile = transactionsFile;
                        this.authController = authController;
                        this.stadiumController = stadiumController;
                        this.bookingController = bookingController;
                        this.stadiumView = stadiumView;
                }

                private void close() {
                        try {
                                Files.deleteIfExists(this.fansFile);
                                Files.deleteIfExists(this.organizersFile);
                                Files.deleteIfExists(this.adminsFile);
                                Files.deleteIfExists(this.stadiumsFile);
                                Files.deleteIfExists(this.sectionsFile);
                                Files.deleteIfExists(this.seatsFile);
                                Files.deleteIfExists(this.ticketsFile);
                                Files.deleteIfExists(this.transactionsFile);
                                Files.deleteIfExists(this.directory);
                        } catch (IOException exception) {
                                throw new UncheckedIOException("Could not clean up end to end test fixture", exception);
                        }
                }
    }
}
