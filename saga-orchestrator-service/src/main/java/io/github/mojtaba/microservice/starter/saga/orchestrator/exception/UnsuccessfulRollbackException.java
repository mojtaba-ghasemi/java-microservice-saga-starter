package io.github.mojtaba.microservice.starter.saga.orchestrator.exception;


import io.github.mojtaba.microservice.starter.shared.model.MicroServices;
import io.github.mojtaba.microservice.starter.shared.model.exception.GeneralMicroserviceException;

public class UnsuccessfulRollbackException extends GeneralMicroserviceException {

    public UnsuccessfulRollbackException() {
        super(MicroServices.BACKOFFICE_ORCHESTRATOR.name(),
                BusinessExceptionCode.UNSUCCESSFUL_ROLLBACK_EXCEPTION.getValue(),
                BusinessExceptionCode.UNSUCCESSFUL_ROLLBACK_EXCEPTION.name());
    }
}
