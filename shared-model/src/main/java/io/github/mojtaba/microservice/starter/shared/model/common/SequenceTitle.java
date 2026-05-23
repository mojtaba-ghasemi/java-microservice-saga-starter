package io.github.mojtaba.microservice.starter.shared.model.common;

public enum SequenceTitle {
    FETCH_CUSTOMER_BY_USERNAME,
    GET_TOTAL_CUSTOMER_COUNT,
    GET_TOTAL_CUSTOMER_LAST_DAY_COUNT,
    GET_PROFILE_LIST,
    ENABLE_CUSTOMER,
    DISABLE_CUSTOMER,
    GET_CUSTOMER_SESSIONS,


    //IAM TITLES
    IAM_SIGN_UP,
    IAM_GET_ALL_SUPPORTERS,
    IAM_UPDATE,
    IAM_CHANGE_PASSWORD,
    IAM_RESET_PASSWORD,
    IAM_LOGIN,
    IAM_UPDATE_CLAIMS,
    IAM_GET_ALL_ROLES,
    IAM_SAVE_ROLE,
    IAM_REMOVE_ROLE,

    ROLLBACK_TRANSACTION,
    CHANGE_PASSWORD,

    CUSTOMER_DETAIL_INFO,
    LOGIN_OAUTH,
    LOGIN_REFRESH_TOKEN,
    REVOKE_ACCESS,

    ;


    public String getFullTitleWithAppender(String titleAppender) {
        return name() + "_" + titleAppender;
    }

}
