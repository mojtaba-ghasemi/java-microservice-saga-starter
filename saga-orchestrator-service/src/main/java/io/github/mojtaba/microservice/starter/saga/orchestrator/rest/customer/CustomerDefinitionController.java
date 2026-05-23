package io.github.mojtaba.microservice.starter.saga.orchestrator.rest.customer;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.SignUpRequestDto;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.UserDto;
import io.github.mojtaba.microservice.starter.saga.orchestrator.service.AuthService;
import io.github.mojtaba.microservice.starter.saga.orchestrator.service.iam.IdentityManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/customer/definition")
@RequiredArgsConstructor
public class CustomerDefinitionController {

    private final IdentityManagementService serviceEntity;

    @PostMapping("/auth/signup-customer")
    public ResponseEntity<UserDto> signupCustomer(@RequestBody @Valid SignUpRequestDto requestDto) throws JsonProcessingException {
        return ResponseEntity.ok(serviceEntity.signupCustomer(requestDto));
    }
}
