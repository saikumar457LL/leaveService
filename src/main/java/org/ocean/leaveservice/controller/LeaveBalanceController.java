package org.ocean.leaveservice.controller;

import lombok.RequiredArgsConstructor;
import org.ocean.leaveservice.dao.UserLeaveBalancesDto;
import org.ocean.leaveservice.dao.admin.AdminLeaveBalanceDto;
import org.ocean.leaveservice.responses.ApiResponse;
import org.ocean.leaveservice.service.AdminLeaveBalanceService;
import org.ocean.leaveservice.service.UserLeaveBalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveBalanceController {

    private final UserLeaveBalanceService userLeaveBalanceService;
    private final AdminLeaveBalanceService adminLeaveBalanceService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserLeaveBalancesDto>>> fetchAllMyLeaves() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.<List<UserLeaveBalancesDto>>builder()
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
    public ResponseEntity<ApiResponse<List<AdminLeaveBalanceDto>>> fetchAllUserLeaves() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.<List<AdminLeaveBalanceDto>>builder()
                                .success(true)
                                .message("success")
                                .statusCode(HttpStatus.OK.value())
                                .timestamp(LocalDateTime.now())
                                .data(adminLeaveBalanceService.getAllUserLeaveBalances())
                                .build()
                );
    }
}
