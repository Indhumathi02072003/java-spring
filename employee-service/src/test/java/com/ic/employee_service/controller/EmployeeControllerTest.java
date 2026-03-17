package com.ic.employee_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ic.employee_service.dto.EmployeeRequestDTO;
import com.ic.employee_service.dto.EmployeeResponseDTO;
import com.ic.employee_service.entity.Employee;
import com.ic.employee_service.enums.Department;
import com.ic.employee_service.enums.Designation;
import com.ic.employee_service.enums.EmployeeStatus;
import com.ic.employee_service.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    // GET ALL
    @Test
    void shouldReturnAllEmployees() throws Exception {

        EmployeeResponseDTO dto = EmployeeResponseDTO.builder()
                .empId(UUID.randomUUID())
                .firstname("Indhu")
                .lastname("Mathi")
                .designation(Designation.MANAGER)
                .department(Department.FINANCE)
                .salary(BigDecimal.valueOf(50000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        when(employeeService.getAllEmployees())
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].firstname").value("Indhu"));
    }

    // GET BY ID
    @Test
    void shouldReturnEmployeeById() throws Exception {

        UUID id = UUID.randomUUID();

        EmployeeResponseDTO dto = EmployeeResponseDTO.builder()
                .empId(id)
                .firstname("Test")
                .lastname("User")
                .designation(Designation.MANAGER)
                .department(Department.FINANCE)
                .salary(BigDecimal.valueOf(50000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        when(employeeService.getEmployeeById(id))
                .thenReturn(dto);

        mockMvc.perform(get("/api/employees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Test"));
    }

    // CREATE SUCCESS
    @Test
    void shouldCreateEmployee() throws Exception {

        EmployeeRequestDTO request = EmployeeRequestDTO.builder()
                .firstname("Indhu")
                .lastname("Mathi")
                .designation(Designation.MANAGER)
                .department(Department.FINANCE)
                .salary(BigDecimal.valueOf(60000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        EmployeeResponseDTO response = EmployeeResponseDTO.builder()
                .empId(UUID.randomUUID())
                .firstname("Indhu")
                .lastname("Mathi")
                .designation(Designation.MANAGER)
                .department(Department.FINANCE)
                .salary(BigDecimal.valueOf(60000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        when(employeeService.createEmployee(request))
                .thenReturn(response);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Indhu"));
    }

    // CREATE VALIDATION FAIL
    @Test
    void shouldReturnBadRequest_whenCreateValidationFails() throws Exception {

        EmployeeRequestDTO invalid = new EmployeeRequestDTO();

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    // UPDATE SUCCESS
    @Test
    void shouldUpdateEmployee() throws Exception {

        UUID id = UUID.randomUUID();

        EmployeeRequestDTO request = EmployeeRequestDTO.builder()
                .firstname("Updated")
                .lastname("User")
                .designation(Designation.MANAGER)
                .department(Department.FINANCE)
                .salary(BigDecimal.valueOf(70000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        EmployeeResponseDTO response = EmployeeResponseDTO.builder()
                .empId(id)
                .firstname("Updated")
                .lastname("User")
                .designation(Designation.MANAGER)
                .department(Department.FINANCE)
                .salary(BigDecimal.valueOf(70000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        when(employeeService.updateEmployee(id, request))
                .thenReturn(response);

        mockMvc.perform(put("/api/employees/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstname").value("Updated"));
    }

    // PAGINATION
    @Test
    void shouldReturnEmployeesWithPagination() throws Exception {

        Employee employee = Employee.builder()
                .empId(UUID.randomUUID())
                .firstname("Paged")
                .lastname("User")
                .designation(Designation.MANAGER)
                .department(Department.FINANCE)
                .salary(BigDecimal.valueOf(50000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        Page<Employee> page = new PageImpl<>(List.of(employee));

        when(employeeService.getEmployeesWithPagination(0, 10))
                .thenReturn(page);

        mockMvc.perform(get("/api/employees/pagination?page=0&size=10"))
                .andExpect(status().isOk());
    }
}
