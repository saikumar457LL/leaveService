package org.ocean.leaveservice.dao;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLeaveBalancesDto {

    private UserLeaveTypeDto leaveType;
    private int availableLeaves;
    private int usedLeaves;
}
