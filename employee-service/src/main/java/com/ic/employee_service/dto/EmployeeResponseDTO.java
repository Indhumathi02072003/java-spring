package com.ic.employee_service.dto;

import com.ic.employee_service.enums.Department;
import com.ic.employee_service.enums.Designation;
import com.ic.employee_service.enums.EmployeeStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EmployeeResponseDTO {
    private UUID empId;
    private String firstname;
    private String lastname;
    private Designation designation;
    private Department department;
    private BigDecimal salary;
    private EmployeeStatus status;
}

