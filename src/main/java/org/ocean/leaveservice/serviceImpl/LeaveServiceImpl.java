package org.ocean.leaveservice.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ocean.leaveservice.clients.AuthServiceClient;
import org.ocean.leaveservice.clients.response.UsernamesFromUuidsResponse;
import org.ocean.leaveservice.constants.LeaveStatus;
import org.ocean.leaveservice.dto.UserLeaveRequestDto;
import org.ocean.leaveservice.dto.admin.AdminLeaveAdjustRequestDto;
import org.ocean.leaveservice.dto.admin.UserDetailsRequestFromUuid;
import org.ocean.leaveservice.entity.LeaveBalances;
import org.ocean.leaveservice.entity.LeaveRequest;
import org.ocean.leaveservice.entity.LeaveType;
import org.ocean.leaveservice.exceptions.LeaveException;
import org.ocean.leaveservice.mappers.UserLeaveBalanceMapper;
import org.ocean.leaveservice.mappers.UserLeaveResponseMapper;
import org.ocean.leaveservice.mappers.admin.AdminLeaveBalanceMapper;
import org.ocean.leaveservice.repository.LeaveBalancesRepository;
import org.ocean.leaveservice.repository.LeaveRequestRepository;
import org.ocean.leaveservice.repository.LeaveTypeRepository;
import org.ocean.leaveservice.responses.AdminLeaveBalanceResponseDto;
import org.ocean.leaveservice.responses.ApiResponse;
import org.ocean.leaveservice.responses.UserLeaveApplyResponseDto;
import org.ocean.leaveservice.responses.UserLeaveBalancesResponseDto;
import org.ocean.leaveservice.service.AdminLeaveService;
import org.ocean.leaveservice.service.UserLeaveService;
import org.ocean.leaveservice.utils.DateTimeUtils;
import org.ocean.leaveservice.utils.MailUtils;
import org.ocean.leaveservice.utils.UserUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class LeaveServiceImpl implements UserLeaveService, AdminLeaveService {

    private final LeaveBalancesRepository leaveBalancesRepository;
    private final UserUtils userUtils;
    private final UserLeaveBalanceMapper userLeaveBalanceMapper;
    private final LeaveRequestRepository leaveRequestRepository;
    private final UserLeaveResponseMapper userLeaveResponseMapper;
    private final LeaveTypeRepository leaveTypeRepository;
    private final AdminLeaveBalanceMapper adminLeaveBalanceMapper;
    private final AuthServiceClient authServiceClient;
    private final MailUtils mailUtils;


    // ADMIN ACTIONS

    @Override
    public List<AdminLeaveBalanceResponseDto> fetchUserLeaves(String uuid) {
        UUID userUuid = UUID.fromString(uuid);
        List<LeaveBalances> userLeaves = leaveBalancesRepository.findAllByUser(userUuid);
        return userLeaves.stream().map(adminLeaveBalanceMapper::toDto).toList();
    }

    @Override
    public List<AdminLeaveBalanceResponseDto> adjustUserLeaves(AdminLeaveAdjustRequestDto leaveAdjustRequestDto) {
        UUID userUuid = UUID.fromString(leaveAdjustRequestDto.getUserId());
        LeaveBalances leaveBalancesToAdjust = leaveBalancesRepository.findByUserAndLeaveType_Code(userUuid, leaveAdjustRequestDto.getLeaveType());
        leaveBalancesToAdjust.setAvailableLeaves(leaveAdjustRequestDto.getNumberOfLeaves());
        leaveBalancesRepository.save(leaveBalancesToAdjust);
        return leaveBalancesRepository.findAllByUser(userUuid).stream().map(adminLeaveBalanceMapper::toDto).toList();
    }

    // USER ACTIONS

    @Override
    public List<UserLeaveBalancesResponseDto> getMyLeaveBalances() {
        String userId = userUtils.getUserId();
        return leaveBalancesRepository.findAllByUser(UUID.fromString(userId)).stream()
                .map(userLeaveBalanceMapper::toDto).toList();
    }

    // TODO Implement half-day leave
    @Transactional
    @Override
    public UserLeaveApplyResponseDto applyLeave(UserLeaveRequestDto leaveRequest) {

        if (DateTimeUtils.isBeforeToday(leaveRequest.getFromDate()) || DateTimeUtils.isBeforeToday(leaveRequest.getToDate())) {
            throw new LeaveException("Invalid Date",
                    "From-Date and To-Date must be today or in the future, not past dates");
        }

        if (leaveRequest.getFromDate().isAfter(leaveRequest.getToDate())) {
            throw new LeaveException("Invalid Date Range", "From-Date cannot be after To-Date");
        }


        UUID userId = UUID.fromString(userUtils.getUserId());

        Map<String, Integer> availableLeaves = leaveBalancesRepository.findAllByUser(userId)
                .stream()
                .collect(Collectors.toMap(leaveBalances -> leaveBalances.getLeaveType().getCode(), LeaveBalances::getAvailableLeaves));

        availableLeaves.computeIfAbsent(leaveRequest.getLeaveType(), ex -> {
            throw new LeaveException(leaveRequest.getLeaveType(),"is not a valid leave type");
        });

        Duration numberOfLeaveHours = Duration.between(leaveRequest.getFromDate(), leaveRequest.getToDate());
        long numberOfLeaveDays = ChronoUnit.DAYS.between(leaveRequest.getFromDate().toLocalDate(), leaveRequest.getToDate().toLocalDate())+1;

        log.info("Leave Hours: {}",numberOfLeaveHours);
        log.info("Leave Days: {}",numberOfLeaveDays);

        Integer availablePerLeaveType = availableLeaves.get(leaveRequest.getLeaveType());

        if(numberOfLeaveDays > availablePerLeaveType) {
            throw new LeaveException(
                    "Leave quota exceeded",
                    "You requested " + numberOfLeaveDays + " days, but only " + availablePerLeaveType + " are available."
            );
        }

        LeaveType leaveType = leaveTypeRepository.findByCode(leaveRequest.getLeaveType()).orElseThrow(() -> new LeaveException("Not found", leaveRequest.getLeaveType() + " is not found"));

        LeaveRequest markedForLeave = new LeaveRequest();
        markedForLeave.setLeaveType(leaveType);
        markedForLeave.setFromDate(leaveRequest.getFromDate());
        markedForLeave.setToDate(leaveRequest.getToDate());
        markedForLeave.setUser(userId);
        markedForLeave.setStatus(LeaveStatus.PENDING);
        markedForLeave.setReason(leaveRequest.getReason());
        markedForLeave.setApprover(UUID.fromString(leaveRequest.getApprover()));

        LeaveRequest savedLeave = leaveRequestRepository.save(markedForLeave);

        LeaveBalances byUserAndLeaveTypeCode = leaveBalancesRepository.findByUserAndLeaveType_Code(userId, leaveRequest.getLeaveType());
        byUserAndLeaveTypeCode.setAvailableLeaves(minus(byUserAndLeaveTypeCode.getAvailableLeaves(), (int)numberOfLeaveDays));
        byUserAndLeaveTypeCode.setUsedLeaves(plus(byUserAndLeaveTypeCode.getUsedLeaves(), (int)numberOfLeaveDays));

        leaveBalancesRepository.save(byUserAndLeaveTypeCode);

        ApiResponse<List<UsernamesFromUuidsResponse>> userDetailsApiResponse = authServiceClient.getUserNamesFromUuids(
                UserDetailsRequestFromUuid.builder()
                        .uuids(List.of(leaveRequest.getApprover(), userUtils.getUserId()))
                        .build()
        );

        Map<String, String> userEmails = userDetailsApiResponse.getData()
                .stream()
                .collect(Collectors.toMap(
                        UsernamesFromUuidsResponse::getUuid,
                        UsernamesFromUuidsResponse::getEmail,
                        (value1, value2) -> value1
                ));

        mailUtils.sendMail(
                        userEmails.get(userUtils.getUserId()),
                        userEmails.get(leaveRequest.getApprover()),
                        "Leave Request",
                        "Type of Leave: " + leaveRequest.getLeaveType() +"\n" +
                                leaveRequest.getReason()
                );

        return userLeaveResponseMapper.toDto(savedLeave);
    }

    private int plus(int a, int b) {
        return a + b;
    }

    private int minus(int a, int b) {
        return a - b;
    }
}
