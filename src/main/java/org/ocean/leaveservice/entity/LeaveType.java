package org.ocean.leaveservice.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "leave_type")
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false,unique = true,length = 10)
    private String code;

    @Column(nullable = false,length = 255)
    private String description;
}
