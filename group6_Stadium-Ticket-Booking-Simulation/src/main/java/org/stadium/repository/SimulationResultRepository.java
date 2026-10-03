package org.stadium.repository;

import org.stadium.model.SimulationResult;
import org.stadium.model.enums.SynchronizationMechanism;
import java.util.List;

public class SimulationResultRepository extends CsvRepository<SimulationResult> {
    public SimulationResultRepository(String filePath) {
        super(filePath);
    }

@Override
public SimulationResult createEntity() {
    return new SimulationResult();
}

@Override 
public String getHeader() {
    return "resultId,mechanism,threadCount,totalRequests,successCount,failedCount,doubleBookingCount,throughput,doubleBookingRate,executionTimeMs";
}

public List<SimulationResult> findByMechanism(SynchronizationMechanism mechanism) {
    return findByCondition(result -> result.getMechanism() == mechanism);
}

public SimulationResult getLatestResult() {
    List<SimulationResult> results = findAll();
    if(results.isEmpty()) {
        return null;
    }
    return results.get(results.size() - 1);
}
}