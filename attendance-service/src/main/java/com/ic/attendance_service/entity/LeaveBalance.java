//package com.ic.attendance_service.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//
//import java.util.UUID;
//
//@Entity
//@Table(
//        name = "leave_balance",
//        uniqueConstraints = @UniqueConstraint(columnNames = {"emp_id", "leave_type_id"})
//)
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class LeaveBalance {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private UUID id;
//
//    @Column(name = "emp_id", nullable = false)
//    private UUID empId;
//
//    @ManyToOne
//    @JoinColumn(name = "leave_type_id", nullable = false)
//    private LeaveType leaveType;
//
//    @Column(nullable = false)
//    private int count;
//}
//
