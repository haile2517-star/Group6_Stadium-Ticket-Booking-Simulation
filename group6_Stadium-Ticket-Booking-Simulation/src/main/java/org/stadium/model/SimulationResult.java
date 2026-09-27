package org.stadium.model;
import org.stadium.model.enums.SynchronizationMechanism;

public class SimulationResult extends BaseEntity {
    private String resultId;
    private SynchronizationMechanism mechanism;
    private int threadCount;
    private int totalRequests;
    private int successCount;
    private int failedCount;
    private int doubleBookingCount;
    private double throughput;
    private double doubleBookingRate;
    private long executionTimeMs;
    @Override 
    public String getId() {
        return resultId;
    }

    @Override 
    public String toCsvLine() {
        if (resultId == null || resultId.isEmpty()) {
            throw new IllegalStateException("resultId must not be null or empty");   
        }
        if (mechanism == null) {
            throw new IllegalStateException("mechanism must not be null");
        }

        return resultId + "," 
                + mechanism.name() + ","
                + threadCount + ","
                + totalRequests + ","
                + successCount + ","
                + failedCount + ","
                + doubleBookingCount + ","
                + throughput + ","
                + doubleBookingRate + ","
                + executionTimeMs;
    }

    @Override
    public void fromCsvLine(String line) {
        if(line == null) {
            throw new IllegalArgumentException("CSV line must not be null");
        }

        String[] parts = line.split(",", -1);
        if (parts.length != 10) {
            throw new IllegalArgumentException(
                    "SimulationResult CSV row must contain exactly 10 columns; found" + parts.length
            );
        }
        if (parts[0].isEmpty()) {
            throw new IllegalArgumentException("resultId must not be empty");
        }
        
        final SynchronizationMechanism parsedMechanism;
        try {
            parsedMechanism = SynchronizationMechanism.valueOf(parts[1]);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException (
                "Invaled synchronization mechanism: " + parts[1], e 
            );
        } 

        final int parsedThreadCount;
        final int parsedTotalRequests;
        final int parsedSuccessCount;
        final int parsedFailedCount;
        final int parsedDoubleBookingCount;
        final double parsedThroughput;
        final double parsedDoubleBookingRate;
        final long parsedExecutionTimeMs;

        try {
            parsedThreadCount = Integer.parseInt(parts[2]);
            parsedTotalRequests = Integer.parseInt(parts[3]);
            parsedSuccessCount = Integer.parseInt(parts[4]);
            parsedFailedCount = Integer.parseInt(parts[5]);
            parsedDoubleBookingCount = Integer.parseInt(parts[6]);
            parsedThroughput = Double.parseDouble(parts[7]);
            parsedDoubleBookingRate = Double.parseDouble(parts[8]);
            parsedExecutionTimeMs = Long.parseLong(parts[9]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "SimulationResult CSV row contains an invalid numeric value", e
            );
        }

        resultId = parts[0];
        mechanism = parsedMechanism;
        threadCount = parsedThreadCount;
        totalRequests = parsedTotalRequests;
        successCount = parsedSuccessCount;
        failedCount = parsedFailedCount;
        doubleBookingCount = parsedDoubleBookingCount;
        throughput = parsedThroughput;
        doubleBookingRate = parsedDoubleBookingRate;
        executionTimeMs = parsedExecutionTimeMs;
    }
}