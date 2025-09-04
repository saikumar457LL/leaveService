package org.ocean.leaveservice.service;

import org.ocean.leaveservice.responses.AdminLeaveBalanceResponseDto;

import java.util.List;

public interface AdminLeaveBalanceService {

    List<AdminLeaveBalanceResponseDto> fetchUserLeaves(String uuid);
}
