package io.github.mojtaba.microservice.starter.saga.orchestrator.rabbit;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "rabbitmq.backoffice.orchestration")
@Component
@Data
public class QueueConfigurationProperties {

    Process process = new Process();
    Sequence sequence = new Sequence();

    @Setter
    @Getter
    public static class QueueProperties {
        private String queueName;
        private String exchange;
        private String routingKey;
    }

    @Setter
    @Getter
    public static class Process extends QueueProperties{}

    @Setter
    @Getter
    public static class Sequence extends QueueProperties{}
}
