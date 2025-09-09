package org.ocean.leaveservice.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserDetailsRequestFromUuid {
    private List<String> uuids;
}
