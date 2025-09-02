package org.ocean.leaveservice.dao;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveBalancesDto {

    private LeaveTypeDto leaveType;
    private Integer user;
    private int availableLeaves;
    private int usedLeaves;
}
