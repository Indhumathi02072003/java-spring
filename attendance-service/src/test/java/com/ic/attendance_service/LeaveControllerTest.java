package com.ic.attendance_service;

import com.ic.attendance_service.config.TestSecurityConfig;
import com.ic.attendance_service.controller.LeaveController;
import com.ic.attendance_service.dto.ApplyLeaveRequestDTO;
import com.ic.attendance_service.dto.LeaveRequestResponseDTO;
import com.ic.attendance_service.enums.LeaveStatus;
import com.ic.attendance_service.enums.LeaveType;
import com.ic.attendance_service.repository.LeaveRequestRepository;
import com.ic.attendance_service.service.LeaveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaveController.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class LeaveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LeaveService leaveService;

    @MockBean
    private LeaveRequestRepository leaveRequestRepository;

    @Test
    @WithMockUser(roles = "ADMIN")
    void approveLeave_shouldReturnApprovedStatus() throws Exception {

        UUID leaveId = UUID.randomUUID();

        LeaveRequestResponseDTO dto =
                new LeaveRequestResponseDTO(
                        leaveId,
                        UUID.randomUUID(),
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        LeaveType.CASUAL,
                        LeaveStatus.APPROVED
                );

        when(leaveService.approveLeave(leaveId)).thenReturn(dto);

        mockMvc.perform(put("/leave/" + leaveId + "/approve"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @WithMockUser
    void applyLeave_shouldReturn200() throws Exception {

        UUID empId = UUID.randomUUID();

        ApplyLeaveRequestDTO request = new ApplyLeaveRequestDTO();
        request.setFromDate(LocalDate.now());
        request.setToDate(LocalDate.now().plusDays(1));
        request.setLeaveType(LeaveType.SICK);

        LeaveRequestResponseDTO dto =
                new LeaveRequestResponseDTO(
                        UUID.randomUUID(),
                        empId,
                        LocalDate.now(),
                        LocalDate.now().plusDays(1),
                        LeaveType.CASUAL,
                        LeaveStatus.DRAFT

                );

        when(leaveService.applyLeave(any(UUID.class), any(ApplyLeaveRequestDTO.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/leave/apply")
                        .header("X-User-Id", empId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "fromDate": "2026-02-05",
                  "toDate": "2026-02-06",
                  "leaveType": "CASUAL"
                }
            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.leaveType").value("CASUAL"));
    }

}

