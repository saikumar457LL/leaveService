package org.ocean.leaveservice.dao.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminLeaveTypeDto {
    private String name;
    private String description;
}
