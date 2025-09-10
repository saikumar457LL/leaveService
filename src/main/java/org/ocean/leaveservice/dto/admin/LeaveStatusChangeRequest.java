package org.ocean.leaveservice.dto.admin;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveStatusChangeRequest {
    @NotBlank
    private String leaveId;
    @NotBlank
    private String leaveStatus;
}
