package io.github.mojtaba.microservice.starter.saga.orchestrator.service.iam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.*;
import io.github.mojtaba.microservice.starter.saga.orchestrator.dto.SequenceRepository;
import io.github.mojtaba.microservice.starter.saga.orchestrator.executer.SagaSequenceTemplate;
import io.github.mojtaba.microservice.starter.saga.orchestrator.executer.SequenceExecuter;
import io.github.mojtaba.microservice.starter.saga.orchestrator.executer.SequenceUtil;
import io.github.mojtaba.microservice.starter.saga.orchestrator.jwt.JWTUtil;
import io.github.mojtaba.microservice.starter.saga.orchestrator.jwt.JwtDataDto;
import io.github.mojtaba.microservice.starter.saga.orchestrator.model.SagaSequence;
import io.github.mojtaba.microservice.starter.shared.model.common.CommandType;
import io.github.mojtaba.microservice.starter.shared.model.common.DeserializableSpringDataPage;
import io.github.mojtaba.microservice.starter.shared.model.common.SagaWrapperCommand;
import io.github.mojtaba.microservice.starter.shared.model.common.SequenceTitle;
import io.github.mojtaba.microservice.starter.shared.model.enums.WrapperCommandOnRollbackBehavior;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static io.github.mojtaba.microservice.starter.shared.model.util.JsonUtil.parseJson;


@Service
@RequiredArgsConstructor
public class IdentityManagementServiceImpl implements IdentityManagementService {

    private final SequenceExecuter sequenceExecuter;
    private final SequenceRepository sequenceRepository;
    private final ObjectMapper objMapper = new ObjectMapper();
    private final SagaSequenceTemplate sagaSequenceTemplate;
    private final JWTUtil jwtUtil;

    @Value("${rabbitmq.identityManagement.routing.key}")
    String identityManagementRoutingKey;

    @Override
    public UserDto signup(SignUpRequestDto requestDto , String authorization) throws JsonProcessingException {

        if(authorization!=null) {
            JwtDataDto token = jwtUtil.getJwtDataDtoFromToken(authorization);
            requestDto.getBaseUser().setCreator(token.getUsername());
        }

        SagaSequence iamSignup = SequenceUtil.generateSequence("IAM_SIGNUP");
        SagaWrapperCommand addMessageCommand = SequenceUtil.generateWrapperCommand("IAM_SIGNUP",
                iamSignup.getId(), identityManagementRoutingKey, CommandType.IAM_SIGNUP, true,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        addMessageCommand.setCommandRequestDto(requestDto);
        iamSignup.getCommandList().add(addMessageCommand);
        sequenceRepository.save(iamSignup);
        sequenceExecuter.startSyncSequence(iamSignup);
        return parseJson(iamSignup.getCommandList().get(0).getResponse(), UserDto.class);
    }

    public UserDto signupCustomer(SignUpRequestDto requestDto) throws JsonProcessingException {

        requestDto.setAssignedRoles(List.of("CUSTOMER"));
        SagaSequence iamSignup = SequenceUtil.generateSequence("IAM_SIGNUP_CUSTOMER");
        SagaWrapperCommand addMessageCommand = SequenceUtil.generateWrapperCommand("IAM_SIGNUP_CUSTOMER",
                iamSignup.getId(), identityManagementRoutingKey, CommandType.IAM_SIGNUP_CUSTOMER, true,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        addMessageCommand.setCommandRequestDto(requestDto);
        iamSignup.getCommandList().add(addMessageCommand);
        sequenceRepository.save(iamSignup);
        sequenceExecuter.startSyncSequence(iamSignup);
        return parseJson(iamSignup.getCommandList().get(0).getResponse(), UserDto.class);
    }


    @Override
    public Page<UserDto> getAllSupporters(UserSearchRequestDto requestDto) throws JsonProcessingException {
        SagaSequence getAll = SequenceUtil.generateSequence("IAM_GET_ALL_SUPPORTERS");
        SagaWrapperCommand getAllCommand = SequenceUtil.generateWrapperCommand("IAM_GET_ALL_SUPPORTERS",
                getAll.getId(), identityManagementRoutingKey, CommandType.IAM_GET_ALL_SUPPORTER, true,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        getAllCommand.setCommandRequestDto(requestDto);
        getAll.getCommandList().add(getAllCommand);
        sequenceRepository.save(getAll);
        sequenceExecuter.startSyncSequence(getAll);
        return objMapper.readValue(getAll.getCommandList().get(0).getResponse(),
                DeserializableSpringDataPage.class);
    }

    @Override
    public void updateUser(UserUpdateRequestDto requestDto , String authorization) throws JsonProcessingException {
        JwtDataDto token = jwtUtil.getJwtDataDtoFromToken(authorization);
        requestDto.getBaseUser().setLastModifier(token.getUsername());

        SagaSequence update = SequenceUtil.generateSequence("IAM_UPDATE");
        SagaWrapperCommand updateCommand = SequenceUtil.generateWrapperCommand("IAM_UPDATE",
                update.getId(), identityManagementRoutingKey, CommandType.IAM_UPDATE_SUPPORTER, false,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        updateCommand.setCommandRequestDto(requestDto);
        update.getCommandList().add(updateCommand);
        sequenceRepository.save(update);
        sequenceExecuter.startSyncSequence(update);
    }

    @Override
    public void changePassword(ChangePasswordRequestDto entity)
            throws JsonProcessingException {
        SagaSequence iamChangePassword = SequenceUtil.generateSequence("IAM_CHANGE_PASSWORD");
        SagaWrapperCommand changePasswordCommand = SequenceUtil.generateWrapperCommand("IAM_CHANGE_PASSWORD",
                iamChangePassword.getId(), identityManagementRoutingKey,
                CommandType.IAM_CHANGES_PASSWORD, true,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        changePasswordCommand.setCommandRequestDto(entity);
        iamChangePassword.getCommandList().add(changePasswordCommand);
        sequenceRepository.save(iamChangePassword);
        sequenceExecuter.startSyncSequence(iamChangePassword);
    }

    @Override
    public void resetPassword(ResetPasswordRequestDto entity)
            throws JsonProcessingException {
        SagaSequence iamResetPassword = SequenceUtil.generateSequence("IAM_RESET_PASSWORD");
        SagaWrapperCommand changePasswordCommand = SequenceUtil.generateWrapperCommand("IAM_RESET_PASSWORD",
                iamResetPassword.getId(), identityManagementRoutingKey,
                CommandType.IAM_RESET_PASSWORD, true,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        changePasswordCommand.setCommandRequestDto(entity);
        iamResetPassword.getCommandList().add(changePasswordCommand);
        sequenceRepository.save(iamResetPassword);
        sequenceExecuter.startSyncSequence(iamResetPassword);

    }

    @Override
    public List<RoleDto> getAllRoles(RoleDto roleDto) throws JsonProcessingException {
        SagaSequence getAllRolesSequence = SequenceUtil.generateSequence(SequenceTitle.IAM_GET_ALL_ROLES.name());
        SagaWrapperCommand getAllRolesCommand = SequenceUtil.generateWrapperCommand(getAllRolesSequence.getTitle(),
                getAllRolesSequence.getId(),
                identityManagementRoutingKey, CommandType.IAM_GET_ALL_ROLES, true,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        getAllRolesCommand.setCommandRequestDto(roleDto);
        getAllRolesSequence.addCommand(getAllRolesCommand);
        sequenceRepository.save(getAllRolesSequence);
        sequenceExecuter.startSyncSequence(getAllRolesSequence);
        return objMapper.readValue(getAllRolesSequence.getCommandList().get(0).getResponse(),
                new TypeReference<List<RoleDto>>() {
                });
    }

    @Override
    public void saveRole(RoleDto roleDto) throws JsonProcessingException {
        SagaSequence saveRoleSequence = SequenceUtil.generateSequence(SequenceTitle.IAM_SAVE_ROLE.name());
        SagaWrapperCommand saveRoleCommand = SequenceUtil.generateWrapperCommand(saveRoleSequence.getTitle(),
                saveRoleSequence.getId(),
                identityManagementRoutingKey, CommandType.IAM_SAVE_ROLE, true,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        saveRoleCommand.setCommandRequestDto(roleDto);
        saveRoleSequence.addCommand(saveRoleCommand);
        sequenceRepository.save(saveRoleSequence);
        sequenceExecuter.startSyncSequence(saveRoleSequence);
    }

    @Override
    public void deleteRole(String roleTitle) throws JsonProcessingException {
        SagaSequence deleteRoleSequence = SequenceUtil.generateSequence(SequenceTitle.IAM_REMOVE_ROLE.name());
        SagaWrapperCommand deleteRoleCommand = SequenceUtil.generateWrapperCommand(deleteRoleSequence.getTitle(),
                deleteRoleSequence.getId(), identityManagementRoutingKey,
                CommandType.IAM_REMOVE_ROLE, true, WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        deleteRoleCommand.setCommandRequestDto(roleTitle);
        deleteRoleSequence.addCommand(deleteRoleCommand);
        sequenceRepository.save(deleteRoleSequence);
        sequenceExecuter.startSyncSequence(deleteRoleSequence);
    }

    @Override
    public LoginResponseDto login(UserLoginDto loginDto) throws JsonProcessingException {
        SagaSequence iamLogin = SequenceUtil.generateSequence("IAM_LOGIN");
        SagaWrapperCommand addMessageCommand = SequenceUtil.generateWrapperCommand("IAM_LOGIN",
                iamLogin.getId(), identityManagementRoutingKey, CommandType.IAM_LOGIN, false,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        addMessageCommand.setCommandRequestDto(loginDto);
        iamLogin.getCommandList().add(addMessageCommand);
        sequenceRepository.save(iamLogin);
        sequenceExecuter.startSyncSequence(iamLogin);
        return parseJson(iamLogin.getCommandList().get(0).getResponse(), LoginResponseDto.class);
    }

    @Override
    public LoginResponseDto acceptTermsAndConditions(UserLoginDto loginDto) throws JsonProcessingException {
        SagaSequence iamLogin = SequenceUtil.generateSequence("IAM_ACCEPT_TERMS_AND_CONDITIONS");
        SagaWrapperCommand addMessageCommand = SequenceUtil.generateWrapperCommand("IAM_ACCEPT_TERMS_AND_CONDITIONS",
                iamLogin.getId(), identityManagementRoutingKey, CommandType.IAM_ACCEPT_TERMS_AND_CONDITIONS, false,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        addMessageCommand.setCommandRequestDto(loginDto);
        iamLogin.getCommandList().add(addMessageCommand);
        sequenceRepository.save(iamLogin);
        sequenceExecuter.startSyncSequence(iamLogin);
        return parseJson(iamLogin.getCommandList().get(0).getResponse(), LoginResponseDto.class);
    }
}
