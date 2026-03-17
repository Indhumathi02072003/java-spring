package com.ic.attendance_service.repository;

import com.ic.attendance_service.entity.LeaveRequest;
import com.ic.attendance_service.enums.LeaveStatus;
import com.ic.attendance_service.enums.LeaveType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class LeaveRequestRepositoryTest {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Test
    void findByEmpId_shouldReturnLeaves() {

        UUID empId = UUID.randomUUID();

        LeaveRequest leave1 = LeaveRequest.builder()
                .empId(empId)
                .fromDate(LocalDate.now())
                .toDate(LocalDate.now().plusDays(2))
                .leaveType(LeaveType.SICK)
                .status(LeaveStatus.DRAFT)
                .build();

        LeaveRequest leave2 = LeaveRequest.builder()
                .empId(empId)
                .fromDate(LocalDate.now().plusDays(5))
                .toDate(LocalDate.now().plusDays(6))
                .leaveType(LeaveType.CASUAL)
                .status(LeaveStatus.APPROVED)
                .build();

        leaveRequestRepository.save(leave1);
        leaveRequestRepository.save(leave2);

        List<LeaveRequest> result =
                leaveRequestRepository.findByEmpId(empId);

        assertEquals(2, result.size());
    }

    @Test
    void findByEmpId_shouldReturnEmptyList_whenNoLeaves() {

        UUID empId = UUID.randomUUID();

        List<LeaveRequest> result =
                leaveRequestRepository.findByEmpId(empId);

        assertTrue(result.isEmpty());
    }

    @Test
    void findById_shouldReturnLeave() {

        LeaveRequest leave = LeaveRequest.builder()
                .empId(UUID.randomUUID())
                .fromDate(LocalDate.now())
                .toDate(LocalDate.now().plusDays(1))
                .leaveType(LeaveType.SICK)
                .status(LeaveStatus.DRAFT)
                .build();

        LeaveRequest saved = leaveRequestRepository.save(leave);

        Optional<LeaveRequest> result =
                leaveRequestRepository.findById(saved.getId());

        assertTrue(result.isPresent());
    }
}
