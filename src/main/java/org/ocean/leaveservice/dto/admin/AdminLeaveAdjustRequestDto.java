package org.ocean.leaveservice.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLeaveAdjustRequestDto {
    @NotBlank
    private String userId;
    @NotBlank
    private String leaveType;
    @Min(value = 0)
    private Integer numberOfLeaves;
}
