package org.ocean.leaveservice.controller;

import lombok.RequiredArgsConstructor;
import org.ocean.leaveservice.dto.UserLeaveRequestDto;
import org.ocean.leaveservice.responses.AdminLeaveBalanceResponseDto;
import org.ocean.leaveservice.responses.ApiResponse;
import org.ocean.leaveservice.responses.UserLeaveBalancesResponseDto;
import org.ocean.leaveservice.service.AdminLeaveBalanceService;
import org.ocean.leaveservice.service.UserLeaveBalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final UserLeaveBalanceService userLeaveBalanceService;
    private final AdminLeaveBalanceService adminLeaveBalanceService;


    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<Void>> applyLeave(@RequestBody @Validated UserLeaveRequestDto leaveRequest) {

        userLeaveBalanceService.applyLeave(leaveRequest);
        return null;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserLeaveBalancesResponseDto>>> fetchAllMyLeaves() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.<List<UserLeaveBalancesResponseDto>>builder()
                                .success(true)
                                .message("success")
                                .statusCode(HttpStatus.OK.value())
                                .timestamp(LocalDateTime.now())
                                .data(userLeaveBalanceService.getMyLeaveBalances())
                                .build()
                );
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AdminLeaveBalanceResponseDto>>> fetchAllUserLeaves() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.<List<AdminLeaveBalanceResponseDto>>builder()
                                .success(true)
                                .message("success")
                                .statusCode(HttpStatus.OK.value())
                                .timestamp(LocalDateTime.now())
                                .data(adminLeaveBalanceService.getAllUserLeaveBalances())
                                .build()
                );
    }
}
