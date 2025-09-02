package org.ocean.leaveservice.service;

import org.ocean.leaveservice.dao.LeaveBalancesDto;

import java.util.List;

public interface LeaveBalance {

    List<LeaveBalancesDto> getLeaveBalances();
}
