package io.github.mojtaba.microservice.starter.shared.model.exception;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GeneralMicroserviceException extends RuntimeException{
    private String microServiceName;
    private Integer exceptionCode;
    private String localeMessage;
    private String exceptionDetails;

    public GeneralMicroserviceException(String microServiceName, Integer exceptionCode, String localeMessage) {
        this.microServiceName = microServiceName;
        this.exceptionCode = exceptionCode;
        this.localeMessage = localeMessage;
    }

    public GeneralMicroserviceException(String microServiceName, Integer exceptionCode, String localeMessage, String exceptionDetails) {
        this.microServiceName = microServiceName;
        this.exceptionCode = exceptionCode;
        this.localeMessage = localeMessage;
        this.exceptionDetails = exceptionDetails;
    }

    public GeneralMicroserviceException(String message, String microServiceName, Integer exceptionCode, String localeMessage) {
        super(message);
        this.microServiceName = microServiceName;
        this.exceptionCode = exceptionCode;
        this.localeMessage = localeMessage;
    }

    public GeneralMicroserviceException(String message, String microServiceName, Integer exceptionCode, String localeMessage, String exceptionDetails) {
        super(message);
        this.microServiceName = microServiceName;
        this.exceptionCode = exceptionCode;
        this.localeMessage = localeMessage;
        this.exceptionDetails = exceptionDetails;
    }

    public GeneralMicroserviceException(String message, Throwable cause, String microServiceName, Integer exceptionCode, String localeMessage) {
        super(message, cause);
        this.microServiceName = microServiceName;
        this.exceptionCode = exceptionCode;
        this.localeMessage = localeMessage;
    }

    public GeneralMicroserviceException(String message, Throwable cause, String microServiceName, Integer exceptionCode, String localeMessage, String exceptionDetails) {
        super(message, cause);
        this.microServiceName = microServiceName;
        this.exceptionCode = exceptionCode;
        this.localeMessage = localeMessage;
        this.exceptionDetails = exceptionDetails;
    }

    public GeneralMicroserviceException(Throwable cause, String microServiceName, Integer exceptionCode, String localeMessage, String exceptionDetails) {
        super(cause);
        this.microServiceName = microServiceName;
        this.exceptionCode = exceptionCode;
        this.localeMessage = localeMessage;
        this.exceptionDetails = exceptionDetails;
    }
}

