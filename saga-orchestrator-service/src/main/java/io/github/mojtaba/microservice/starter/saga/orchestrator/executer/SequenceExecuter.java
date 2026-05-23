package io.github.mojtaba.microservice.starter.saga.orchestrator.executer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mojtaba.microservice.starter.saga.orchestrator.dto.SequenceRepository;
import io.github.mojtaba.microservice.starter.saga.orchestrator.exception.BackofficeOrchestratorException;
import io.github.mojtaba.microservice.starter.saga.orchestrator.exception.BusinessExceptionCode;
import io.github.mojtaba.microservice.starter.saga.orchestrator.exception.RollbackSequenceException;
import io.github.mojtaba.microservice.starter.saga.orchestrator.exception.UnsuccessfulRollbackException;
import io.github.mojtaba.microservice.starter.saga.orchestrator.model.SagaSequence;
import io.github.mojtaba.microservice.starter.shared.model.MicroServices;
import io.github.mojtaba.microservice.starter.shared.model.common.*;
import io.github.mojtaba.microservice.starter.shared.model.saga.SequenceStatus;
import io.github.mojtaba.microservice.starter.shared.model.exception.GeneralMicroserviceException;
import io.github.mojtaba.microservice.starter.shared.model.enums.WrapperCommandOnRollbackBehavior;
import io.github.mojtaba.microservice.starter.saga.orchestrator.common.ErrorCodeReaderUtil;
import io.github.mojtaba.microservice.starter.shared.model.util.JsonUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;

@Service
@Slf4j
public class SequenceExecuter {

    Logger logger = LoggerFactory.getLogger(SequenceExecuter.class);

    private final SequenceRepository sequenceRepository;
    private final RabbitTemplate rabbitTemplate;
    private static ResourceBundle secretclassnames = ResourceBundle.getBundle("secretclassnames");

    private static final List<String> secretCommandList =
            new ArrayList<>(Arrays.asList(
                    CommandType.LOGIN_STATIC.name(),
                    CommandType.HARIM_OTP.name(),
                    CommandType.IAM_LOGIN.name(),
                    CommandType.CHANGE_PASSWORD.name(),
                    CommandType.WALLET_FIND_PAN_BY_PHONE_NO.name()));

    @Value("${rabbitmq.exchange}")
    private String exchange;

    public SequenceExecuter(SequenceRepository sequenceRepository, RabbitTemplate rabbitTemplate) {
        this.sequenceRepository = sequenceRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public void startSyncSequence(SagaSequence sequence) throws JsonProcessingException {
        Map<String, String> requestResponseMap = new HashMap<>();
        if (sequence.getStatus() != SequenceStatus.CREATED) {
            throw new RuntimeException("Sequence is not in created Status");
        }
        sequence.setStatus(SequenceStatus.IN_PROGRESS);
        sequence.setStartDate(new Date());
        sequenceRepository.save(sequence);
        if (CollectionUtils.isEmpty(sequence.getCommandList())) {
            logger.warn(MessageFormat.format("Sequence command list is empty{0}", sequence.getTitle()));
            completeSequence(sequence, SequenceStatus.COMPLETED);
        }
        List<SagaWrapperCommand> commandList = sequence.getCommandList();
        boolean rollbacked = false;
        int iterator = 0;
        SagaWrapperCommand responseCommand = null;
        for (iterator = 0; iterator < commandList.size(); iterator++) {

            try {
                responseCommand = startSyncCommand(sequence, iterator, requestResponseMap);
                updateCommandAndSequence(responseCommand, iterator, sequence);
            } catch (GeneralMicroserviceException e) {
                rollbacked = true;
                logger.error(e.getMessage(), e);
                sequence.setRollbackException(e);
                break;
            } catch (RuntimeException e) {
                rollbacked = true;
                logger.error(e.getMessage(), e);
                sequence.setRollbackDescription(e.getMessage());
                break;
            } finally {
                sequenceRepository.save(sequence);
            }

        }
        try {
            if (rollbacked) {
                rollbackSequence(sequence, iterator);
            } else {
                completeSequence(sequence, SequenceStatus.COMPLETED);
            }
        } finally {
            sequenceRepository.save(sequence);
        }
    }

    public void rollbackSequence(SagaSequence sequence, int rollbackCommandIndex) {
        if (rollbackCommandIndex < 0) {
            throw sequence.getRollbackException();
        }
        if (sequence.getCommandList().get(rollbackCommandIndex).getStatus().ordinal() < CommandStatus.IN_PROGRESS.ordinal()) {
            rollbackCommandIndex--;
        }
        for (; rollbackCommandIndex >= 0; rollbackCommandIndex--) {
            if (sequence.getCommandList().get(rollbackCommandIndex).isRollbackRequired()) {
                SagaWrapperCommand rollBackResponseCommand = rollbackSyncCommand(sequence, rollbackCommandIndex);
                updateCommandAndSequenceForRollback(rollBackResponseCommand, rollbackCommandIndex, sequence);
            }
        }
        if (!sequence.getStatus().equals(SequenceStatus.COMPLETED)
                && !sequence.getStatus().equals(SequenceStatus.ROLL_BACKED)) {
            completeSequence(sequence, SequenceStatus.ROLL_BACKED);
        }
        throw sequence.getRollbackException();
    }

    private SagaSequence updateCommandAndSequence(SagaWrapperCommand responseCommand, int index, SagaSequence sequence) {

        sequence.getCommandList().set(index, responseCommand);
        handleResponseCommand(responseCommand, index, sequence);
        return sequence;
    }

    private SagaWrapperCommand cloneCommand(SagaWrapperCommand requestCommand) {
        SagaWrapperCommand responseCommand;
        try {
            responseCommand = JsonUtil.parseJson(JsonUtil.jsonize(requestCommand), SagaWrapperCommand.class); //clonning responseCommand
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new GeneralMicroserviceException(MicroServices.BACKOFFICE_ORCHESTRATOR.name(), -1, e.getMessage());
        }
        return responseCommand;
    }


    private SagaSequence updateCommandAndSequenceForRollback(SagaWrapperCommand responseCommand, int index, SagaSequence sequence) {

        SagaWrapperCommand requestCommand = sequence.getCommandList().get(index);
        if (responseCommand != null) {
            sequence.getCommandList().set(index, responseCommand);
        } else {
            SagaWrapperCommand clonedCommand = cloneCommand(requestCommand);

            clonedCommand.setStatus(CommandStatus.UNSUCCESSFUL_ROLLBACK);
            responseCommand = clonedCommand;
        }
        handleRollbackResponseCommand(responseCommand, index, sequence);
        return sequenceRepository.save(sequence);
    }

    private void handleResponseCommand(SagaWrapperCommand responseCommand, int index, SagaSequence sequence) {
        switch (responseCommand.getStatus()) {
            case COMPLETED:
            case ROLL_BACK_IN_PROGRESS:
            case SKIPPED:
                //no action needed..just continue...
                break;
            case UNSUCCESSFUL_ROLLBACK:
                throw new UnsuccessfulRollbackException();
            default: //it means rollbacked, noResponse, transfer_failed
                setSequenceRollbackProperties(responseCommand, sequence);

        }
    }

    private void handleRollbackResponseCommand(SagaWrapperCommand responseCommand, int index, SagaSequence sequence) {
        switch (responseCommand.getStatus()) {
            case ROLL_BACKED:
            case COMPLETED:
            case SKIPPED:
                if (index == 0) {
                    completeSequence(sequence, SequenceStatus.ROLL_BACKED);
                }
                break;
            default:
                throw new UnsuccessfulRollbackException();
        }
    }

    private void setSequenceRollbackProperties(SagaWrapperCommand responseCommand, SagaSequence sequence) {
        if (responseCommand.getOnRollbackBehavior().equals(WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE)) {
            GeneralMicroserviceException exception = responseCommand.getRollbackException();
            if (responseCommand.getRollbackDescription() != null) {
                if(StringUtils.isNotBlank(responseCommand.getRollbackException().getLocaleMessage())) {
                    throw new GeneralMicroserviceException(MicroServices.BACKOFFICE_ORCHESTRATOR.name()
                            , responseCommand.getRollbackException().getExceptionCode(), responseCommand.getRollbackException().getLocaleMessage());
                }
                throw new GeneralMicroserviceException(MicroServices.BACKOFFICE_ORCHESTRATOR.name()
                        , responseCommand.getRollbackException().getExceptionCode(), ErrorCodeReaderUtil.getResourceProperity(responseCommand.getRollbackDescription()));
            }
            if (exception != null) {
                setRollbackDescription(responseCommand);
                sequence.setRollbackException(responseCommand.getRollbackException());
                throw new RollbackSequenceException(exception);
            } else {
                sequence.setRollbackException(new GeneralMicroserviceException(MicroServices.BACKOFFICE_ORCHESTRATOR.name(), -1, responseCommand.getRollbackDescription()));
                throw sequence.getRollbackException();
            }
        }
    }

    private void setRollbackDescription(SagaWrapperCommand command) {
        command.setRollbackDescription(
                MessageFormat.format("{0}_{1}_{2}_{3}",
                        command.getRollbackException().getMicroServiceName(),
                        command.getRollbackException().getExceptionCode(),
                        command.getRollbackException().getMessage(),
                        command.getRollbackException().getLocaleMessage()));

    }

    private SagaWrapperCommand startSyncCommand(SagaSequence sequence, Integer commandIndex, Map<String, String> requestResponseMap) {
        SagaWrapperCommand command = sequence.getCommandList().get(commandIndex);
        //////////////////
        boolean executeFlag = checkCommandExecutionCondition(sequence, requestResponseMap, command);
        if (!executeFlag) {
            command.setStatus(CommandStatus.SKIPPED);
            return command;
        }
        //////////////////
        command = executeIntermediatePipe(sequence, requestResponseMap, command);
        if (command.getStatus().equals(CommandStatus.SKIPPED)) {
            return command;
        }
        ///////////////////////
        SagaWrapperCommand responseCommand = executeCommand(sequence, commandIndex, command);
        ///////////////
        if (responseCommand == null) {
            command.setStatus(CommandStatus.NO_RESPONSE);
            responseCommand = cloneCommand(command);
            responseCommand.setRollbackException(new BackofficeOrchestratorException(BusinessExceptionCode.MESSAGE_TIMEOUT_ERROR));
            return responseCommand;
        }
        logger.debug("response received : \n" + responseCommand.getResponse());
        requestResponseMap.put(command.getCommandType(), responseCommand.getResponse());
        if (responseCommand.getRollbackException() == null && responseCommand.getRollbackDescription() == null) {
            executePostExecutionTask(sequence, requestResponseMap, command);
        }
        return responseCommand;
    }

    private void executePostExecutionTask(SagaSequence sequence, Map<String, String> requestResponseMap, SagaWrapperCommand command) {
        if (command.getPostExecutionTasks() != null && !command.getPostExecutionTasks().isEmpty()) {
            for (PostExecutionTask task : command.getPostExecutionTasks()) {
                try {
                    task.execute(requestResponseMap, sequence.getSequenceContext());
                } catch (GeneralMicroserviceException e) {

                    if (!command.getOnRollbackBehavior().equals(WrapperCommandOnRollbackBehavior.SKIP)) {
                        command.setRollbackException(e);
                        throw new RollbackSequenceException(e);
                    }
                } catch (Exception e) {

                    if (!command.getOnRollbackBehavior().equals(WrapperCommandOnRollbackBehavior.SKIP)) {
                        command.setRollbackException(new GeneralMicroserviceException(MicroServices.BACKOFFICE_ORCHESTRATOR.name(), -1, e.getMessage()));
                        throw new RollbackSequenceException(command.getRollbackException());
                    }
                }
            }
        }
    }

    private SagaWrapperCommand executeCommand(SagaSequence sequence, Integer commandIndex, SagaWrapperCommand command) {
        command.setStatus(CommandStatus.IN_PROGRESS);
        command.setStartDate(new Date());
        sequenceRepository.save(sequence);
        logger.debug("starting command : \n" + command);
        try {
            return (SagaWrapperCommand) rabbitTemplate.convertSendAndReceive(exchange, command.getRoutingKey(), command);
        } catch (AmqpException e) {
            logger.error(MessageFormat.format("unable to send command to routing key:{0} , Command Type: {1}",
                    command.getRoutingKey(), command.getCommandType()), e);
            command.setStatus(CommandStatus.COMMAND_TRANSFER_FAILED);
            throw new BackofficeOrchestratorException(BusinessExceptionCode.COMMAND_TRANSFER_FAILED);
        }
    }

    private SagaWrapperCommand executeIntermediatePipe(SagaSequence sequence, Map<String, String> requestResponseMap, SagaWrapperCommand command) {
        if (command.getIntermediatePipe() != null) {
            command.setStatus(CommandStatus.EXECUTE_INTERMEDIATE_PIPE);
            try {
                command.setCommandContent(command.getIntermediatePipe().pipe(command.getCommandContent(),
                        requestResponseMap, sequence.getSequenceContext()));
            } catch (RuntimeException e) {
                logger.error(e.getMessage(), e);
                if (command.getOnRollbackBehavior().equals(WrapperCommandOnRollbackBehavior.SKIP)) {
                    command.setStatus(CommandStatus.SKIPPED);
                    return command;
                } else {
                    throw e;
                }
            }
        }
        return command;
    }

    private boolean checkCommandExecutionCondition(SagaSequence sequence, Map<String, String> requestResponseMap, SagaWrapperCommand command) {
        boolean executeFlag = true;
        if (command.getPreExecuteConditionList() != null && !command.getPreExecuteConditionList().isEmpty()) {
            command.setStatus(CommandStatus.CHECK_PRE_EXECUTION_CONDITION);
            for (PreExecuteCondition c : command.getPreExecuteConditionList()) {
                try {
                    executeFlag = c.executeCondition(requestResponseMap, sequence.getSequenceContext());
                    if (!executeFlag) {
                        break;
                    }
                } catch (GeneralMicroserviceException e) {
                    logger.error(e.getMessage(), e);
                    executeFlag = false;
                    if (!command.getOnRollbackBehavior().equals(WrapperCommandOnRollbackBehavior.SKIP)) {
                        command.setRollbackException(e);
                        throw new RollbackSequenceException(e);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    executeFlag = false;
                    if (!command.getOnRollbackBehavior().equals(WrapperCommandOnRollbackBehavior.SKIP)) {
                        command.setRollbackException(new GeneralMicroserviceException(MicroServices.BACKOFFICE_ORCHESTRATOR.name(), -1, e.getMessage()));
                        throw new RollbackSequenceException(command.getRollbackException());
                    }

                }
            }
        }
        return executeFlag;
    }


    public SagaWrapperCommand rollbackSyncCommand(SagaSequence sequence, Integer commandIndex) {
        if (commandIndex < 0) {
            this.logger.error("rollback command is invalid");
            sequence.setStatus(SequenceStatus.ROLL_BACKED);
            sequenceRepository.save(sequence);
        }
        SagaWrapperCommand command = sequence.getCommandList().get(commandIndex);
        if (command.getStatus().equals(CommandStatus.SKIPPED) || command.getStatus().equals(CommandStatus.ROLL_BACKED)) {
            return command;
        }
        if (command.getStatus().ordinal() < CommandStatus.IN_PROGRESS.ordinal()) {
            logger.error("error has been occurred before sending command to microservice. maybe in pipe or preExecuteConditions");
            return command;
        }
        if (!command.getStatus().equals(CommandStatus.COMPLETED)
                && !command.getStatus().equals(CommandStatus.NO_RESPONSE)) {
            throw new BackofficeOrchestratorException(BusinessExceptionCode.ROLLBACKING_NOT_COMPLETED_COMMAND);
        }
        command.setStatus(CommandStatus.ROLL_BACK_IN_PROGRESS);
        sequence.getCommandList().set(commandIndex, command);
        sequenceRepository.save(sequence);
        return (SagaWrapperCommand) rabbitTemplate.convertSendAndReceive(exchange, command.getRoutingKey(), command);
    }

    private static final Logger SAGA_LOGGER = LoggerFactory.getLogger("sagaLogger");

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void completeSequence(SagaSequence sequence, SequenceStatus sequenceStatus) {
        hashSecretCommandsContent(sequence);
        sequence.setStatus(sequenceStatus);
        if (sequenceStatus.equals(SequenceStatus.COMPLETED)) {
            sequence.setCompleteDate(new Date());
        }
        sequenceRepository.save(sequence);

        //log saga-sequence into ELK
        try {
            SAGA_LOGGER.info(objectMapper.writeValueAsString(sequence));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void hashSecretCommandsContent(SagaSequence sagaSequence) {
        if (CollectionUtils.isEmpty(sagaSequence.getCommandList())) {
            return;
        }
        sagaSequence.getCommandList().forEach(c -> {
            try {
                handleSecretCommand(c);
            } catch (JsonProcessingException e) {
                logger.error("unable to parse command");
                e.printStackTrace();
            }
        });
    }

    private void handleSecretCommand(SagaWrapperCommand command) throws JsonProcessingException {
        securateRequestContent(command);
        securateResonseContent(command);
    }

    private void securateRequestContent(SagaWrapperCommand command) {
        if (StringUtils.isBlank(command.getRequestClassName()))
            return;
        try {
            Class.forName(command.getRequestClassName()).asSubclass(SecretCommandDto.class);
            command.setCommandContent(JsonUtil.jsonize(((SecretCommandDto) JsonUtil.parseJson(command.getCommandContent(), Class.forName(command.getRequestClassName()))).securate()));
        } catch (Exception e) {
            //nothing to do..
        }
    }

    private void securateResonseContent(SagaWrapperCommand command) {
        if (StringUtils.isBlank(command.getResponseClassName()))
            return;
        try {
            Class.forName(command.getResponseClassName()).asSubclass(SecretCommandDto.class);
            command.setResponse(JsonUtil.jsonize(((SecretCommandDto) JsonUtil.parseJson(command.getResponse(), Class.forName(command.getResponseClassName()))).securate()));
        } catch (Exception e) {
            //nothing to do..
        }
    }
}
