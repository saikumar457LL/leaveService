package org.ocean.leaveservice.dao;

import lombok.Builder;
import lombok.Data;
import org.ocean.leaveservice.constants.LeaveStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class LeaveRequestDto {
    private Integer user;
    private UserLeaveTypeDto userLeaveTypeDto;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private LeaveStatus status;
    private UUID approver;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
