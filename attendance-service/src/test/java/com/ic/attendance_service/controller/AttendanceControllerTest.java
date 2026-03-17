package com.ic.attendance_service.controller;

import com.ic.attendance_service.config.TestSecurityConfig;
import com.ic.attendance_service.dto.AttendanceLogResponseDTO;
import com.ic.attendance_service.dto.AttendanceMeResponseDTO;
import com.ic.attendance_service.repository.AttendanceRepository;
import com.ic.attendance_service.service.AttendanceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttendanceController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean  // ← ADD THIS - Mock the repository!
    private AttendanceRepository attendanceRepository;

    @MockBean  // ← Changed from @MockitoBean
    private AttendanceService attendanceService;

    @Test
    @WithMockUser(username = "jegan")  // ← Added mock user
    void checkIn_shouldReturn200() throws Exception {
        // GIVEN
        UUID empId = UUID.randomUUID();

        AttendanceLogResponseDTO responseDTO = new AttendanceLogResponseDTO(
                UUID.randomUUID(),
                empId,
                LocalDateTime.now(),
                null
        );

        when(attendanceService.checkIn(any(UUID.class))).thenReturn(responseDTO);

        // WHEN & THEN
        mockMvc.perform(post("/attendance/check-in/" + empId))  // ← Fixed URL!
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.empId").value(empId.toString()));
    }

    @Test
    @WithMockUser(username = "jegan")
    void checkOut_shouldReturn200() throws Exception {
        // GIVEN
        UUID empId = UUID.randomUUID();

        AttendanceLogResponseDTO responseDTO = new AttendanceLogResponseDTO(
                UUID.randomUUID(),
                empId,
                LocalDateTime.now().minusHours(8),
                LocalDateTime.now()
        );

        when(attendanceService.checkOut(any(UUID.class))).thenReturn(responseDTO);

        // WHEN & THEN
        mockMvc.perform(post("/attendance/check-out/" + empId))  // ← Fixed URL!
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkOut").exists());
    }



}