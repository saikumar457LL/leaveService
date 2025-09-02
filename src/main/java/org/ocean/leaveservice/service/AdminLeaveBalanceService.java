package org.ocean.leaveservice.service;

import org.ocean.leaveservice.dao.admin.AdminLeaveBalanceDto;

import java.util.List;

public interface AdminLeaveBalanceService {

    List<AdminLeaveBalanceDto> getAllUserLeaveBalances();
}
