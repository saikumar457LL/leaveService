package org.ocean.leaveservice.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserLeaveApplyResponseDto {
    private String requestId;
    private String leaveType;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
