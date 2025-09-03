package org.ocean.leaveservice.responses;

import lombok.Builder;
import lombok.Data;
import org.ocean.leaveservice.dto.UserLeaveTypeDto;

@Data
@Builder
public class UserLeaveBalancesResponseDto {

    private String leaveType;
    private int availableLeaves;
    private int usedLeaves;
}
