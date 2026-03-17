package com.ic.attendance_service.service;

import com.ic.attendance_service.dto.ApplyLeaveRequestDTO;
import com.ic.attendance_service.dto.LeaveRequestResponseDTO;
import com.ic.attendance_service.entity.LeaveRequest;
import com.ic.attendance_service.enums.LeaveStatus;
import com.ic.attendance_service.repository.LeaveRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class LeaveService {
    private static final Logger log = LoggerFactory.getLogger(LeaveService.class);


    private final LeaveRequestRepository leaveRequestRepository;

    public LeaveService(LeaveRequestRepository leaveRequestRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
    }

    // APPLY LEAVE
    @CacheEvict(value = "leaveByEmp", key = "#empId")
    public LeaveRequestResponseDTO applyLeave(UUID empId,  ApplyLeaveRequestDTO request)
    {
        LeaveRequest leave = LeaveRequest.builder()
                .empId(empId)
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .leaveType(request.getLeaveType())
                .status(LeaveStatus.DRAFT)
                .build();


        LeaveRequest saved = leaveRequestRepository.save(leave);

        return mapToDTO(saved);
    }

    // GET LEAVES BY EMP
    @Cacheable(value = "leaveByEmp", key = "#empId")
    public List<LeaveRequestResponseDTO> getLeaves(UUID empId) {
        return leaveRequestRepository.findByEmpId(empId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private LeaveRequestResponseDTO mapToDTO(LeaveRequest request) {
        return new LeaveRequestResponseDTO(
                request.getId(),
                request.getEmpId(),
                request.getFromDate(),
                request.getToDate(),
                request.getLeaveType(),
                request.getStatus()

        );
    }

    // APPROVE LEAVE
    @CacheEvict(value = "leaveByEmp", allEntries = true)
    public LeaveRequestResponseDTO approveLeave(UUID leaveId) {

        LeaveRequest request = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        request.setStatus(LeaveStatus.APPROVED);

        LeaveRequest saved = leaveRequestRepository.save(request);
        return mapToDTO(saved);
    }

    // REJECT LEAVE
    @CacheEvict(value = "leaveByEmp", allEntries = true)
    public LeaveRequestResponseDTO rejectLeave(UUID leaveId) {

        LeaveRequest request = leaveRequestRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        request.setStatus(LeaveStatus.REJECTED);

        LeaveRequest saved = leaveRequestRepository.save(request);
        return mapToDTO(saved);
    }

}

