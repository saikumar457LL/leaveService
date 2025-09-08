package org.ocean.leaveservice.controller;

import lombok.RequiredArgsConstructor;
import org.ocean.leaveservice.dto.admin.AdminLeaveAdjustRequestDto;
import org.ocean.leaveservice.responses.AdminLeaveBalanceResponseDto;
import org.ocean.leaveservice.responses.ApiResponse;
import org.ocean.leaveservice.service.AdminLeaveBalanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/leave/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','HR','SUPER')")
public class LeaveAdminController {

    private final AdminLeaveBalanceService adminLeaveBalanceService;


    @GetMapping("/fetch_user_leaves")
    public ResponseEntity<ApiResponse<List<AdminLeaveBalanceResponseDto>>> fetchUserLeaves(@RequestParam String uuid) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.<List<AdminLeaveBalanceResponseDto>>builder()
                                .success(true)
                                .message("success")
                                .statusCode(HttpStatus.OK.value())
                                .timestamp(LocalDateTime.now())
                                .data(adminLeaveBalanceService.fetchUserLeaves(uuid))
                                .build()
                );
    }

    @PostMapping("/adjust_user_leaves")
    public ResponseEntity<ApiResponse<List<AdminLeaveBalanceResponseDto>>> adjustUserLeaves(@RequestBody @Validated AdminLeaveAdjustRequestDto leaveAdjustRequestDto) {
        List<AdminLeaveBalanceResponseDto> adjustedUserLeaves = adminLeaveBalanceService.adjustUserLeaves(leaveAdjustRequestDto);

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.<List<AdminLeaveBalanceResponseDto>>builder()
                                .success(true)
                                .message("Leaves adjusted Successfully")
                                .statusCode(HttpStatus.OK.value())
                                .data(adjustedUserLeaves)
                                .statusCode(HttpStatus.OK.value())
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }
}
