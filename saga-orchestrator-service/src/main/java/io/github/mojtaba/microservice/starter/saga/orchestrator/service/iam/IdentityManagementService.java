package io.github.mojtaba.microservice.starter.saga.orchestrator.service.iam;


import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IdentityManagementService {
    LoginResponseDto login(UserLoginDto credential) throws JsonProcessingException;
    LoginResponseDto acceptTermsAndConditions(UserLoginDto credential) throws JsonProcessingException;

    UserDto signup(SignUpRequestDto requestDto , String authorization) throws JsonProcessingException;
    UserDto signupCustomer(SignUpRequestDto requestDto) throws JsonProcessingException;


    Page<UserDto> getAllSupporters(UserSearchRequestDto requestDto) throws JsonProcessingException;

    void updateUser(UserUpdateRequestDto requestDto , String authorization) throws JsonProcessingException;

    void changePassword(ChangePasswordRequestDto requestDto) throws JsonProcessingException;

    void resetPassword(ResetPasswordRequestDto requestDto) throws JsonProcessingException;

    List<RoleDto> getAllRoles(RoleDto roleDto) throws JsonProcessingException;

    void saveRole(RoleDto roleDto) throws JsonProcessingException;

    void deleteRole(String roleTitle) throws JsonProcessingException;
}