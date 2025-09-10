package org.ocean.leaveservice.responses;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LeaveStatus {
    private String leaveId;
    private String leaveType;
    private String status;
    private String reason;
    private LocalDateTime fromDate;
    private LocalDateTime toDate;
}
