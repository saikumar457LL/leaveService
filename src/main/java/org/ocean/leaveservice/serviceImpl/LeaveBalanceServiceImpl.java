package org.ocean.leaveservice.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ocean.leaveservice.dao.UserLeaveBalancesDto;
import org.ocean.leaveservice.dao.admin.AdminLeaveBalanceDto;
import org.ocean.leaveservice.mappers.UserLeaveBalanceMapper;
import org.ocean.leaveservice.mappers.admin.AdminLeaveBalanceMapper;
import org.ocean.leaveservice.repository.LeaveBalancesRepository;
import org.ocean.leaveservice.service.AdminLeaveBalanceService;
import org.ocean.leaveservice.service.UserLeaveBalanceService;
import org.ocean.leaveservice.utils.UserUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LeaveBalanceServiceImpl implements UserLeaveBalanceService , AdminLeaveBalanceService {

    private final LeaveBalancesRepository leaveBalancesRepository;
    private final UserUtils userUtils;
    private final UserLeaveBalanceMapper userLeaveBalanceMapper;
    private final AdminLeaveBalanceMapper adminLeaveBalanceMapper;

    @Override
    public List<UserLeaveBalancesDto> getMyLeaveBalances() {
        String userId = userUtils.getUserId();
        return leaveBalancesRepository.findAllByUser(UUID.fromString(userId)).stream()
                .map(userLeaveBalanceMapper::toDto).toList();
    }

    @Override
    public List<AdminLeaveBalanceDto> getAllUserLeaveBalances() {
        return leaveBalancesRepository.findAll().stream()
                .map(leaveBalance -> adminLeaveBalanceMapper.toDto(leaveBalance,userUtils)).toList();
    }
}
