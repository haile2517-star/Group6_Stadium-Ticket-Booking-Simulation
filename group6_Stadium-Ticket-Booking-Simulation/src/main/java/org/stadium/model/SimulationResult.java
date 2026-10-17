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

    public SimulationResult() {
    }

    public SimulationResult(String resultId, SynchronizationMechanism mechanism, int threadCount,
                            int totalRequests, int successCount, int failedCount,
                            int doubleBookingCount, double throughput, double doubleBookingRate,
                            long executionTimeMs) {
        this.resultId = resultId;
        this.mechanism = mechanism;
        this.threadCount = threadCount;
        this.totalRequests = totalRequests;
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.doubleBookingCount = doubleBookingCount;
        this.throughput = throughput;
        this.doubleBookingRate = doubleBookingRate;
        this.executionTimeMs = executionTimeMs;
    }

    @Override
    public String getId() {
        return this.resultId;
    }

    @Override
    public String toCsvLine() {
        if (this.resultId == null || this.resultId.isEmpty()) {
            throw new IllegalStateException("resultId must not be null or empty");
        }
        if (this.mechanism == null) {
            throw new IllegalStateException("mechanism must not be null");
        }

        return this.resultId + ","
                + this.mechanism.name() + ","
                + this.threadCount + ","
                + this.totalRequests + ","
                + this.successCount + ","
                + this.failedCount + ","
                + this.doubleBookingCount + ","
                + this.throughput + ","
                + this.doubleBookingRate + ","
                + this.executionTimeMs;
    }

    @Override
    public void fromCsvLine(String line) {
        if (line == null) {
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
            throw new IllegalArgumentException(
                    "Invalid synchronization mechanism: " + parts[1], e
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

        this.resultId = parts[0];
        this.mechanism = parsedMechanism;
        this.threadCount = parsedThreadCount;
        this.totalRequests = parsedTotalRequests;
        this.successCount = parsedSuccessCount;
        this.failedCount = parsedFailedCount;
        this.doubleBookingCount = parsedDoubleBookingCount;
        this.throughput = parsedThroughput;
        this.doubleBookingRate = parsedDoubleBookingRate;
        this.executionTimeMs = parsedExecutionTimeMs;
    }

    public String getResultId() {
        return this.resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public SynchronizationMechanism getMechanism() {
        return this.mechanism;
    }

    public void setMechanism(SynchronizationMechanism mechanism) {
        this.mechanism = mechanism;
    }

    public int getThreadCount() {
        return this.threadCount;
    }

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    public int getTotalRequests() {
        return this.totalRequests;
    }

    public void setTotalRequests(int totalRequests) {
        this.totalRequests = totalRequests;
    }

    public int getSuccessCount() {
        return this.successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailedCount() {
        return this.failedCount;
    }

    public void setFailedCount(int failedCount) {
        this.failedCount = failedCount;
    }

    public int getDoubleBookingCount() {
        return this.doubleBookingCount;
    }

    public void setDoubleBookingCount(int doubleBookingCount) {
        this.doubleBookingCount = doubleBookingCount;
    }

    public double getThroughput() {
        return this.throughput;
    }

    public void setThroughput(double throughput) {
        this.throughput = throughput;
    }

    public double getDoubleBookingRate() {
        return this.doubleBookingRate;
    }

    public void setDoubleBookingRate(double doubleBookingRate) {
        this.doubleBookingRate = doubleBookingRate;
    }

    public long getExecutionTimeMs() {
        return this.executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
}
