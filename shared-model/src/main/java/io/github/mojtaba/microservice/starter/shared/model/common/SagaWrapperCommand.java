package io.github.mojtaba.microservice.starter.shared.model.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.mojtaba.microservice.starter.shared.model.enums.WrapperCommandOnRollbackBehavior;
import io.github.mojtaba.microservice.starter.shared.model.exception.GeneralMicroserviceException;
import io.github.mojtaba.microservice.starter.shared.model.util.JsonUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import java.util.Date;
import java.util.List;

@Getter
@Setter
public class SagaWrapperCommand {

    private String id;
    private String sequenceId;
    private String title;
    private CommandStatus status;
    private String commandType;
    private String commandContent;
    private Date StartDate;
    private Date endDate;
    private String routingKey;
    private Integer schemaVersion = 1;
    private String commandUser;
    private String commandDate;
    private String rollbackDescription;
    @Transient
    private GeneralMicroserviceException rollbackException;
    private String response;
    private boolean rollbackRequired;
    private WrapperCommandOnRollbackBehavior onRollbackBehavior;
    @JsonIgnore
    @Transient
    private IntermediatePipe intermediatePipe;

    @JsonIgnore
    @Transient
    private List<PreExecuteCondition> preExecuteConditionList;

    @JsonIgnore
    @Transient
    private List<PostExecutionTask> postExecutionTasks;
    private String userIP;

    private String requestClassName;
    private String responseClassName;
    private String customerId;

    public SagaWrapperCommand() {
    }

    public SagaWrapperCommand(String id, String sequenceId, String title, CommandStatus status, String commandType,
                              String routingKey, String commandUser,
                              boolean rollbackRequired, WrapperCommandOnRollbackBehavior onRollbackBehavior, String customerId) {
        this.id = id;
        this.sequenceId = sequenceId;
        this.title = title;
        this.status = status;
        this.commandType = commandType;
        this.routingKey = routingKey;
        this.commandUser = commandUser;
        this.rollbackRequired = rollbackRequired;
        this.onRollbackBehavior = onRollbackBehavior;
        this.customerId = customerId;
    }

    public SagaWrapperCommand(String id, String sequenceId, String title, CommandStatus status, String commandType,
                              String routingKey, String commandUser,
                              boolean rollbackRequired, String customerId) {
        this.id = id;
        this.sequenceId = sequenceId;
        this.title = title;
        this.status = status;
        this.commandType = commandType;
        this.routingKey = routingKey;
        this.commandUser = commandUser;
        this.rollbackRequired = rollbackRequired;
        this.onRollbackBehavior = WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE;
        this.customerId = customerId;
    }

    public SagaWrapperCommand(String id, String sequenceId, String title, CommandStatus status, String commandType,
                              String routingKey, String commandUser,
                              boolean rollbackRequired, String userIP, String customerId) {
        this.id = id;
        this.sequenceId = sequenceId;
        this.title = title;
        this.status = status;
        this.commandType = commandType;
        this.routingKey = routingKey;
        this.commandUser = commandUser;
        this.rollbackRequired = rollbackRequired;
        this.onRollbackBehavior = WrapperCommandOnRollbackBehavior.ROLLBACK_SEQUENCE;
        this.setUserIP(userIP);
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return "SagaWrapperCommand{" +
                "id=" + id +
                ", sequenceId=" + sequenceId +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", StartDate=" + StartDate +
                ", endDate=" + endDate +
                '}';
    }

    public <T> void setCommandRequestDto(T request) throws JsonProcessingException {
        if(request == null) {
            return;
        }
        this.setCommandContent(JsonUtil.jsonize(request));
        this.setRequestClassName(request.getClass().getName());
    }

    public <T> void setCommandResponseDto(T response) throws JsonProcessingException {
        if(response == null) {
            return;
        }
        this.setResponse(JsonUtil.jsonize(response));
        this.setResponseClassName(response.getClass().getName());
    }
}
