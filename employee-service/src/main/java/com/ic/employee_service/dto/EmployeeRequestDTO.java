package com.ic.employee_service.dto;

import com.ic.employee_service.enums.Department;
import com.ic.employee_service.enums.Designation;
import com.ic.employee_service.enums.EmployeeStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class EmployeeRequestDTO {
    @NotBlank(message = "First name cannot be empty")
    private String firstname;

    @NotBlank(message = "Last name cannot be empty")
    private String lastname;

    @NotNull(message = "Designation is required")
    private Designation designation;

    @NotNull(message = "Department is required")
    private Department department;

    @NotNull(message = "Salary cannot be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Salary must be greater than zero")
    private BigDecimal salary;

    @NotNull(message = "Status is required")
    private EmployeeStatus status;

}
