package org.ocean.leaveservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserUtils {


    private Jwt getJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Jwt) authentication.getPrincipal();
    }

    public String getUserName(){
        Jwt jwt = getJwt();
        return jwt.getClaim("preferred_username");
    }

    public String getUserId(){
        Jwt jwt = getJwt();
        return jwt.getSubject();
    }

}
