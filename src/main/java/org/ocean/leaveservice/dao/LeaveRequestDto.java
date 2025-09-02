package org.ocean.leaveservice.dao;

import lombok.Builder;
import lombok.Data;
import org.ocean.leaveservice.constants.LeaveStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class LeaveRequestDto {
    private Integer user;
    private LeaveTypeDto leaveTypeDto;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private LeaveStatus status;
    private Integer approver;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
