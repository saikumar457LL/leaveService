package org.ocean.leaveservice.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "leave_balances",uniqueConstraints =@UniqueConstraint(columnNames = {"user_id","leave_type_id"}))
public class LeaveBalances {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id",nullable = false)
    private LeaveType leaveType;

    @Column(name = "user_id")
    private UUID user;

    @Column(name = "available_leaves",nullable = false)
    private int availableLeaves = 0;

    @Column(name = "used_leaves",nullable = false)
    private int usedLeaves = 0;

    @Version
    private Integer version;
}
