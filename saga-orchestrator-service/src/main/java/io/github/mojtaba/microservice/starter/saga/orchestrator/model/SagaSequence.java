package io.github.mojtaba.microservice.starter.saga.orchestrator.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.github.mojtaba.microservice.starter.shared.model.common.SagaWrapperCommand;
import io.github.mojtaba.microservice.starter.shared.model.saga.SequenceStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Document(collection = "backofficeSagaSequence")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class SagaSequence {

    @Id
    private String id;
    private String title;
    private SequenceStatus status;
    private List<SagaWrapperCommand> commandList;
    private Date creationDate;
    private Date startDate;
    private Date  completeDate;
    private Map<String, String> sequenceContext;
    @Transient
    private RuntimeException rollbackException;
    private String rollbackDescription;
    private Boolean kafkaSynchronize;
    private String userIp;
    private String registerId;

    private String sagaVersion = "12";

    public SagaSequence() {
    }

    public SagaSequence(String id, String title) {
        this.id = id;
        this.title = title;
        this.creationDate = new Date();
    }

    /**
     *
     * @param sagaWrapperCommand
     * @return inserted index
     */
    public int addCommand(SagaWrapperCommand sagaWrapperCommand) {
        if(this.getCommandList() == null) {
            this.setCommandList(new ArrayList<>());
        }
        this.getCommandList().add(sagaWrapperCommand);
        return this.getCommandList().size()-1;
    }

    @Override
    public String toString() {
        return "Sequence{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", commandList=" + commandList +
                '}';
    }
}
