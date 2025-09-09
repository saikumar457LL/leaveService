package org.ocean.leaveservice.controller;

import lombok.RequiredArgsConstructor;
import org.ocean.leaveservice.dto.UserLeaveRequestDto;
import org.ocean.leaveservice.responses.ApiResponse;
import org.ocean.leaveservice.responses.UserLeaveApplyResponseDto;
import org.ocean.leaveservice.responses.UserLeaveBalancesResponseDto;
import org.ocean.leaveservice.service.UserLeaveBalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {

    private final UserLeaveBalanceService userLeaveBalanceService;
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<UserLeaveApplyResponseDto>> applyLeave(@RequestBody @Validated UserLeaveRequestDto leaveRequest) {

        UserLeaveApplyResponseDto userLeaveApplyResponse = userLeaveBalanceService.applyLeave(leaveRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                  ApiResponse.<UserLeaveApplyResponseDto>builder()
                          .success(true)
                          .data(userLeaveApplyResponse)
                          .message("Leave applied successfully")
                          .statusCode(HttpStatus.OK.value())
                          .timestamp(LocalDateTime.now())
                          .build()
                );
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
}
