package org.ocean.leaveservice.config;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ocean.leaveservice.utils.UserUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeignConfig {

    private final UserUtils userUtils;

    @Bean
    RequestInterceptor authInterceptor() {
        return requestTemplate -> {
            String jwtToken = "Bearer " + userUtils.getJwt().getTokenValue();
            requestTemplate.header("Authorization",jwtToken);
        };
    }
}
