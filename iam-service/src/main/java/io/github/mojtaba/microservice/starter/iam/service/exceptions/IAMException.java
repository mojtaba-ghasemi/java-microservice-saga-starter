package io.github.mojtaba.microservice.starter.iam.service.exceptions;


import io.github.mojtaba.microservice.starter.iam.service.exceptions.enums.BusinessException;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.util.ErrorCodeReaderUtil;
import io.github.mojtaba.microservice.starter.shared.model.MicroServices;
import io.github.mojtaba.microservice.starter.shared.model.exception.GeneralMicroserviceException;

public class IAMException extends GeneralMicroserviceException {

    private static final String MICROSERVICE_NAME = MicroServices.IAM.name();

    public IAMException(BusinessException businessException) {
        super(
                MICROSERVICE_NAME,
                businessException.getErrorCode(),
                ErrorCodeReaderUtil.getResourceProperity(businessException.name())
        );
    }

    public IAMException(String message, String localeMessage) {
        super(message, MICROSERVICE_NAME, -2, localeMessage);
    }
}
