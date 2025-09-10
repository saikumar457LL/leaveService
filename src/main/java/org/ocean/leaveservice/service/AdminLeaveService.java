package org.ocean.leaveservice.service;

import org.ocean.leaveservice.dto.admin.AdminLeaveAdjustRequestDto;
import org.ocean.leaveservice.dto.admin.LeaveStatusChangeRequest;
import org.ocean.leaveservice.responses.AdminLeaveBalanceResponseDto;
import org.ocean.leaveservice.responses.LeaveStatus;

import java.util.List;

public interface AdminLeaveService {

    List<AdminLeaveBalanceResponseDto> fetchUserLeaves(String uuid);
    List<AdminLeaveBalanceResponseDto> adjustUserLeaves(AdminLeaveAdjustRequestDto leaveAdjustRequestDto);
    LeaveStatus approveOrReject(LeaveStatusChangeRequest leaveStatusChangeRequestDto);
}
