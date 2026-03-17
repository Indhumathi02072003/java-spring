package com.ic.employee_service.service;

import com.ic.employee_service.dto.EmployeeRequestDTO;
import com.ic.employee_service.dto.EmployeeResponseDTO;
import com.ic.employee_service.entity.Employee;
import com.ic.employee_service.enums.Department;
import com.ic.employee_service.enums.Designation;
import com.ic.employee_service.enums.EmployeeStatus;
import com.ic.employee_service.repository.EmployeeRepository;
import com.ic.notification.contract.NotificationEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeEmailEventProducer employeeEmailEventProducer;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private EmployeeRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        requestDTO = EmployeeRequestDTO.builder()
                .firstname("Indhu")
                .lastname("Mathi")
                .designation(Designation.JUNIOR_DEVELOPER)
                .department(Department.ENGINEERING)
                .salary(BigDecimal.valueOf(30000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        employee = Employee.builder()
                .empId(UUID.randomUUID())
                .firstname("Indhu")
                .lastname("Mathi")
                .designation(Designation.JUNIOR_DEVELOPER)
                .department(Department.ENGINEERING)
                .salary(BigDecimal.valueOf(30000))
                .status(EmployeeStatus.ACTIVE)
                .build();
    }

    // ================= CREATE =================

    @Test
    void shouldCreateEmployeeSuccessfully() {
        when(employeeRepository.save(any())).thenReturn(employee);
        when(templateEngine.process(eq("EMPLOYEE_CREATED"), any(Context.class)))
                .thenReturn("<html>Email</html>");

        EmployeeResponseDTO result = employeeService.createEmployee(requestDTO);

        assertNotNull(result);
        assertEquals("Indhu", result.getFirstname());

        verify(employeeRepository, times(1)).save(any());
        verify(employeeEmailEventProducer, times(1))
                .sendEmailEvent(any(NotificationEvent.class));
    }

    @Test
    void shouldPopulateThymeleafContextCorrectly() {
        when(employeeRepository.save(any())).thenReturn(employee);
        when(templateEngine.process(eq("EMPLOYEE_CREATED"), any(Context.class)))
                .thenReturn("<html>Email</html>");

        employeeService.createEmployee(requestDTO);

        verify(templateEngine).process(eq("EMPLOYEE_CREATED"),
                argThat(context ->
                        context.getVariable("employeeName").equals("Indhu")));
    }

    @Test
    void shouldThrowExceptionWhenRepositoryFailsDuringCreate() {
        when(employeeRepository.save(any()))
                .thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class,
                () -> employeeService.createEmployee(requestDTO));

        verify(employeeEmailEventProducer, never()).sendEmailEvent(any());
    }

    @Test
    void shouldThrowExceptionWhenTemplateFails() {
        when(employeeRepository.save(any())).thenReturn(employee);
        when(templateEngine.process(anyString(), any()))
                .thenThrow(new RuntimeException("Template error"));

        assertThrows(RuntimeException.class,
                () -> employeeService.createEmployee(requestDTO));

        verify(employeeEmailEventProducer, never()).sendEmailEvent(any());
    }

    @Test
    void shouldThrowExceptionWhenKafkaFails() {
        when(employeeRepository.save(any())).thenReturn(employee);
        when(templateEngine.process(anyString(), any()))
                .thenReturn("<html>Email</html>");

        doThrow(new RuntimeException("Kafka error"))
                .when(employeeEmailEventProducer)
                .sendEmailEvent(any());

        assertThrows(RuntimeException.class,
                () -> employeeService.createEmployee(requestDTO));
    }

    @Test
    void shouldSendEmailEventExactlyOnce() {
        when(employeeRepository.save(any())).thenReturn(employee);
        when(templateEngine.process(anyString(), any()))
                .thenReturn("<html>Email</html>");

        employeeService.createEmployee(requestDTO);

        verify(employeeEmailEventProducer, times(1))
                .sendEmailEvent(any(NotificationEvent.class));
    }

    // ================= GET ALL =================

    @Test
    void shouldReturnAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(employee));
        List<EmployeeResponseDTO> result = employeeService.getAllEmployees();
        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoEmployeesExist() {
        when(employeeRepository.findAll()).thenReturn(Collections.emptyList());
        assertTrue(employeeService.getAllEmployees().isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenFindAllFails() {
        when(employeeRepository.findAll())
                .thenThrow(new RuntimeException("DB failure"));

        assertThrows(RuntimeException.class,
                () -> employeeService.getAllEmployees());
    }

    @Test
    void shouldMapMultipleEmployeesCorrectly() {
        when(employeeRepository.findAll())
                .thenReturn(List.of(employee, employee));

        assertEquals(2, employeeService.getAllEmployees().size());
    }

    // ================= GET BY ID =================

    @Test
    void shouldReturnEmployeeWhenExists() {
        when(employeeRepository.findById(employee.getEmpId()))
                .thenReturn(Optional.of(employee));

        EmployeeResponseDTO result =
                employeeService.getEmployeeById(employee.getEmpId());

        assertEquals(employee.getEmpId(), result.getEmpId());
        assertEquals("Mathi", result.getLastname());
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        when(employeeRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> employeeService.getEmployeeById(UUID.randomUUID()));
    }

    // ================= UPDATE =================

    @Test
    void shouldUpdateEmployeeSuccessfully() {
        when(employeeRepository.findById(employee.getEmpId()))
                .thenReturn(Optional.of(employee));
        when(employeeRepository.save(any())).thenReturn(employee);

        EmployeeResponseDTO result =
                employeeService.updateEmployee(employee.getEmpId(), requestDTO);

        assertEquals("Indhu", result.getFirstname());
        verify(employeeRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingEmployee() {
        when(employeeRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> employeeService.updateEmployee(UUID.randomUUID(), requestDTO));
    }

    @Test
    void shouldPreserveEmpIdDuringUpdate() {
        when(employeeRepository.findById(employee.getEmpId()))
                .thenReturn(Optional.of(employee));
        when(employeeRepository.save(any())).thenReturn(employee);

        EmployeeResponseDTO result =
                employeeService.updateEmployee(employee.getEmpId(), requestDTO);

        assertEquals(employee.getEmpId(), result.getEmpId());
    }

    @Test
    void shouldHandleNullDesignationDuringUpdate() {
        requestDTO.setDesignation(null);

        when(employeeRepository.findById(employee.getEmpId()))
                .thenReturn(Optional.of(employee));
        when(employeeRepository.save(any())).thenReturn(employee);

        employeeService.updateEmployee(employee.getEmpId(), requestDTO);

        assertNull(employee.getDesignation());
    }

    @Test
    void shouldNotCallSaveIfEmployeeNotFound() {
        when(employeeRepository.findById(any()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> employeeService.updateEmployee(UUID.randomUUID(), requestDTO));

        verify(employeeRepository, never()).save(any());
    }

    // ================= PAGINATION =================

    @Test
    void shouldReturnPaginatedEmployees() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(List.of(employee));
        when(employeeRepository.findAll(pageable)).thenReturn(page);

        assertEquals(1,
                employeeService.getEmployeesWithPagination(0, 10)
                        .getTotalElements());
    }

    @Test
    void shouldReturnEmptyPageWhenNoDataExists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(Collections.emptyList());
        when(employeeRepository.findAll(pageable)).thenReturn(page);

        assertTrue(employeeService
                .getEmployeesWithPagination(0, 10).isEmpty());
    }

    @Test
    void shouldReturnCorrectPageNumber() {
        Pageable pageable = PageRequest.of(1, 5);
        Page<Employee> page = new PageImpl<>(List.of(employee), pageable, 1);
        when(employeeRepository.findAll(pageable)).thenReturn(page);

        assertEquals(1,
                employeeService.getEmployeesWithPagination(1, 5)
                        .getNumber());
    }

    @Test
    void shouldReturnCorrectPageSize() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Employee> page = new PageImpl<>(List.of(employee), pageable, 1);
        when(employeeRepository.findAll(pageable)).thenReturn(page);

        assertEquals(5,
                employeeService.getEmployeesWithPagination(0, 5)
                        .getSize());
    }
}
