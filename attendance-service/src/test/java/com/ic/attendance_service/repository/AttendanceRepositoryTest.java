package com.ic.attendance_service.repository;

import com.ic.attendance_service.entity.AttendanceLog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AttendanceRepositoryTest {


    @Autowired
    private AttendanceRepository attendanceRepository;

    @Test
    void findTopByEmpIdAndCheckOutIsNullOrderByCheckInDesc_shouldReturnLatestOpenLog() {

        UUID empId = UUID.randomUUID();

        AttendanceLog oldLog = AttendanceLog.builder()
                .empId(empId)
                .checkIn(LocalDateTime.now().minusHours(8))
                .checkOut(LocalDateTime.now().minusHours(1))
                .build();

        AttendanceLog activeLog = AttendanceLog.builder()
                .empId(empId)
                .checkIn(LocalDateTime.now().minusHours(2))
                .build();

        attendanceRepository.save(oldLog);
        attendanceRepository.save(activeLog);

        Optional<AttendanceLog> result =
                attendanceRepository
                        .findTopByEmpIdAndCheckOutIsNullOrderByCheckInDesc(empId);

        assertTrue(result.isPresent());
        assertNull(result.get().getCheckOut());
        assertEquals(empId, result.get().getEmpId());
    }


    @Test
    void findTopByEmpIdAndCheckOutIsNullOrderByCheckInDesc_shouldReturnEmpty_whenNoOpenLog() {

        UUID empId = UUID.randomUUID();

        Optional<AttendanceLog> result =
                attendanceRepository
                        .findTopByEmpIdAndCheckOutIsNullOrderByCheckInDesc(empId);

        assertTrue(result.isEmpty());
    }

}

