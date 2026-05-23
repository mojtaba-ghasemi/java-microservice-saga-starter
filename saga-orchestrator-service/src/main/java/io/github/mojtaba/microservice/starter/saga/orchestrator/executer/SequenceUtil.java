package io.github.mojtaba.microservice.starter.saga.orchestrator.executer;


import io.github.mojtaba.microservice.starter.saga.orchestrator.common.UUIDGenerator;
import io.github.mojtaba.microservice.starter.saga.orchestrator.enums.SagaElementType;
import io.github.mojtaba.microservice.starter.saga.orchestrator.jwt.JwtAuthenticationToken;
import io.github.mojtaba.microservice.starter.shared.model.common.CommandStatus;
import io.github.mojtaba.microservice.starter.shared.model.common.CommandType;
import io.github.mojtaba.microservice.starter.saga.orchestrator.model.SagaSequence;
import io.github.mojtaba.microservice.starter.shared.model.common.SagaWrapperCommand;
import io.github.mojtaba.microservice.starter.shared.model.enums.WrapperCommandOnRollbackBehavior;
import io.github.mojtaba.microservice.starter.shared.model.saga.SequenceStatus;
import org.springframework.security.core.context.SecurityContextHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class SequenceUtil {

    public static SagaSequence generateSequence(CommandType commandType) {
        return generateSequence(makeUniqueNameByCurrentDate(SagaElementType.Sequence, commandType));
    }

    public static SagaSequence generateSequence(CommandType commandType, String requestIdentifier) {
        return generateSequence(makeUniqueNameByCurrentDate(SagaElementType.Sequence, commandType, requestIdentifier));
    }

    public static SagaSequence generateSequence(String title) {
        String id = UUIDGenerator.generateRandomUUID();
        SagaSequence sequence = new SagaSequence(id, title);
        sequence.setStatus(SequenceStatus.CREATED);
        sequence.setTitle(title);
        sequence.setCommandList(new ArrayList<>());
        sequence.setSequenceContext(new HashMap<>());
        sequence.setUserIp(fetchUserIpFromAuthentication());
        sequence.setRegisterId(fetchRegisterIdFromAuthentication());
        return sequence;
    }

    public static SagaSequence generateSequence(String title, String userIP) {
        SagaSequence sequence = generateSequence(title);
        sequence.setUserIp(userIP);
        return sequence;
    }

    public static SagaWrapperCommand generateWrapperCommand(String title, String sequenceId, String routingKey, CommandType commandType, Boolean rollbackRequired, WrapperCommandOnRollbackBehavior onRollbackBehavior) {
        return generateWrapperCommand(title, sequenceId, routingKey, commandType, rollbackRequired, onRollbackBehavior, null);
    }

    public static SagaWrapperCommand generateWrapperCommand(String title, String sequenceId, String routingKey,
                                                            CommandType commandType, Boolean rollbackRequired,
                                                            WrapperCommandOnRollbackBehavior onRollbackBehavior,
                                                            String userIP) {
        String id = UUIDGenerator.generateRandomUUID();
        String user = fetchUsernameFromAuthentication();
        String registerId = fetchRegisterIdFromAuthentication();

        SagaWrapperCommand sagaWrapperCommand = new SagaWrapperCommand(id, sequenceId, title, CommandStatus.CREATED,
                commandType.name(), routingKey, user, rollbackRequired, onRollbackBehavior, registerId);
        sagaWrapperCommand.setPreExecuteConditionList(new ArrayList<>());
        sagaWrapperCommand.setPostExecutionTasks(new ArrayList<>());
        sagaWrapperCommand.setUserIP(userIP);
        return sagaWrapperCommand;
    }

    public static SagaWrapperCommand generateWrapperCommand(String title, String sequenceId, String routingKey,
                                                            CommandType commandType, Boolean rollbackRequired) {
        return generateWrapperCommand(title, sequenceId, routingKey, commandType, rollbackRequired, WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE);
    }

    public static String fetchUserIpFromAuthentication() {
        try {
            JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return token.getDetails().getUserIP();
        } catch (Exception e) {
            return "AnonymousIP";
        }
    }

    public static String fetchRegisterIdFromAuthentication() {
        try {
            JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return token.getDetails().getCustomerId();
        } catch (Exception e) {
            return null;
        }
    }

    public static String fetchUsernameFromAuthentication() {
        try {
            JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            return token.getPrincipal().toString();
        } catch (Exception e) {
            return "Anonymous";
        }
    }

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");

    public static String makeUniqueNameByCurrentDate(SagaElementType elementType, CommandType commandType) {
        return makeUniqueNameByCurrentDate(elementType, commandType, "");
    }

    public static String makeUniqueNameByCurrentDate(
            SagaElementType elementType,
            CommandType commandType,
            String requestIdentifier) {
        return String.format("%s__%s_%s_%s",
                elementType.getShortForm(),
                commandType.name(),
                requestIdentifier,
                dateFormat.format(new Date()));
    }

}
