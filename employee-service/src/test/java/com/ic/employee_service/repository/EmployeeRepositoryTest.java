//package com.ic.employee_service.repository;
//
//import com.ic.employee_service.entity.Employee;
//import com.ic.employee_service.enums.*;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DataJpaTest
//class EmployeeRepositoryTest {
//
//    @Autowired
//    private EmployeeRepository employeeRepository;
//
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    private Employee buildEmployee() {
//        return Employee.builder()
//                .firstname("Indhu")
//                .lastname("Mathi")
//                .designation(Designation.JUNIOR_DEVELOPER)
//                .department(Department.ENGINEERING)
//                .salary(BigDecimal.valueOf(50000))
//                .status(EmployeeStatus.ACTIVE)
//                .build();
//    }
//
//
//    // SAVE TESTS
//    @Test
//    @DisplayName("Should save employee successfully")
//    void shouldSaveEmployeeSuccessfully() {
//        Employee saved = employeeRepository.save(buildEmployee());
//
//        assertNotNull(saved.getEmpId());
//        assertEquals("Indhu", saved.getFirstname());
//    }
//
//    @Test
//    @DisplayName("Should generate UUID automatically")
//    void shouldGenerateUUIDAutomatically() {
//        Employee saved = employeeRepository.save(buildEmployee());
//
//        assertNotNull(saved.getEmpId());
//        assertTrue(saved.getEmpId() instanceof UUID);
//    }
//
//    @Test
//    @DisplayName("Should throw exception when required field is null")
//    void shouldFailWhenFirstnameIsNull() {
//        Employee employee = buildEmployee();
//        employee.setFirstname(null);
//
//        assertThrows(Exception.class, () -> {
//            employeeRepository.saveAndFlush(employee);
//        });
//    }
//
//
//    // FIND TESTS
//    @Test
//    @DisplayName("Should find employee by ID")
//    void shouldFindEmployeeById() {
//        Employee saved = employeeRepository.save(buildEmployee());
//
//        Optional<Employee> found =
//                employeeRepository.findById(saved.getEmpId());
//
//        assertTrue(found.isPresent());
//    }
//
//    @Test
//    @DisplayName("Should return empty when employee not found")
//    void shouldReturnEmptyWhenEmployeeNotFound() {
//        Optional<Employee> found =
//                employeeRepository.findById(UUID.randomUUID());
//
//        assertTrue(found.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Should return all employees")
//    void shouldReturnAllEmployees() {
//        employeeRepository.save(buildEmployee());
//        employeeRepository.save(buildEmployee());
//
//        List<Employee> employees = employeeRepository.findAll();
//
//        assertEquals(2, employees.size());
//    }
//
//
//    // UPDATE TESTS
//    @Test
//    @DisplayName("Should update employee salary")
//    void shouldUpdateEmployeeSalary() {
//        Employee saved = employeeRepository.save(buildEmployee());
//
//        saved.setSalary(BigDecimal.valueOf(70000));
//        employeeRepository.save(saved);
//
//        Employee updated =
//                employeeRepository.findById(saved.getEmpId()).get();
//
//        assertEquals(BigDecimal.valueOf(70000), updated.getSalary());
//    }
//
//    @Test
//    @DisplayName("Should update employee status")
//    void shouldUpdateEmployeeStatus() {
//        Employee saved = employeeRepository.save(buildEmployee());
//
//        saved.setStatus(EmployeeStatus.INACTIVE);
//        employeeRepository.save(saved);
//
//        Employee updated =
//                employeeRepository.findById(saved.getEmpId()).get();
//
//        assertEquals(EmployeeStatus.INACTIVE, updated.getStatus());
//    }
//
//
//    // DELETE TESTS
//    @Test
//    @DisplayName("Should delete employee successfully")
//    void shouldDeleteEmployee() {
//        Employee saved = employeeRepository.save(buildEmployee());
//
//        employeeRepository.deleteById(saved.getEmpId());
//
//        Optional<Employee> found =
//                employeeRepository.findById(saved.getEmpId());
//
//        assertTrue(found.isEmpty());
//    }
//
//    @Test
//    @DisplayName("Deleting non-existing employee should not throw error")
//    void shouldNotThrowWhenDeletingNonExistingEmployee() {
//        assertDoesNotThrow(() ->
//                employeeRepository.deleteById(UUID.randomUUID()));
//    }
//
//
//    // COUNT & EXISTS TESTS
//     @Test
//    @DisplayName("Should count employees correctly")
//    void shouldCountEmployeesCorrectly() {
//        employeeRepository.save(buildEmployee());
//        employeeRepository.save(buildEmployee());
//
//        long count = employeeRepository.count();
//
//        assertEquals(2, count);
//    }
//
//    @Test
//    @DisplayName("Should check if employee exists by ID")
//    void shouldCheckIfEmployeeExists() {
//        Employee saved = employeeRepository.save(buildEmployee());
//
//        boolean exists =
//                employeeRepository.existsById(saved.getEmpId());
//
//        assertTrue(exists);
//    }
//
//    @Test
//    @DisplayName("Should return false if employee does not exist")
//    void shouldReturnFalseIfEmployeeDoesNotExist() {
//        boolean exists =
//                employeeRepository.existsById(UUID.randomUUID());
//
//        assertFalse(exists);
//    }
//}

package com.ic.employee_service.repository;

import com.ic.employee_service.entity.Employee;
import com.ic.employee_service.enums.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16.0");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private EmployeeRepository employeeRepository;

    private Employee buildEmployee() {
        return Employee.builder()
                .firstname("Indhu")
                .lastname("Mathi")
                .designation(Designation.JUNIOR_DEVELOPER)
                .department(Department.ENGINEERING)
                .salary(BigDecimal.valueOf(50000))
                .status(EmployeeStatus.ACTIVE)
                .build();
    }

    // ✅ Save Test
    @Test
    void shouldSaveEmployeeSuccessfully() {

        Employee saved = employeeRepository.saveAndFlush(buildEmployee());

        assertNotNull(saved.getEmpId());
        assertEquals("Indhu", saved.getFirstname());
    }

    // ✅ Find By ID Test
    @Test
    void shouldFindEmployeeById() {

        Employee saved = employeeRepository.saveAndFlush(buildEmployee());

        Optional<Employee> found =
                employeeRepository.findById(saved.getEmpId());

        assertTrue(found.isPresent());
    }

    // ✅ Exists Test
    @Test
    void shouldReturnTrueWhenEmployeeExists() {

        Employee saved = employeeRepository.saveAndFlush(buildEmployee());

        boolean exists =
                employeeRepository.existsById(saved.getEmpId());

        assertTrue(exists);
    }

    // ✅ Custom Query Test
    @Test
    void shouldFindEmployeeByStatus() {

        employeeRepository.saveAndFlush(buildEmployee());

        List<Employee> employees =
                employeeRepository.findByStatus(EmployeeStatus.ACTIVE);

        assertEquals(1, employees.size());
    }
}

