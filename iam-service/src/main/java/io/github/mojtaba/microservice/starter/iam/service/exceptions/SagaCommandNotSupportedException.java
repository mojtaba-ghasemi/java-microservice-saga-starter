package io.github.mojtaba.microservice.starter.iam.service.exceptions;

public class SagaCommandNotSupportedException extends RuntimeException{
    public SagaCommandNotSupportedException(String message) {
        super(message);
    }
}
