package org.ocean.leaveservice.service;

import jakarta.annotation.Nullable;
import org.ocean.leaveservice.dto.admin.AdminLeaveAdjustRequestDto;
import org.ocean.leaveservice.dto.admin.LeaveStatusChangeRequest;
import org.ocean.leaveservice.responses.AdminLeaveBalanceResponseDto;
import org.ocean.leaveservice.responses.LeaveStatus;

import java.util.List;

public interface AdminLeaveService {

    // TODO remove below method, not the correct method, remove this method
    List<AdminLeaveBalanceResponseDto> fetchUserLeaves(String uuid);
    List<AdminLeaveBalanceResponseDto> adjustUserLeaves(AdminLeaveAdjustRequestDto leaveAdjustRequestDto);
    LeaveStatus approveOrReject(LeaveStatusChangeRequest leaveStatusChangeRequestDto);

    List<LeaveStatus> fetchAllAppliedLeaves(@Nullable String leaveStatus);
}
