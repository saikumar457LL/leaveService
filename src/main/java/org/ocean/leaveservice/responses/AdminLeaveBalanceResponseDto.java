package org.ocean.leaveservice.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLeaveBalanceResponseDto {
    private String username;
    private AdminLeaveTypeResponseDto leaveType;
    private int availableLeaves;
    private int usedLeaves;
}
