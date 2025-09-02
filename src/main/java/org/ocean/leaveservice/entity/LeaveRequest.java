package org.ocean.leaveservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.ocean.leaveservice.constants.LeaveStatus;

import java.time.LocalDateTime;
import java.util.UUID;


// TODO
/*
approverDate
reason for leave
 */

@Data
@Entity
@Table(name = "leave_requests")
public class LeaveRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "user_id",nullable = false)
    private UUID user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id",nullable = false)
    LeaveType leaveType;

    @Column(name = "from_date",nullable = false)
    private LocalDateTime fromDate;

    @Column(name = "to_date",nullable = false)
    private LocalDateTime toDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_status",nullable = false)
    private LeaveStatus status;

    @Column(name = "approver_id")
    private UUID approver;

    @CreationTimestamp
    @Column(name = "created_at",nullable = false,updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;

}
