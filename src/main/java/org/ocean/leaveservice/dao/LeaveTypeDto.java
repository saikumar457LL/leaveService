package org.ocean.leaveservice.dao;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LeaveTypeDto {
    private String name;
    private String description;
}
