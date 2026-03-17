package com.ic.employee_service.service;

import com.ic.employee_service.dto.EmployeeRequestDTO;
import com.ic.employee_service.dto.EmployeeResponseDTO;
import com.ic.employee_service.entity.Employee;
import com.ic.employee_service.mapper.EmployeeMapper;
import com.ic.employee_service.repository.EmployeeRepository;
import com.ic.employee_service.utils.EmailNotificationFactory;
import com.ic.notification.contract.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository; //Tool to talk to the database

    private final EmployeeEmailEventProducer employeeEmailEventProducer;    //Responsible for sending event to Kafka

    private final TemplateEngine templateEngine; // ✅ inject Thymeleaf

    // CREATE
    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {
        Employee employee = EmployeeMapper.toEntity(dto);
        Employee saved = employeeRepository.save(employee);

        Context context = new Context();
        context.setVariable("employeeName", saved.getFirstname());
        context.setVariable("department", saved.getDepartment().name());
        context.setVariable("designation", saved.getDesignation().name());
        context.setVariable("salary", saved.getSalary());
        context.setVariable("status", saved.getStatus().name());

        // 2️⃣ Render HTML
        String html = templateEngine.process(
                "EMPLOYEE_CREATED", // template name
                context
        );

        // 3️⃣ Create event with FINAL HTML
        NotificationEvent event =
                EmailNotificationFactory.employeeCreated(
                        html
                );


        // 🔔 SEND EMAIL EVENT (ONLY ONCE)
        employeeEmailEventProducer.sendEmailEvent(event);


        return EmployeeMapper.toDTO(saved);
    }


    // GET all
    @Cacheable("employeeCache")
    @Transactional(readOnly = true)
    public List<EmployeeResponseDTO> getAllEmployees() {
        System.out.println("GET ALL HIT");
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeMapper::toDTO)
                .toList();
    }

    //GET By ID
    @Transactional(readOnly = true)
    public EmployeeResponseDTO getEmployeeById(UUID id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        return EmployeeMapper.toDTO(employee);
    }

    //UPDATE By ID
    @Transactional
    public EmployeeResponseDTO updateEmployee(UUID id, EmployeeRequestDTO dto) {

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));

        // Update fields
        existing.setFirstname(dto.getFirstname());
        existing.setLastname(dto.getLastname());
        existing.setDepartment(dto.getDepartment());
        existing.setDesignation(dto.getDesignation());
        existing.setSalary(dto.getSalary());
        existing.setStatus(dto.getStatus());

        Employee updated = employeeRepository.save(existing);

        return EmployeeMapper.toDTO(updated);
    }

    public Page<Employee> getEmployeesWithPagination(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findAll(pageable);
    }


}
