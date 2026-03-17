package com.ic.attendance_service.service;

import com.ic.attendance_service.dto.AttendanceLogResponseDTO;
import com.ic.attendance_service.dto.AttendanceMeResponseDTO;
import com.ic.attendance_service.entity.AttendanceLog;
import com.ic.attendance_service.entity.LeaveRequest;
import com.ic.attendance_service.enums.LeaveStatus;
import com.ic.attendance_service.enums.LeaveType;
import com.ic.attendance_service.repository.LeaveRequestRepository;
import com.ic.attendance_service.repository.AttendanceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {

    @Mock
    private AttendanceRepository attendanceRepository;

    @InjectMocks
    private AttendanceService attendanceService;

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Test
    void checkIn_shouldCreateAttendanceLog() {
        // GIVEN
        UUID empId = UUID.randomUUID();

        AttendanceLog savedLog = AttendanceLog.builder()
                .id(UUID.randomUUID())
                .empId(empId)
                .checkIn(LocalDateTime.now())
                .build();

        when(attendanceRepository.save(any())).thenReturn(savedLog);

        // WHEN
        AttendanceLogResponseDTO response =
                attendanceService.checkIn(empId);

        // THEN
        assertThat(response.getEmpId()).isEqualTo(empId);
        assertThat(response.getCheckIn()).isNotNull();
        verify(attendanceRepository, times(1)).save(any());
    }

    @Test
    void checkIn_shouldCreateNewAttendance() {

        UUID empId = UUID.randomUUID();

        when(attendanceRepository.save(any(AttendanceLog.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        AttendanceLogResponseDTO response =
                attendanceService.checkIn(empId);

        assertNotNull(response);
        assertEquals(empId, response.getEmpId());
        assertNotNull(response.getCheckIn());
        assertNull(response.getCheckOut());

        // Optional verification
        verify(attendanceRepository).save(any(AttendanceLog.class));
    }


    @Test
    void checkOut_shouldUpdateCheckoutTime() {
        // GIVEN
        UUID empId = UUID.randomUUID();

        AttendanceLog activeLog = AttendanceLog.builder()
                .id(UUID.randomUUID())
                .empId(empId)
                .checkIn(LocalDateTime.now().minusHours(8))
                .build();

        when(attendanceRepository
                .findTopByEmpIdAndCheckOutIsNullOrderByCheckInDesc(empId))
                .thenReturn(Optional.of(activeLog));

        when(attendanceRepository.save(any())).thenReturn(activeLog);

        // WHEN
        AttendanceLogResponseDTO response =
                attendanceService.checkOut(empId);

        // THEN
        assertThat(response.getCheckOut()).isNotNull();
        verify(attendanceRepository).save(activeLog);
    }

    @Test
    void checkOut_shouldThrowException_ifNoActiveCheckIn() {

        UUID empId = UUID.randomUUID();

        when(attendanceRepository
                .findTopByEmpIdAndCheckOutIsNullOrderByCheckInDesc(empId))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> attendanceService.checkOut(empId));
    }

    @Test
    void checkOut_shouldThrowException_whenNoActiveCheckIn() {

        UUID empId = UUID.randomUUID();

        when(attendanceRepository
                .findTopByEmpIdAndCheckOutIsNullOrderByCheckInDesc(empId))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(RuntimeException.class,
                        () -> attendanceService.checkOut(empId));

        assertEquals("No active check-in found", exception.getMessage());
    }


    @Test
    void getMyAttendance_shouldReturnLeaveDetails() {
        UUID empId = UUID.randomUUID();

        LeaveRequest leave = LeaveRequest.builder()
                .id(UUID.randomUUID())
                .empId(empId)
                .fromDate(LocalDate.now())
                .toDate(LocalDate.now().plusDays(2))
                .leaveType(LeaveType.SICK)
                .status(LeaveStatus.APPROVED)
                .build();

        when(leaveRequestRepository
                .findTopByEmpIdOrderByFromDateDesc(empId))
                .thenReturn(Optional.of(leave));

        AttendanceMeResponseDTO dto =
                attendanceService.getMyAttendance(empId);

        assertEquals(leave.getId(), dto.getLeaveId());
        assertEquals(LeaveStatus.APPROVED, dto.getLeaveStatus());
        assertEquals(LeaveType.SICK, dto.getLeaveType());
    }

    @Test
    void getMyAttendance_shouldReturnEmptyDto_whenNoLeave() {

        UUID empId = UUID.randomUUID();

        when(leaveRequestRepository
                .findTopByEmpIdOrderByFromDateDesc(empId))
                .thenReturn(Optional.empty());

        AttendanceMeResponseDTO dto =
                attendanceService.getMyAttendance(empId);

        assertNull(dto.getLeaveId());
        assertNull(dto.getLeaveStatus());
        assertNull(dto.getLeaveType());
    }

    @Test
    void getMyAttendance_shouldWorkWithoutAttendanceData() {

        UUID empId = UUID.randomUUID();

        when(leaveRequestRepository
                .findTopByEmpIdOrderByFromDateDesc(empId))
                .thenReturn(Optional.empty());

        AttendanceMeResponseDTO dto =
                attendanceService.getMyAttendance(empId);

        assertNotNull(dto); // no exception = pass
    }
}

