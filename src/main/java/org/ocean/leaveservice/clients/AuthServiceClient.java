package org.ocean.leaveservice.clients;

import org.ocean.leaveservice.clients.response.UsernamesFromUuidsResponse;
import org.ocean.leaveservice.responses.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "authService", url = "http://localhost:8080")
public interface AuthServiceClient {
    @PostMapping("/admin/get_user_names_from_uuid")
    ApiResponse<List<UsernamesFromUuidsResponse>> getUserNamesFromUuids();
}
