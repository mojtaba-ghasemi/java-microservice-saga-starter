package io.github.mojtaba.microservice.starter.iam.service.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.IAMException;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.SagaCommandNotSupportedException;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.util.ErrorCodeReaderUtil;
import io.github.mojtaba.microservice.starter.iam.service.services.RoleService;
import io.github.mojtaba.microservice.starter.iam.service.services.UserService;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.*;
import io.github.mojtaba.microservice.starter.shared.model.common.CommandStatus;
import io.github.mojtaba.microservice.starter.shared.model.common.CommandType;
import io.github.mojtaba.microservice.starter.shared.model.common.SagaWrapperCommand;
import io.github.mojtaba.microservice.starter.shared.model.exception.BusinessExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommandDispatcher {

    private final UserService userService;
    private final RoleService roleService;

    @RabbitListener(queues = "${orchestration.identityManagement.queue}", concurrency = "16")
    public SagaWrapperCommand handleCommand(SagaWrapperCommand wrapperCommand) {
        try {
            if (wrapperCommand.getStatus().equals(CommandStatus.IN_PROGRESS)) {
                handleInProgressCommand(wrapperCommand);
            } else if (wrapperCommand.getStatus().equals(CommandStatus.ROLL_BACK_IN_PROGRESS)) {
                handleRollBackInProgressCommand(wrapperCommand);
            }
            return wrapperCommand;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            setExceptionForCommand(wrapperCommand, e);
            wrapperCommand.setStatus(CommandStatus.ROLL_BACKED);
            wrapperCommand.setRollbackDescription(e.getMessage());
            return wrapperCommand;
        }
    }

    private void setExceptionForCommand(SagaWrapperCommand wrapperCommand, Exception e) {
        if (e instanceof IAMException) {
            wrapperCommand.setRollbackException((IAMException) e);
        } else {
            wrapperCommand.setRollbackException(
                    new IAMException(
                            e.getMessage(),
                            ErrorCodeReaderUtil.getResourceProperity(BusinessExceptionCode.GENERAL_ERROR.name())
                    )
            );
        }
    }

    private void handleRollBackInProgressCommand(SagaWrapperCommand wrapperCommand) {
        try {
            wrapperCommand.setStatus(CommandStatus.ROLL_BACKED);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            setExceptionForCommand(wrapperCommand, e);
            wrapperCommand.setStatus(CommandStatus.UNSUCCESSFUL_ROLLBACK);
            wrapperCommand.setRollbackDescription(e.getMessage());
        }
    }

    private void handleInProgressCommand(SagaWrapperCommand wrapperCommand) {
        try {
            handleForwardCommand(wrapperCommand);
            wrapperCommand.setStatus(CommandStatus.COMPLETED);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            setExceptionForCommand(wrapperCommand, e);
            wrapperCommand.setStatus(CommandStatus.ROLL_BACKED);
            wrapperCommand.setRollbackDescription(e.getMessage());
        }
    }

    private void handleForwardCommand(SagaWrapperCommand wrapperCommand) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        CommandType commandType = CommandType.valueOf(wrapperCommand.getCommandType());
        switch (commandType) {

            case IAM_SIGNUP:
                wrapperCommand.setResponse(
                        objectMapper.writeValueAsString(
                                userService.signup(
                                        objectMapper.readValue(
                                                wrapperCommand.getCommandContent(), SignUpRequestDto.class
                                        )
                                )
                        )
                );
                break;
            case IAM_SIGNUP_CUSTOMER:
                wrapperCommand.setResponse(
                        objectMapper.writeValueAsString(
                                userService.signupCustomer(
                                        objectMapper.readValue(
                                                wrapperCommand.getCommandContent(), SignUpRequestDto.class
                                        )
                                )
                        )
                );
                break;
            case IAM_LOGIN:
                wrapperCommand.setResponse(
                        objectMapper.writeValueAsString(
                                userService.login(
                                        objectMapper.readValue(
                                                wrapperCommand.getCommandContent(), UserLoginDto.class)
                                )
                        )
                );
                break;

            case IAM_ACCEPT_TERMS_AND_CONDITIONS:
                wrapperCommand.setResponse(
                        objectMapper.writeValueAsString(
                                userService.acceptTermsAndConditions(
                                        objectMapper.readValue(
                                                wrapperCommand.getCommandContent(), UserLoginDto.class)
                                )
                        )
                );
                break;

            case IAM_GET_ALL_SUPPORTER:
                wrapperCommand.setResponse(
                        objectMapper.writeValueAsString(
                                userService.getAll(objectMapper.readValue(
                                        wrapperCommand.getCommandContent(), UserSearchRequestDto.class)
                                )
                        )
                );
                break;

            case IAM_CHANGES_PASSWORD:
                userService.changePassword(objectMapper.readValue(
                        wrapperCommand.getCommandContent(), ChangePasswordRequestDto.class)
                );
                break;

            case IAM_RESET_PASSWORD:

                userService.resetPassword(objectMapper.readValue(
                        wrapperCommand.getCommandContent(), ResetPasswordRequestDto.class)
                );

                break;
            case IAM_UPDATE_SUPPORTER:
                userService.updateUser(objectMapper.readValue(
                        wrapperCommand.getCommandContent(), UserUpdateRequestDto.class)
                );
                break;
            case IAM_GET_ALL_ROLES:
                wrapperCommand.setResponse(
                        objectMapper.writeValueAsString(
                                roleService.getAll(objectMapper.readValue(
                                        wrapperCommand.getCommandContent(), RoleDto.class)
                                )
                        )
                );
                break;
            case IAM_SAVE_ROLE:

                roleService.saveRole(objectMapper.readValue(
                        wrapperCommand.getCommandContent(), RoleDto.class)
                );
                break;

            case IAM_REMOVE_ROLE:
                roleService.deleteByTitle(objectMapper.readValue(
                        wrapperCommand.getCommandContent(), String.class
                ));

                break;
            case FETCH_BASE_USER:
                wrapperCommand.setResponse(objectMapper.writeValueAsString(userService.fetchBaseUserInfoFromUser(
                        objectMapper.readValue(wrapperCommand.getCommandContent(), FetchBaseUserRequestDto.class)
                )));
                break;
            default:
                throw new SagaCommandNotSupportedException(BusinessExceptionCode.COMMAND_NOT_SUPPORTED.name());
        }
    }

}
