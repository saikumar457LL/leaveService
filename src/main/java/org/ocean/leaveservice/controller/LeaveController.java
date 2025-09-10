package org.ocean.leaveservice.controller;

import lombok.RequiredArgsConstructor;
import org.ocean.leaveservice.dto.UserLeaveRequestDto;
import org.ocean.leaveservice.responses.ApiResponse;
import org.ocean.leaveservice.responses.LeaveStatus;
import org.ocean.leaveservice.responses.UserLeaveApplyResponseDto;
import org.ocean.leaveservice.responses.UserLeaveBalancesResponseDto;
import org.ocean.leaveservice.service.UserLeaveService;
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

    private final UserLeaveService userLeaveService;
    @PostMapping("/apply")
    public ResponseEntity<ApiResponse<UserLeaveApplyResponseDto>> applyLeave(@RequestBody @Validated UserLeaveRequestDto leaveRequest) {

        UserLeaveApplyResponseDto userLeaveApplyResponse = userLeaveService.applyLeave(leaveRequest);

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

    @GetMapping("/balances")
    public ResponseEntity<ApiResponse<List<UserLeaveBalancesResponseDto>>> fetchAllMyLeaves() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.<List<UserLeaveBalancesResponseDto>>builder()
                                .success(true)
                                .message("success")
                                .statusCode(HttpStatus.OK.value())
                                .timestamp(LocalDateTime.now())
                                .data(userLeaveService.getMyLeaveBalances())
                                .build()
                );
    }

    @GetMapping("/applied_status")
    public ResponseEntity<ApiResponse<List<LeaveStatus>>> fetchAppliedLeaves() {

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.<List<LeaveStatus>>builder()
                                .success(true)
                                .message("Pending leaves")
                                .statusCode(HttpStatus.OK.value())
                                .data(userLeaveService.fetchAllLeaveStatus())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }

    @PatchMapping("/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelLeave(@RequestParam String leaveId) {

        userLeaveService.cancelLeave(leaveId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.<Void>builder()
                                .statusCode(HttpStatus.OK.value())
                                .timestamp(LocalDateTime.now())
                                .success(true)
                                .message("Leave Cancelled Successfully")
                                .build()
                );
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<LeaveStatus>> fetchLeaveStatus(@RequestParam String leaveId) {
        LeaveStatus leaveStatus = userLeaveService.getLeaveStatus(leaveId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                  ApiResponse.<LeaveStatus>builder()
                          .success(true)
                          .message("Leave Status fetched successfully")
                          .statusCode(HttpStatus.OK.value())
                          .data(leaveStatus)
                          .timestamp(LocalDateTime.now())
                          .build()
                );
    }
}
