package com.ic.employee_service.mapper;

import com.ic.employee_service.dto.EmployeeRequestDTO;
import com.ic.employee_service.dto.EmployeeResponseDTO;
import com.ic.employee_service.entity.Employee;

public class EmployeeMapper {

    public static Employee toEntity(EmployeeRequestDTO dto) {
        return Employee.builder()
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .designation(dto.getDesignation())
                .department(dto.getDepartment())
                .salary(dto.getSalary())
                .status(dto.getStatus())
                .build();
    }

    public static EmployeeResponseDTO toDTO(Employee entity) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmpId(entity.getEmpId());
        dto.setFirstname(entity.getFirstname());
        dto.setLastname(entity.getLastname());
        dto.setDesignation(entity.getDesignation());
        dto.setDepartment(entity.getDepartment());
        dto.setSalary(entity.getSalary());
        dto.setStatus(entity.getStatus());
        return dto;
    }
}
