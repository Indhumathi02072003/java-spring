package com.ic.attendance_service.service;

import com.ic.attendance_service.dto.ApplyLeaveRequestDTO;
import com.ic.attendance_service.dto.LeaveRequestResponseDTO;
import com.ic.attendance_service.entity.LeaveRequest;
import com.ic.attendance_service.enums.LeaveStatus;
import com.ic.attendance_service.enums.LeaveType;
import com.ic.attendance_service.repository.LeaveRequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @InjectMocks
    private LeaveService leaveService;

    @Test
    void applyLeave_shouldCreateDraftLeave_whenValidRequest() {

        UUID empId = UUID.randomUUID();

        ApplyLeaveRequestDTO request = new ApplyLeaveRequestDTO();
        request.setFromDate(LocalDate.now());
        request.setToDate(LocalDate.now().plusDays(2));
        request.setLeaveType(LeaveType.SICK);

        when(leaveRequestRepository.save(any(LeaveRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        LeaveRequestResponseDTO response =
                leaveService.applyLeave(empId, request);

        assertEquals(empId, response.getEmpId());


        assertEquals(LeaveStatus.DRAFT, response.getStatus());
        assertEquals(LeaveType.SICK, response.getLeaveType());
    }

    @Test
    void applyLeave_shouldCreateDraftLeave() {

        UUID empId = UUID.randomUUID();

        ApplyLeaveRequestDTO request = new ApplyLeaveRequestDTO();
        request.setFromDate(LocalDate.now());
        request.setToDate(LocalDate.now().plusDays(2));
        request.setLeaveType(LeaveType.SICK);

        LeaveRequest saved = LeaveRequest.builder()
                .id(UUID.randomUUID())
                .empId(empId)
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .leaveType(request.getLeaveType())
                .status(LeaveStatus.DRAFT)
                .build();

        when(leaveRequestRepository.save(any(LeaveRequest.class)))
                .thenReturn(saved);

        LeaveRequestResponseDTO response =
                leaveService.applyLeave(empId, request);

        assertEquals(LeaveStatus.DRAFT, response.getStatus());
        assertEquals(LeaveType.SICK, response.getLeaveType());
    }

    @Test
    void applyLeave_shouldFail_whenLeaveTypeIsNull() {

        UUID empId = UUID.randomUUID();

        ApplyLeaveRequestDTO request = new ApplyLeaveRequestDTO();
        request.setFromDate(LocalDate.now());
        request.setToDate(LocalDate.now().plusDays(1));

        assertThrows(NullPointerException.class,
                () -> leaveService.applyLeave(empId, request));
    }

    @Test
    void applyLeave_shouldFail_whenFromDateIsNull() {

        UUID empId = UUID.randomUUID();

        ApplyLeaveRequestDTO request = new ApplyLeaveRequestDTO();
        request.setToDate(LocalDate.now().plusDays(1));
        request.setLeaveType(LeaveType.SICK);

        assertThrows(RuntimeException.class,
                () -> leaveService.applyLeave(empId, request));
    }

    @Test
    void applyLeave_shouldFail_whenToDateIsNull() {

        UUID empId = UUID.randomUUID();

        ApplyLeaveRequestDTO request = new ApplyLeaveRequestDTO();
        request.setFromDate(LocalDate.now());
        request.setLeaveType(LeaveType.SICK);

        assertThrows(RuntimeException.class,
                () -> leaveService.applyLeave(empId, request));
    }

    @Test
    void applyLeave_shouldFail_whenFromDateAfterToDate() {

        UUID empId = UUID.randomUUID();

        ApplyLeaveRequestDTO request = new ApplyLeaveRequestDTO();
        request.setFromDate(LocalDate.now());
        request.setToDate(LocalDate.now().plusDays(2));
        request.setLeaveType(LeaveType.CASUAL);

        assertThrows(RuntimeException.class,
                () -> leaveService.applyLeave(empId, request));
    }

    @Test
    void getLeaves_shouldReturnList() {

        UUID empId = UUID.randomUUID();

        LeaveRequest leave = LeaveRequest.builder()
                .id(UUID.randomUUID())
                .empId(empId)
                .fromDate(LocalDate.now())
                .toDate(LocalDate.now().plusDays(1))
                .leaveType(LeaveType.CASUAL)
                .status(LeaveStatus.DRAFT)
                .build();

        when(leaveRequestRepository.findByEmpId(empId))
                .thenReturn(List.of(leave));

        List<LeaveRequestResponseDTO> result =
                leaveService.getLeaves(empId);

        assertEquals(1, result.size());
    }


    @Test
    void getLeaves_shouldReturnEmptyList() {

        UUID empId = UUID.randomUUID();

        when(leaveRequestRepository.findByEmpId(empId))
                .thenReturn(List.of());

        List<LeaveRequestResponseDTO> result =
                leaveService.getLeaves(empId);

        assertTrue(result.isEmpty());
    }


    @Test
    void approveLeave_shouldApproveDraftLeave() {

        UUID leaveId = UUID.randomUUID();

        LeaveRequest leave = LeaveRequest.builder()
                .id(leaveId)
                .status(LeaveStatus.DRAFT)
                .fromDate(LocalDate.now())
                .toDate(LocalDate.now().plusDays(1))
                .leaveType(LeaveType.SICK)
                .build();

        when(leaveRequestRepository.findById(leaveId))
                .thenReturn(Optional.of(leave));

        when(leaveRequestRepository.save(any(LeaveRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        LeaveRequestResponseDTO response =
                leaveService.approveLeave(leaveId);

        assertEquals(LeaveStatus.APPROVED, response.getStatus());
    }


    @Test
    void approveLeave_shouldFail_whenAlreadyApproved() {

        UUID leaveId = UUID.randomUUID();

        LeaveRequest leave = LeaveRequest.builder()
                .status(LeaveStatus.APPROVED)
                .build();

        when(leaveRequestRepository.findById(leaveId))
                .thenReturn(Optional.of(leave));

        assertThrows(RuntimeException.class,
                () -> leaveService.approveLeave(leaveId));
    }

    @Test
    void approveLeave_shouldFail_whenLeaveNotFound() {

        UUID leaveId = UUID.randomUUID();

        when(leaveRequestRepository.findById(leaveId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> leaveService.approveLeave(leaveId));
    }

    @Test
    void approveLeave_shouldCallSave() {

        UUID leaveId = UUID.randomUUID();

        LeaveRequest leave = LeaveRequest.builder()
                .id(leaveId)
                .status(LeaveStatus.DRAFT)
                .build();

        when(leaveRequestRepository.findById(leaveId))
                .thenReturn(Optional.of(leave));

        when(leaveRequestRepository.save(any()))
                .thenReturn(leave);

        leaveService.approveLeave(leaveId);

        verify(leaveRequestRepository).save(any());
    }


    @Test
    void rejectLeave_shouldRejectDraftLeave() {

        UUID leaveId = UUID.randomUUID();

        LeaveRequest leave = LeaveRequest.builder()
                .id(leaveId)
                .status(LeaveStatus.DRAFT)
                .fromDate(LocalDate.now())
                .toDate(LocalDate.now().plusDays(1))
                .leaveType(LeaveType.CASUAL)
                .build();

        when(leaveRequestRepository.findById(leaveId))
                .thenReturn(Optional.of(leave));

        when(leaveRequestRepository.save(any(LeaveRequest.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        LeaveRequestResponseDTO response =
                leaveService.rejectLeave(leaveId);

        assertEquals(LeaveStatus.REJECTED, response.getStatus());
    }


    @Test
    void rejectLeave_shouldFail_whenAlreadyRejected() {

        UUID leaveId = UUID.randomUUID();

        LeaveRequest leave = LeaveRequest.builder()
                .status(LeaveStatus.REJECTED)
                .build();

        when(leaveRequestRepository.findById(leaveId))
                .thenReturn(Optional.of(leave));

        assertThrows(RuntimeException.class,
                () -> leaveService.rejectLeave(leaveId));
    }


    @Test
    void rejectLeave_shouldFail_whenLeaveNotFound() {

        UUID leaveId = UUID.randomUUID();

        when(leaveRequestRepository.findById(leaveId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> leaveService.rejectLeave(leaveId));
    }


}

