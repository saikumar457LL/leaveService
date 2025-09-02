package org.ocean.leaveservice.dao.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLeaveBalanceDto {
    private String username;
    private AdminLeaveTypeDto leaveType;
    private int availableLeaves;
    private int usedLeaves;
}
