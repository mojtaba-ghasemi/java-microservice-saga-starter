package io.github.mojtaba.microservice.starter.saga.orchestrator.executer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.mojtaba.microservice.starter.saga.orchestrator.exception.BackofficeOrchestratorException;
import io.github.mojtaba.microservice.starter.saga.orchestrator.exception.BusinessExceptionCode;
import io.github.mojtaba.microservice.starter.saga.orchestrator.model.SagaSequence;
import io.github.mojtaba.microservice.starter.shared.model.common.CommandType;
import io.github.mojtaba.microservice.starter.shared.model.common.SagaWrapperCommand;
import io.github.mojtaba.microservice.starter.shared.model.enums.WrapperCommandOnRollbackBehavior;
import io.github.mojtaba.microservice.starter.shared.model.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SagaSequenceTemplate {

    private final SequenceExecuter sequenceExecuter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public <E> E exchange(String routingKey, CommandType commandType,
                          Object requestBody, Class<E> responseType) throws JsonProcessingException {
        SagaSequence sequence = SequenceUtil.generateSequence(commandType);
        SagaWrapperCommand command = SequenceUtil.generateWrapperCommand(commandType.name(),
                sequence.getId(),
                routingKey,
                commandType,
                false,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        try {
            command.setCommandContent(JsonUtil.jsonize(requestBody));
            sequence.addCommand(command);
        } catch (JsonProcessingException e) {
            log.error("unable to parse request object", e);
            throw new BackofficeOrchestratorException(BusinessExceptionCode.GENERAL_ERROR);
        }
        sequenceExecuter.startSyncSequence(sequence);
        return JsonUtil.parseJson(sequence.getCommandList().get(0).getResponse(), responseType);
    }

    public <E> E exchange(String routingKey, CommandType commandType,
                          Object requestBody, TypeReference<E> typeReference) throws JsonProcessingException {
        SagaSequence sequence = SequenceUtil.generateSequence(commandType);
        SagaWrapperCommand command = SequenceUtil.generateWrapperCommand(commandType.name(),
                sequence.getId(),
                routingKey,
                commandType,
                false,
                WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
        try {
            command.setCommandContent(JsonUtil.jsonize(requestBody));
            sequence.addCommand(command);
        } catch (JsonProcessingException e) {
            log.error("unable to parse request object", e);
            throw new BackofficeOrchestratorException(BusinessExceptionCode.GENERAL_ERROR);
        }
        sequenceExecuter.startSyncSequence(sequence);
        return objectMapper.readValue(sequence.getCommandList().get(0).getResponse(),typeReference);
    }
}
