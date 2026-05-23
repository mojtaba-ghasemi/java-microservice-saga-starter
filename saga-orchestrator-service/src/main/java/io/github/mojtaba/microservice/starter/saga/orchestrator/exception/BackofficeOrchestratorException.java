package io.github.mojtaba.microservice.starter.saga.orchestrator.exception;

import io.github.mojtaba.microservice.starter.saga.orchestrator.common.ErrorCodeReaderUtil;
import io.github.mojtaba.microservice.starter.shared.model.MicroServices;
import io.github.mojtaba.microservice.starter.shared.model.exception.GeneralMicroserviceException;

public class BackofficeOrchestratorException extends GeneralMicroserviceException {
    public BackofficeOrchestratorException(BusinessExceptionCode businessExceptionCode) {
        super(MicroServices.BACKOFFICE_ORCHESTRATOR.name(), businessExceptionCode.getValue(),
                ErrorCodeReaderUtil.getResourceProperity(businessExceptionCode.name()));
    }

    public BackofficeOrchestratorException(BusinessExceptionCode businessExceptionCode, String exceptionDetails) {
        super(MicroServices.BACKOFFICE_ORCHESTRATOR.name(), businessExceptionCode.getValue(),
                ErrorCodeReaderUtil.getResourceProperity(businessExceptionCode.name()), exceptionDetails);
    }

    public BackofficeOrchestratorException(Integer exceptionCode, String localeMessage) {
        super(MicroServices.BACKOFFICE_ORCHESTRATOR.name(), exceptionCode, localeMessage);
    }

    public BackofficeOrchestratorException(Integer exceptionCode, String localeMessage, String exceptionDetails) {
        super(MicroServices.BACKOFFICE_ORCHESTRATOR.name(), exceptionCode, localeMessage, exceptionDetails);
    }

    public BackofficeOrchestratorException(String message, Integer exceptionCode, String localeMessage) {
        super(message, MicroServices.BACKOFFICE_ORCHESTRATOR.name(), exceptionCode, localeMessage);
    }

    public BackofficeOrchestratorException(String message, Integer exceptionCode, String localeMessage, String exceptionDetails) {
        super(message, MicroServices.BACKOFFICE_ORCHESTRATOR.name(), exceptionCode, localeMessage, exceptionDetails);
    }

    public BackofficeOrchestratorException(String message, Throwable cause, Integer exceptionCode, String localeMessage) {
        super(message, cause, MicroServices.BACKOFFICE_ORCHESTRATOR.name(), exceptionCode, localeMessage);
    }

    public BackofficeOrchestratorException(String message, Throwable cause, Integer exceptionCode, String localeMessage, String exceptionDetails) {
        super(message, cause, MicroServices.BACKOFFICE_ORCHESTRATOR.name(), exceptionCode, localeMessage, exceptionDetails);
    }

    public BackofficeOrchestratorException(Throwable cause, Integer exceptionCode, String localeMessage, String exceptionDetails) {
        super(cause, MicroServices.BACKOFFICE_ORCHESTRATOR.name(), exceptionCode, localeMessage, exceptionDetails);
    }

    public BackofficeOrchestratorException() {
        this.setMicroServiceName(MicroServices.BACKOFFICE_ORCHESTRATOR.name());
    }
}
