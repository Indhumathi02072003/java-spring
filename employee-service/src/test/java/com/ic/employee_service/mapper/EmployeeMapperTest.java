
package com.ic.employee_service.mapper;

import com.ic.employee_service.dto.EmployeeRequestDTO;
import com.ic.employee_service.dto.EmployeeResponseDTO;
import com.ic.employee_service.entity.Employee;
import com.ic.employee_service.enums.Department;
import com.ic.employee_service.enums.Designation;
import com.ic.employee_service.enums.EmployeeStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

    // ==========================================================
    // ENTITY → DTO TESTS
    // ==========================================================

    @Test
    void shouldMapAllFieldsFromEntityToDTO() {

        UUID id = UUID.randomUUID();

        Employee employee = Employee.builder()
                .empId(id)
                .firstname("Indhu")
                .lastname("Mathi")
                .designation(Designation.MANAGER)
                .department(Department.FINANCE)
                .salary(BigDecimal.valueOf(60000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        EmployeeResponseDTO dto = EmployeeMapper.toDTO(employee);

        assertAll(
                () -> assertEquals(id, dto.getEmpId()),
                () -> assertEquals("Indhu", dto.getFirstname()),
                () -> assertEquals("Mathi", dto.getLastname()),
                () -> assertEquals(Designation.MANAGER, dto.getDesignation()),
                () -> assertEquals(Department.FINANCE, dto.getDepartment()),
                () -> assertEquals(BigDecimal.valueOf(60000), dto.getSalary()),
                () -> assertEquals(EmployeeStatus.ACTIVE, dto.getStatus())
        );
    }

    @Test
    void shouldHandleDifferentEnumValuesCorrectly() {

        Employee employee = Employee.builder()
                .empId(UUID.randomUUID())
                .firstname("Test")
                .lastname("User")
                .designation(Designation.DEVOPS_ENGINEER)
                .department(Department.OPERATIONS)
                .salary(BigDecimal.valueOf(45000))
                .status(EmployeeStatus.INACTIVE)
                .build();

        EmployeeResponseDTO dto = EmployeeMapper.toDTO(employee);

        assertEquals(Designation.DEVOPS_ENGINEER, dto.getDesignation());
        assertEquals(Department.OPERATIONS, dto.getDepartment());
        assertEquals(EmployeeStatus.INACTIVE, dto.getStatus());
    }

    @Test
    void shouldReturnNewInstanceEachTime() {

        Employee employee = Employee.builder()
                .empId(UUID.randomUUID())
                .firstname("Test")
                .lastname("User")
                .designation(Designation.MANAGER)
                .department(Department.FINANCE)
                .salary(BigDecimal.valueOf(50000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        EmployeeResponseDTO dto1 = EmployeeMapper.toDTO(employee);
        EmployeeResponseDTO dto2 = EmployeeMapper.toDTO(employee);

        assertNotSame(dto1, dto2);
    }

    @Test
    void shouldNotModifyOriginalEntityDuringMapping() {

        Employee employee = Employee.builder()
                .empId(UUID.randomUUID())
                .firstname("Original")
                .lastname("User")
                .designation(Designation.MANAGER)
                .department(Department.FINANCE)
                .salary(BigDecimal.valueOf(50000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        EmployeeMapper.toDTO(employee);

        assertEquals("Original", employee.getFirstname());
    }

    @Test
    void shouldThrowExceptionWhenEntityIsNull() {
        assertThrows(NullPointerException.class,
                () -> EmployeeMapper.toDTO(null));
    }
   // DTO → ENTITY TESTS
    @Test
    void shouldMapAllFieldsFromDTOToEntity() {

        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .firstname("Indhu")
                .lastname("Mathi")
                .designation(Designation.ARCHITECT)
                .department(Department.ENGINEERING)
                .salary(BigDecimal.valueOf(90000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        Employee entity = EmployeeMapper.toEntity(dto);

        assertAll(
                () -> assertEquals("Indhu", entity.getFirstname()),
                () -> assertEquals("Mathi", entity.getLastname()),
                () -> assertEquals(Designation.ARCHITECT, entity.getDesignation()),
                () -> assertEquals(Department.ENGINEERING, entity.getDepartment()),
                () -> assertEquals(BigDecimal.valueOf(90000), entity.getSalary()),
                () -> assertEquals(EmployeeStatus.ACTIVE, entity.getStatus())
        );
    }

    @Test
    void shouldNotSetEmpIdWhenMappingDTOToEntity() {

        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .firstname("Test")
                .lastname("User")
                .designation(Designation.MANAGER)
                .department(Department.HUMAN_RESOURCES)
                .salary(BigDecimal.valueOf(30000))
                .status(EmployeeStatus.ACTIVE)
                .build();

        Employee entity = EmployeeMapper.toEntity(dto);

        assertNull(entity.getEmpId());
    }

    @Test
    void shouldHandleZeroSalaryCorrectly() {

        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .firstname("Zero")
                .lastname("Salary")
                .designation(Designation.QA_ENGINEER)
                .department(Department.QUALITY_ASSURANCE)
                .salary(BigDecimal.ONE)
                .status(EmployeeStatus.ACTIVE)
                .build();

        Employee entity = EmployeeMapper.toEntity(dto);

        assertEquals(BigDecimal.ONE, entity.getSalary());
    }

    @Test
    void shouldHandleLargeSalaryValue() {

        BigDecimal largeSalary = new BigDecimal("999999999.99");

        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .firstname("High")
                .lastname("Salary")
                .designation(Designation.DIRECTOR)
                .department(Department.FINANCE)
                .salary(largeSalary)
                .status(EmployeeStatus.ACTIVE)
                .build();

        Employee entity = EmployeeMapper.toEntity(dto);

        assertEquals(largeSalary, entity.getSalary());
    }

    @Test
    void shouldPreserveSalaryPrecision() {

        BigDecimal preciseSalary = new BigDecimal("12345.6789");

        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .firstname("Precise")
                .lastname("Amount")
                .designation(Designation.MANAGER)
                .department(Department.MARKETING)
                .salary(preciseSalary)
                .status(EmployeeStatus.ACTIVE)
                .build();

        Employee entity = EmployeeMapper.toEntity(dto);

        assertEquals(preciseSalary, entity.getSalary());
    }

    @Test
    void shouldMapEmptyStringsCorrectly() {

        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .firstname("")
                .lastname("")
                .designation(Designation.MANAGER)
                .department(Department.SALES)
                .salary(BigDecimal.TEN)
                .status(EmployeeStatus.ACTIVE)
                .build();

        Employee entity = EmployeeMapper.toEntity(dto);

        assertEquals("", entity.getFirstname());
        assertEquals("", entity.getLastname());
    }

    @Test
    void shouldHandleNullEnumsGracefully() {

        EmployeeRequestDTO dto = EmployeeRequestDTO.builder()
                .firstname("Test")
                .lastname("User")
                .designation(null)
                .department(null)
                .salary(BigDecimal.TEN)
                .status(null)
                .build();

        Employee entity = EmployeeMapper.toEntity(dto);

        assertNull(entity.getDesignation());
        assertNull(entity.getDepartment());
        assertNull(entity.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenDTOIsNull() {
        assertThrows(NullPointerException.class,
                () -> EmployeeMapper.toEntity(null));
    }
}
