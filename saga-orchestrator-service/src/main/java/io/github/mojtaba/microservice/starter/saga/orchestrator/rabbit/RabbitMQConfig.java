package io.github.mojtaba.microservice.starter.saga.orchestrator.rabbit;


import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Jackson2JsonMessageConverter getMessageConverter() {
//        return new Jackson2JsonMessageConverter();
        Jackson2JsonMessageConverter messageConverter = new ImplicitJsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages("*");
        classMapper.setDefaultType(Map.class);
        messageConverter.setClassMapper(classMapper);
        return messageConverter;

    }

    public static class ImplicitJsonMessageConverter extends Jackson2JsonMessageConverter {
        public ImplicitJsonMessageConverter() {
            super( "*");
        }
        @Override
        public Object fromMessage(Message message) throws MessageConversionException {
            message.getMessageProperties().setContentType("application/json");
            return super.fromMessage(message);
        }
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory factory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(factory);
        rabbitTemplate.setMessageConverter(getMessageConverter());
        rabbitTemplate.setReplyTimeout(600000);
        return rabbitTemplate;
    }

    @Bean
    public MappingJackson2MessageConverter consumerJackson2MessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(consumerJackson2MessageConverter());
        return factory;
    }
}