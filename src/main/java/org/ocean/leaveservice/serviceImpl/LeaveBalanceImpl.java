package org.ocean.leaveservice.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ocean.leaveservice.dao.LeaveBalancesDto;
import org.ocean.leaveservice.repository.LeaveBalancesRepository;
import org.ocean.leaveservice.service.LeaveBalance;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LeaveBalanceImpl implements LeaveBalance {

    private final LeaveBalancesRepository leaveBalancesRepository;

    @Override
    public List<LeaveBalancesDto> getLeaveBalances() {
        return List.of();
    }
}
