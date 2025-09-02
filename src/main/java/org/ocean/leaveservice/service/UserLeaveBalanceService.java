package org.ocean.leaveservice.service;

import org.ocean.leaveservice.dao.UserLeaveBalancesDto;

import java.util.List;

public interface UserLeaveBalanceService {
    List<UserLeaveBalancesDto> getMyLeaveBalances();
}
