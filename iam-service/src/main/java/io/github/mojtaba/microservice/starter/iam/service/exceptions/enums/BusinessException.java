package io.github.mojtaba.microservice.starter.iam.service.exceptions.enums;

public enum BusinessException {

    USER_ALREADY_EXISTS(409001),
    CHANGE_PASSWORD_EXCEPTION(203002),
    USER_NOT_FOUND_EXCEPTION(404003),
    INVALID_CREDENTIALS(401004),
    ROLE_NOT_FOUNT_EXCEPTION(404005),
    CUSTOMER_INVALID_ROLE_EXCEPTION(404006),
    ROLE_EXISTS_FOR_USER(400006),
    BACKOFFICEMEMBER_ROLE_NOT_FOUND(400007),
    USER_IS_DISABLED(401008),
    BASE_USER_INFO_IN_INVALID(400009),
    BASE_USER_NOT_FOUND(404010),
    BASE_USER_ALREADY_EXISTS(400011),

    ;


    BusinessException(int errorCode) {
        this.errorCode = errorCode;
    }

    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }
}
