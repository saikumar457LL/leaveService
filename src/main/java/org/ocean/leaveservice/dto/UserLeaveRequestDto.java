package org.ocean.leaveservice.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserLeaveRequestDto {
    @NotBlank
    private String leaveType;

    @NotNull
    @Future
    private LocalDateTime fromDate;

    @NotNull
    @Future
    private LocalDateTime toDate;
}
