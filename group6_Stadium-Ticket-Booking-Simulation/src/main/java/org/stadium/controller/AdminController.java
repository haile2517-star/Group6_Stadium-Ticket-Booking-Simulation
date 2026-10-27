package org.stadium.controller;

import org.stadium.model.AccountSearchCriteria;
import org.stadium.model.AccountStatus;
import org.stadium.model.Fan;
import org.stadium.model.Organizer;
import org.stadium.model.Seat;
import org.stadium.model.SimulationResult;
import org.stadium.model.enums.SeatStatus;
import org.stadium.model.enums.SeatType;
import org.stadium.model.enums.SynchronizationMechanism;
import org.stadium.repository.FanRepository;
import org.stadium.repository.OrganizerRepository;
import org.stadium.repository.SeatRepository;
import org.stadium.repository.SimulationResultRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    private final FanRepository fanRepository;
    private final OrganizerRepository organizerRepository;
    private final SeatRepository seatRepository;
    private final SimulationResultRepository simulationResultRepository;

    public AdminController(FanRepository fanRepository,
                           OrganizerRepository organizerRepository,
                           SeatRepository seatRepository,
                           SimulationResultRepository simulationResultRepository) {
        this.fanRepository = fanRepository;
        this.organizerRepository = organizerRepository;
        this.seatRepository = seatRepository;
        this.simulationResultRepository = simulationResultRepository;
    }

    public AdminController() {
        String pathFans = this.resolvePath("fans.csv");
        String pathOrgs = this.resolvePath("organizers.csv");
        String pathSeats = this.resolvePath("seats.csv");
        String pathSims = this.resolvePath("simulation_results.csv");

        this.fanRepository = new FanRepository(pathFans);
        this.organizerRepository = new OrganizerRepository(pathOrgs);
        this.seatRepository = new SeatRepository(pathSeats);
        this.simulationResultRepository = new SimulationResultRepository(pathSims);
    }

    public String resolvePath(String fileName) {
        if (new File("data/" + fileName).exists()) {
            return "data/" + fileName;
        } else if (new File("../data/" + fileName).exists()) {
            return "../data/" + fileName;
        }
        return "data/" + fileName;
    }

    public boolean approveFanAccount(String fanId) {
        if (fanId == null || fanId.trim().isEmpty()) {
            return false;
        }
        Fan fan = this.fanRepository.findById(fanId.trim());
        if (fan == null) {
            return false;
        }
        fan.setStatus(AccountStatus.ACTIVE);
        this.fanRepository.save(fan);
        this.fanRepository.saveToFile();
        return true;
    }

    public boolean rejectFanAccount(String fanId) {
        if (fanId == null || fanId.trim().isEmpty()) {
            return false;
        }
        Fan fan = this.fanRepository.findById(fanId.trim());
        if (fan == null) {
            return false;
        }
        fan.setStatus(AccountStatus.REJECTED);
        this.fanRepository.save(fan);
        this.fanRepository.saveToFile();
        return true;
    }

    public boolean deleteFanAccount(String fanId) {
        if (fanId == null || fanId.trim().isEmpty()) {
            return false;
        }
        boolean deleted = this.fanRepository.deleteById(fanId.trim());
        if (deleted) {
            this.fanRepository.saveToFile();
        }
        return deleted;
    }

    public List<Fan> searchFanAccounts(AccountSearchCriteria criteria) {
        List<Fan> allFans = this.fanRepository.findAll();
        if (criteria == null) {
            return allFans;
        }
        List<Fan> results = new ArrayList<>();
        for (Fan fan : allFans) {
            if (criteria.matchesCriteria(fan)) {
                results.add(fan);
            }
        }
        return results;
    }

    public boolean approveOrganizerAccount(String orgId) {
        if (orgId == null || orgId.trim().isEmpty()) {
            return false;
        }
        Organizer org = this.organizerRepository.findById(orgId.trim());
        if (org == null) {
            return false;
        }
        org.setStatus(AccountStatus.ACTIVE);
        this.organizerRepository.save(org);
        this.organizerRepository.saveToFile();
        return true;
    }

    public boolean rejectOrganizerAccount(String orgId) {
        if (orgId == null || orgId.trim().isEmpty()) {
            return false;
        }
        Organizer org = this.organizerRepository.findById(orgId.trim());
        if (org == null) {
            return false;
        }
        org.setStatus(AccountStatus.REJECTED);
        this.organizerRepository.save(org);
        this.organizerRepository.saveToFile();
        return true;
    }

    public boolean deleteOrganizerAccount(String orgId) {
        if (orgId == null || orgId.trim().isEmpty()) {
            return false;
        }
        boolean deleted = this.organizerRepository.deleteById(orgId.trim());
        if (deleted) {
            this.organizerRepository.saveToFile();
        }
        return deleted;
    }

    public List<Organizer> searchOrganizerAccounts(AccountSearchCriteria criteria) {
        List<Organizer> allOrgs = this.organizerRepository.findAll();
        if (criteria == null) {
            return allOrgs;
        }
        List<Organizer> results = new ArrayList<>();
        for (Organizer org : allOrgs) {
            if (criteria.matchesCriteria(org)) {
                results.add(org);
            }
        }
        return results;
    }

    public List<Fan> getPendingFans() {
        return this.fanRepository.findByStatus(AccountStatus.PENDING);
    }

    public List<Organizer> getPendingOrganizers() {
        return this.organizerRepository.findPendingOrganizers();
    }

    public boolean createSeat(String sectionId, int row, String num, SeatType type) {
        if (sectionId == null || sectionId.trim().isEmpty() || row <= 0 || num == null || num.trim().isEmpty() || type == null) {
            return false;
        }
        String sec = sectionId.trim();
        String seatNum = num.trim();
        String rowStr = String.format("%02d", row);

        String cleanNum = seatNum.startsWith("S") ? seatNum : "S" + seatNum;
        String formattedSeatNumber = seatNum.contains("R") ? seatNum : ("R" + rowStr + "-" + cleanNum);
        String seatId = sec + "_R" + rowStr + "_" + cleanNum;

        if (this.seatRepository.findById(seatId) != null) {
            return false;
        }

        Seat seat = new Seat(seatId, sec, row, formattedSeatNumber, type, SeatStatus.AVAILABLE, 0);
        boolean saved = this.seatRepository.save(seat);
        if (saved) {
            this.seatRepository.saveToFile();
        }
        return saved;
    }

    public boolean updateSeat(String seatId, SeatType type, SeatStatus status) {
        if (seatId == null || seatId.trim().isEmpty()) {
            return false;
        }
        Seat seat = this.seatRepository.findById(seatId.trim());
        if (seat == null) {
            return false;
        }
        if (type != null) {
            seat.setSeatType(type);
        }
        if (status != null) {
            seat.setStatus(status);
        }
        seat.incrementVersion();
        this.seatRepository.save(seat);
        this.seatRepository.saveToFile();
        return true;
    }

    public boolean deleteSeat(String seatId) {
        if (seatId == null || seatId.trim().isEmpty()) {
            return false;
        }
        boolean deleted = this.seatRepository.deleteById(seatId.trim());
        if (deleted) {
            this.seatRepository.saveToFile();
        }
        return deleted;
    }

    public List<Seat> searchSeat(String sectionId, SeatType type, SeatStatus status) {
        List<Seat> allSeats = this.seatRepository.findAll();
        List<Seat> results = new ArrayList<>();
        for (Seat seat : allSeats) {
            if (sectionId != null && !sectionId.trim().isEmpty()) {
                if (!sectionId.trim().equalsIgnoreCase(seat.getSectionId())) {
                    continue;
                }
            }
            if (type != null && seat.getSeatType() != type) {
                continue;
            }
            if (status != null && seat.getStatus() != status) {
                continue;
            }
            results.add(seat);
        }
        return results;
    }

    public SimulationResult runConcurrencyBenchmark(SynchronizationMechanism mech, int threads, int reqs) {
        System.out.println("[INFO] Benchmark feature is scheduled for Week 8 milestone (Coming Soon).");
        return null;
    }

    public List<SimulationResult> viewBenchmarkReports() {
        return this.simulationResultRepository.findAll();
    }

    public FanRepository getFanRepository() {
        return this.fanRepository;
    }

    public OrganizerRepository getOrganizerRepository() {
        return this.organizerRepository;
    }

    public SeatRepository getSeatRepository() {
        return this.seatRepository;
    }

    public SimulationResultRepository getSimulationResultRepository() {
        return this.simulationResultRepository;
    }
}
