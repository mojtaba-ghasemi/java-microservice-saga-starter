package io.github.mojtaba.microservice.starter.saga.orchestrator.exception;

import io.github.mojtaba.microservice.starter.shared.model.exception.GeneralMicroserviceException;

public class RollbackSequenceException extends GeneralMicroserviceException {
    public RollbackSequenceException(String microServiceName, Integer exceptionCode, String localeMessage) {
        super(microServiceName, exceptionCode, localeMessage);
    }

    public RollbackSequenceException(GeneralMicroserviceException exception) {
        super(exception.getMicroServiceName(), exception.getExceptionCode(),
                exception.getLocaleMessage());
    }
}
