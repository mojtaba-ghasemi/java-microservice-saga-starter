package io.github.mojtaba.microservice.starter.saga.orchestrator.service;

import io.github.mojtaba.microservice.starter.saga.orchestrator.jwt.JWTUserDetails;
import io.github.mojtaba.microservice.starter.saga.orchestrator.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JWTUtil jwtUtil;

    public String generateIamLoginJwtToken(String username, Set<String> roleTitles, Set<String> claims) {
        return jwtUtil.generateIamToken(
                new JWTUserDetails(username, roleTitles, claims)
        );
    }

    public String extractUsernameFromToken(String token){
        return jwtUtil.getJwtDataDtoFromToken(token).getUsername();
    }

}
