package com.ic.employee_service.entity;


import com.ic.employee_service.enums.Designation;
import com.ic.employee_service.enums.Department;
import com.ic.employee_service.enums.EmployeeStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;
//
//@Entity
//@Data
//@Table(name = "employees")
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class Employee {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "emp_id", nullable = false, updatable = false)
//    private UUID empId;
//
//    @Column(nullable = false)
//    private String firstname;
//
//    @Column(nullable = false)
//    private String lastname;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Designation designation;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private Department department;
//
//    @Column(nullable = false)
//    private BigDecimal salary;
//
//    @Enumerated(EnumType.STRING)
//    @Column(nullable = false)
//    private EmployeeStatus status;
//}

@Entity
@Data
@Table(name = "employees")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "emp_id", nullable = false, updatable = false)
    private UUID empId;

    @Column(nullable = false)
    private String firstname;

    @Column(nullable = false)
    private String lastname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Designation designation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Department department;

    @Column(nullable = false)
    private BigDecimal salary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeStatus status;
}
