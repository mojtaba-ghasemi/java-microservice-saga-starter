package io.github.mojtaba.microservice.starter.saga.orchestrator.rest.bacoffice;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.*;
import io.github.mojtaba.microservice.starter.saga.orchestrator.service.AuthService;
import io.github.mojtaba.microservice.starter.saga.orchestrator.service.iam.IdentityManagementService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/backoffice/iam")
@RequiredArgsConstructor
public class IAMController {
    private final AuthService authService;
    private final IdentityManagementService serviceEntity;

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid UserLoginDto loginDto, HttpServletResponse response) throws JsonProcessingException {
        LoginResponseDto loginResponse = serviceEntity.login(loginDto);
        if(loginResponse.isAcceptTermsAndConditions())
            response.setHeader(HttpHeaders.AUTHORIZATION,
                    authService.generateIamLoginJwtToken(
                            loginDto.getUsername(),
                            Mappers.rolesMapper(loginResponse.getRoles()),
                            Mappers.claimsMapper(loginResponse.getRoles())
                    )
            );
        return ResponseEntity.ok(loginResponse.setRoles(null));
    }

    @PostMapping("/auth/signup")
    @PreAuthorize("hasAnyAuthority('ALL','USER_MANAGEMENT_CREATE','ADMIN')")
    public ResponseEntity<UserDto> signup(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
            , @RequestBody @Valid SignUpRequestDto requestDto) throws JsonProcessingException {
        return ResponseEntity.ok(serviceEntity.signup(requestDto, authorization));
    }

    private static class Mappers {
        static Set<String> rolesMapper(Collection<RoleDto> roles) {
            if (CollectionUtils.isEmpty(roles)) {
                return new HashSet<>();
            }
            return roles.stream().map(RoleDto::getTitle).collect(Collectors.toSet());
        }

        static Set<String> claimsMapper(Collection<RoleDto> roles) {
            if (CollectionUtils.isEmpty(roles)) {
                return new HashSet<>();
            }
            return roles.stream().flatMap(
                    roleDto -> roleDto.getClaims().stream().map(Enum::name)
            ).collect(Collectors.toSet());
        }
    }
}
