package io.github.mojtaba.microservice.starter.saga.orchestrator.rabbit;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ProcessQueueConfig {
    private final QueueConfigurationProperties properties;

    @Value("${application.version}")
    private String applicationVersion;

    @Bean
    public Queue orchestrationProcessQueue(){
        return  QueueBuilder
                .durable(properties.getProcess().getQueueName().concat(applicationVersion))
                .quorum()
                .overflow(QueueBuilder.Overflow.dropHead)
                .build();
    }

    @Bean
    public TopicExchange orchestrationProcessExchange(){
        return new TopicExchange(properties.getProcess().getExchange().concat(applicationVersion));
    }

    @Bean
    public Binding orchestrationProcessBinding(){
        return BindingBuilder
                .bind(orchestrationProcessQueue())
                .to(orchestrationProcessExchange())
                .with(properties.getProcess().getRoutingKey().concat(applicationVersion));
    }
}
