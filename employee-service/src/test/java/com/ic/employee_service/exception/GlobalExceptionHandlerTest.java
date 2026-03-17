package com.ic.employee_service.exception;

import com.ic.employee_service.controller.EmployeeController;
import com.ic.employee_service.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;


    // 404 - RESOURCE NOT FOUND
    @Test
    void shouldReturn404_whenResourceNotFound() throws Exception {

        UUID id = UUID.randomUUID();

        when(employeeService.getEmployeeById(id))
                .thenThrow(new ResourceNotFoundException("Employee not found"));

        mockMvc.perform(get("/api/employees/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Employee not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }



    // 500 - INTERNAL SERVER ERROR
    @Test
    void shouldReturn500_whenUnhandledExceptionOccurs() throws Exception {

        UUID id = UUID.randomUUID();

        when(employeeService.getEmployeeById(id))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(get("/api/employees/" + id))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.message").value("Unexpected error"))
                .andExpect(jsonPath("$.timestamp").exists());
    }


    // 400 - VALIDATION FAILURE (MULTIPLE ERRORS)
    @Test
    void shouldReturn400_whenMultipleValidationErrorsOccur() throws Exception {

        String invalidJson = """
                {
                  "firstname": "",
                  "lastname": "",
                  "salary": 0
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.message").value("Input validation error"))
                .andExpect(jsonPath("$.errors.firstname").exists())
                .andExpect(jsonPath("$.errors.lastname").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }



    // 400 - EMPTY JSON
    @Test
    void shouldReturn400_whenEmptyRequestBody() throws Exception {

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }


    // 400 - MISSING REQUIRED ENUM FIELDS
    @Test
    void shouldReturn400_whenRequiredEnumsAreMissing() throws Exception {

        String invalidJson = """
                {
                  "firstname": "Indhu",
                  "lastname": "Mathi",
                  "salary": 50000
                }
                """;

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.designation").exists())
                .andExpect(jsonPath("$.errors.department").exists())
                .andExpect(jsonPath("$.errors.status").exists());
    }

    // ==========================================
    // POSITIVE CHECK - VALID INPUT SHOULD NOT TRIGGER 400
    // ==========================================

    @Test
    void shouldNotReturn400_whenValidInputProvided() throws Exception {

        String validJson = """
                {
                  "firstname": "Indhu",
                  "lastname": "Mathi",
                  "designation": "MANAGER",
                  "department": "FINANCE",
                  "salary": 60000,
                  "status": "ACTIVE"
                }
                """;

        when(employeeService.createEmployee(any()))
                .thenThrow(new RuntimeException("Force error for test"));

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validJson))
                .andExpect(status().isInternalServerError());
    }
}
