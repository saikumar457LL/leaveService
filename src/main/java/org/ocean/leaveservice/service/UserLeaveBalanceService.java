package org.ocean.leaveservice.service;

import org.ocean.leaveservice.dto.UserLeaveRequestDto;
import org.ocean.leaveservice.responses.UserLeaveApplyResponseDto;
import org.ocean.leaveservice.responses.UserLeaveBalancesResponseDto;

import java.util.List;

public interface UserLeaveBalanceService {
    List<UserLeaveBalancesResponseDto> getMyLeaveBalances();
    UserLeaveApplyResponseDto applyLeave(UserLeaveRequestDto userLeaveRequestDto);
}
