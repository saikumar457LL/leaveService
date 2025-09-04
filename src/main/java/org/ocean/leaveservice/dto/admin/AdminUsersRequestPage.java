package org.ocean.leaveservice.dto.admin;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AdminUsersRequestPage {
    private int currentPage;
    private int pageSize;
}
