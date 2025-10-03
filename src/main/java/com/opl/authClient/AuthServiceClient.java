package com.opl.authClient;

import com.opl.request.AuthRequest;
import com.opl.request.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.opl.request.SecurityUserDTO;

@FeignClient(
        name = "auth-client",
        url = "${auth.service.url}"
)
public interface AuthServiceClient {

    @PostMapping("/auth/register")
    ResponseEntity<SecurityUserDTO> registerUserInternal(@RequestBody SecurityUserDTO securityUser);

    @PostMapping("/auth/login")
    ResponseEntity<AuthResponse> generateJWT(@RequestBody AuthRequest authRequest);
}
