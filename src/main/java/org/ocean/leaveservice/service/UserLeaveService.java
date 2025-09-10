package org.ocean.leaveservice.service;

import org.ocean.leaveservice.dto.UserLeaveRequestDto;
import org.ocean.leaveservice.responses.LeaveStatus;
import org.ocean.leaveservice.responses.UserLeaveApplyResponseDto;
import org.ocean.leaveservice.responses.UserLeaveBalancesResponseDto;

import java.util.List;

public interface UserLeaveService {
    List<UserLeaveBalancesResponseDto> getMyLeaveBalances();
    UserLeaveApplyResponseDto applyLeave(UserLeaveRequestDto userLeaveRequestDto);
    List<LeaveStatus> fetchAllLeaveStatus();
    void cancelLeave(String leaveId);
}
