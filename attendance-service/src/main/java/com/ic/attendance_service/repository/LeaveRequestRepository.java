package com.ic.attendance_service.repository;


import com.ic.attendance_service.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, UUID> {

    @Query("SELECT l FROM LeaveRequest l WHERE l.empId = :empId")
    List<LeaveRequest> findByEmpId(@Param("empId") UUID empId);

    Optional<LeaveRequest> findTopByEmpIdOrderByFromDateDesc(UUID empId);

//    @Query("""
//        SELECT l FROM LeaveRequest l
//        WHERE l.empId = :empId
//          AND :today BETWEEN l.fromDate AND l.toDate
//    """)
//    Optional<LeaveRequest> findActiveLeave(
//            @Param("empId") UUID empId,
//            @Param("today") LocalDate today
//    );
}


