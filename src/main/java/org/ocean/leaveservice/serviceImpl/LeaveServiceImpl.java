package org.ocean.leaveservice.serviceImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ocean.leaveservice.clients.AuthServiceClient;
import org.ocean.leaveservice.clients.response.UsernamesFromUuidsResponse;
import org.ocean.leaveservice.constants.LeaveStatus;
import org.ocean.leaveservice.dto.UserLeaveRequestDto;
import org.ocean.leaveservice.dto.admin.AdminLeaveAdjustRequestDto;
import org.ocean.leaveservice.dto.admin.LeaveStatusChangeRequest;
import org.ocean.leaveservice.dto.admin.UserDetailsRequestFromUuid;
import org.ocean.leaveservice.entity.LeaveBalances;
import org.ocean.leaveservice.entity.LeaveRequest;
import org.ocean.leaveservice.entity.LeaveType;
import org.ocean.leaveservice.exceptions.LeaveException;
import org.ocean.leaveservice.mappers.LeaveStatusMapper;
import org.ocean.leaveservice.mappers.UserLeaveBalanceMapper;
import org.ocean.leaveservice.mappers.UserLeaveResponseMapper;
import org.ocean.leaveservice.mappers.admin.AdminLeaveBalanceMapper;
import org.ocean.leaveservice.repository.LeaveBalancesRepository;
import org.ocean.leaveservice.repository.LeaveRequestRepository;
import org.ocean.leaveservice.repository.LeaveTypeRepository;
import org.ocean.leaveservice.responses.*;
import org.ocean.leaveservice.service.AdminLeaveService;
import org.ocean.leaveservice.service.UserLeaveService;
import org.ocean.leaveservice.utils.DateTimeUtils;
import org.ocean.leaveservice.utils.MailUtils;
import org.ocean.leaveservice.utils.UserUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.AbstractMap;
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
    private final LeaveStatusMapper leaveStatusMapper;


    // ADMIN ACTIONS


    @Transactional
    @Override
    public org.ocean.leaveservice.responses.LeaveStatus approveOrReject(LeaveStatusChangeRequest leaveStatusChangeRequest) {

        LeaveStatus leaveStatus = null;
        try {
            leaveStatus = LeaveStatus.valueOf(leaveStatusChangeRequest.getLeaveStatus());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw new LeaveException("Given leave status not found","Please contact support");
        }

        if(leaveStatus.equals(LeaveStatus.PENDING) || leaveStatus.equals(LeaveStatus.CANCELED)) {
            throw new LeaveException("Illegal Leave Status Change","You can only reject/approve the leaves");
        }

        UUID actingUser = UUID.fromString(userUtils.getUserId());
        UUID leaveId = UUID.fromString(leaveStatusChangeRequest.getLeaveId());

        LeaveRequest requestedLeave = leaveRequestRepository.findByApproverAndUuid(actingUser, leaveId).orElseThrow(() -> new LeaveException("Leave not found", "May be you are not the approver for this leave"));
        requestedLeave.setStatus(leaveStatus);

        UUID requestedUserUuid = requestedLeave.getUser();

        LeaveBalances requestedUserLeaveBalanceState = leaveBalancesRepository.findByUserAndLeaveType_Code(requestedUserUuid, requestedLeave.getLeaveType().getCode());

        if(leaveStatus.equals(LeaveStatus.REJECTED)) {
            requestedUserLeaveBalanceState.setAvailableLeaves(requestedUserLeaveBalanceState.getAvailableLeaves() + 1);
            requestedUserLeaveBalanceState.setUsedLeaves(requestedUserLeaveBalanceState.getUsedLeaves() - 1);
        }

        LeaveRequest modifiedLeaveStatus = leaveRequestRepository.save(requestedLeave);
        leaveBalancesRepository.save(requestedUserLeaveBalanceState);

        ApiResponse<List<UsernamesFromUuidsResponse>> userDetailsResponse = authServiceClient.getUserNamesFromUuids(
                UserDetailsRequestFromUuid.builder()
                        .uuids(List.of(actingUser.toString(), requestedUserUuid.toString()))
                        .build()
        );

        Map<String, UsernamesFromUuidsResponse> extractedUserDetails = userDetailsResponse.getData().stream()
                .map(userResponse -> new AbstractMap.SimpleEntry<>(userResponse.getUuid(), userResponse))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        mailUtils.sendMail(
                extractedUserDetails.get(actingUser.toString()).getEmail(),
                extractedUserDetails.get(requestedUserUuid.toString()).getEmail(),
                "Your Leave Status Changed: "+leaveStatus.toString(),
                "Your recent leave is " + leaveStatus.toString()
        );

        return leaveStatusMapper.toDto(modifiedLeaveStatus);
    }

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


    @Transactional
    @Override
    public void cancelLeave(String leaveUuid) {
        String userId = userUtils.getUserId();

        UUID userUuid = UUID.fromString(userId);
        UUID leaveId = UUID.fromString(leaveUuid);

        LeaveRequest userAppliedLeave = leaveRequestRepository.findByUserAndUuid(userUuid, leaveId).orElseThrow(() -> new LeaveException("Leave not found", "Requested Leave not found"));

        if(userAppliedLeave.getStatus().equals(LeaveStatus.APPROVED) || userAppliedLeave.getStatus().equals(LeaveStatus.REJECTED)) {
            throw new LeaveException("Unable to cancel the leave", "Leave Already approved/rejected\nFor Cancelling contact you HR/Manager");
        }

        userAppliedLeave.setStatus(LeaveStatus.CANCELED);
        leaveRequestRepository.save(userAppliedLeave);

        LeaveBalances leaveBalanceAdjust = leaveBalancesRepository.findByUserAndLeaveType_Code(userUuid, userAppliedLeave.getLeaveType().getCode());
        leaveBalanceAdjust.setUsedLeaves(leaveBalanceAdjust.getUsedLeaves() - 1);
        leaveBalanceAdjust.setAvailableLeaves(leaveBalanceAdjust.getAvailableLeaves() + 1);

        leaveBalancesRepository.save(leaveBalanceAdjust);

        String approverUuid = userAppliedLeave.getApprover().toString();

        ApiResponse<List<UsernamesFromUuidsResponse>> userEmailDetails = authServiceClient.getUserNamesFromUuids(
                UserDetailsRequestFromUuid.builder()
                        .uuids(List.of(userId, approverUuid))
                        .build()
        );

        Map<String, UsernamesFromUuidsResponse> formattedUserDetails = userEmailDetails.getData().stream()
                .map(userResponse -> new AbstractMap.SimpleEntry<>(userResponse.getUuid(), userResponse))
                .collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue));

        UsernamesFromUuidsResponse loggedInUserDetails = formattedUserDetails.get(userId);
        UsernamesFromUuidsResponse approverUserDetails = formattedUserDetails.get(approverUuid);

        mailUtils.sendMail(
                loggedInUserDetails.getEmail(),approverUserDetails.getEmail(),
                loggedInUserDetails.getUsername()+" Canceled the Leave",
                "Type of Leave: " + getLeaveType(userAppliedLeave.getLeaveType().getCode())
                + "\nLeave Reason: " + userAppliedLeave.getReason()
        );
    }


    @Override
    public List<org.ocean.leaveservice.responses.LeaveStatus> fetchAllLeaveStatus() {
        UUID userUuid = UUID.fromString(userUtils.getUserId());
        List<LeaveRequest> userPendingLeaves = leaveRequestRepository.findAllByUser(userUuid);
        return userPendingLeaves.stream().map(leaveStatusMapper::toDto).toList();
    }

    @Override
    public List<UserLeaveBalancesResponseDto> getMyLeaveBalances() {
        String userId = userUtils.getUserId();
        return leaveBalancesRepository.findAllByUser(UUID.fromString(userId)).stream()
                .map(userLeaveBalanceMapper::toDto).toList();
    }

    @Override
    public org.ocean.leaveservice.responses.LeaveStatus getLeaveStatus(String leaveId) {
        UUID userId = UUID.fromString(userUtils.getUserId());
        UUID leave = UUID.fromString(leaveId);
        LeaveRequest appliedLeave = leaveRequestRepository.findByUserAndUuid(userId, leave).orElseThrow(() -> new LeaveException("Leave not found", "Leave not found"));
        return leaveStatusMapper.toDto(appliedLeave);
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
        markedForLeave.setUuid(UUID.randomUUID());

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
                        "leave_apply@ocean.com",
                        userEmails.get(leaveRequest.getApprover()),
                        "Leave Request For "+userUtils.getUserName(),
                        "Type of Leave: " + getLeaveType(leaveRequest.getLeaveType()) +"\n" +
                                "Leave Reason: "+leaveRequest.getReason()
                );

        return userLeaveResponseMapper.toDto(savedLeave);
    }

    // private methods

    private int plus(int a, int b) {
        return a + b;
    }

    private int minus(int a, int b) {
        return a - b;
    }
    private String getLeaveType(String code) {
        return switch (code) {
            case "EL" -> "EARNED";
            case "CML" -> "CAMP-OFF";
            case "ML" -> "MATERNITY LEAVE";
            case "SL" -> "SICK LEAVE";
            default -> "CASUAL";
        };
    }
}
