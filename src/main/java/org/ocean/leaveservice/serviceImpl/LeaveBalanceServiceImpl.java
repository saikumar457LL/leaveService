package org.ocean.leaveservice.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ocean.leaveservice.constants.LeaveStatus;
import org.ocean.leaveservice.entity.LeaveRequest;
import org.ocean.leaveservice.entity.LeaveType;
import org.ocean.leaveservice.repository.LeaveTypeRepository;
import org.ocean.leaveservice.responses.UserLeaveBalancesResponseDto;
import org.ocean.leaveservice.dto.UserLeaveRequestDto;
import org.ocean.leaveservice.responses.AdminLeaveBalanceResponseDto;
import org.ocean.leaveservice.entity.LeaveBalances;
import org.ocean.leaveservice.exceptions.LeaveException;
import org.ocean.leaveservice.mappers.UserLeaveResponseMapper;
import org.ocean.leaveservice.mappers.UserLeaveBalanceMapper;
import org.ocean.leaveservice.mappers.admin.AdminLeaveBalanceMapper;
import org.ocean.leaveservice.repository.LeaveBalancesRepository;
import org.ocean.leaveservice.repository.LeaveRequestRepository;
import org.ocean.leaveservice.service.AdminLeaveBalanceService;
import org.ocean.leaveservice.service.UserLeaveBalanceService;
import org.ocean.leaveservice.utils.UserUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LeaveBalanceServiceImpl implements UserLeaveBalanceService , AdminLeaveBalanceService {

    private final LeaveBalancesRepository leaveBalancesRepository;
    private final UserUtils userUtils;
    private final UserLeaveBalanceMapper userLeaveBalanceMapper;
    private final AdminLeaveBalanceMapper adminLeaveBalanceMapper;
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserLeaveResponseMapper userLeaveResponseMapper;
    private final LeaveTypeRepository leaveTypeRepository;

    @Override
    public List<UserLeaveBalancesResponseDto> getMyLeaveBalances() {
        String userId = userUtils.getUserId();
        return leaveBalancesRepository.findAllByUser(UUID.fromString(userId)).stream()
                .map(userLeaveBalanceMapper::toDto).toList();
    }

    @Override
    public List<AdminLeaveBalanceResponseDto> getAllUserLeaveBalances() {
        return leaveBalancesRepository.findAll().stream()
                .map(leaveBalance -> adminLeaveBalanceMapper.toDto(leaveBalance,userUtils)).toList();
    }

    @Transactional
    @Override
    public UserLeaveRequestDto applyLeave(UserLeaveRequestDto leaveRequest) {

        Map<String, Integer> availableLeaves = leaveBalancesRepository.findAllByUser(UUID.fromString(userUtils.getUserId()))
                .stream()
                .collect(Collectors.toMap(leaveBalances -> leaveBalances.getLeaveType().getCode(), LeaveBalances::getAvailableLeaves));

        availableLeaves.computeIfAbsent(leaveRequest.getLeaveType(), ex -> {
            throw new LeaveException(leaveRequest.getLeaveType(),"is not valid leaveType");
        });

        Duration numberOfLeaveHours = Duration.between(leaveRequest.getFromDate(), leaveRequest.getToDate());
        long numberOfLeaveDays = Duration.from(numberOfLeaveHours).toDays();

        Integer availablePerLeaveType = availableLeaves.get(leaveRequest.getLeaveType());

        if(numberOfLeaveDays > availablePerLeaveType) {
            throw new LeaveException("Leave " + leaveRequest.getLeaveType() + " available quota exceed","Currently you have " + availablePerLeaveType + " leaves for this leave type");
        }

        LeaveType leaveType = leaveTypeRepository.findByCode(leaveRequest.getLeaveType()).orElseThrow(() -> new LeaveException("Not found", leaveRequest.getLeaveType() + " is not found"));

        LeaveRequest markedForLeave = new LeaveRequest();
        markedForLeave.setLeaveType(leaveType);
        markedForLeave.setFromDate(leaveRequest.getFromDate());
        markedForLeave.setToDate(leaveRequest.getToDate());
        markedForLeave.setUser(UUID.fromString(userUtils.getUserId()));
        markedForLeave.setStatus(LeaveStatus.PENDING);
        leaveRequestRepository.save(markedForLeave);

        LeaveBalances byUserAndLeaveTypeCode = leaveBalancesRepository.findByUserAndLeaveType_Code(UUID.fromString(userUtils.getUserId()), leaveRequest.getLeaveType());
        byUserAndLeaveTypeCode.setAvailableLeaves(byUserAndLeaveTypeCode.getAvailableLeaves() - (int)numberOfLeaveDays);
        byUserAndLeaveTypeCode.setUsedLeaves(byUserAndLeaveTypeCode.getUsedLeaves() + (int)numberOfLeaveDays);
        leaveBalancesRepository.save(byUserAndLeaveTypeCode);
        return null;
    }
}
