package org.ocean.leaveservice.service;

import org.ocean.leaveservice.dto.admin.AdminLeaveAdjustRequestDto;
import org.ocean.leaveservice.responses.AdminLeaveBalanceResponseDto;

import java.util.List;

public interface AdminLeaveService {

    List<AdminLeaveBalanceResponseDto> fetchUserLeaves(String uuid);
    List<AdminLeaveBalanceResponseDto> adjustUserLeaves(AdminLeaveAdjustRequestDto leaveAdjustRequestDto);
}
