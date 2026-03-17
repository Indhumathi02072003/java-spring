package com.ic.employee_service.controller;

import com.ic.employee_service.dto.EmployeeRequestDTO;
import com.ic.employee_service.dto.EmployeeResponseDTO;
import com.ic.employee_service.entity.Employee;
import com.ic.employee_service.mapper.EmployeeMapper;
import com.ic.employee_service.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    @GetMapping("/pagination")
    @Transactional(readOnly = true)
    public ResponseEntity<Page<EmployeeResponseDTO>> getEmployeesByPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Page<Employee> employeesPage = employeeService.getEmployeesWithPagination(page, size);

        Page<EmployeeResponseDTO> dtoPage = employeesPage.map(EmployeeMapper::toDTO);

        return ResponseEntity.ok(dtoPage);
    }

    // CREATE Employee
    @PostMapping
    @Transactional
    public ResponseEntity<EmployeeResponseDTO> create(@Valid @RequestBody EmployeeRequestDTO dto) {
        return ResponseEntity.ok(employeeService.createEmployee(dto));
    }

    // GET all
    @Cacheable(value = "employeeCache")
    @Transactional(readOnly = true)
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> getAll() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    //GET By ID
    @Cacheable(value = "employeeByIdCache", key = "#id")
    @Transactional(readOnly = true)
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    //Update By ID
    @Caching(
            evict = {
                    @CacheEvict(value = "employeeCache", allEntries = true)
            },
            put = {
                    @CachePut(value = "employeeByIdCache", key = "#id")
            }
    )
    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(
            @PathVariable UUID id,
            @Valid @RequestBody EmployeeRequestDTO dto
    ) {
        return ResponseEntity.ok(employeeService.updateEmployee(id, dto));
    }

}
