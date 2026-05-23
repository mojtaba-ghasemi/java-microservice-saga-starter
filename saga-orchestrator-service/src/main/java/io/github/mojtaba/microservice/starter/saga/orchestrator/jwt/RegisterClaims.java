package io.github.mojtaba.microservice.starter.saga.orchestrator.jwt;

import io.jsonwebtoken.Claims;

public interface RegisterClaims extends Claims {
    String MOBILE = "MOBILE";
    String DEVICE = "DEVICE";
    String PLATFORM = "PLATFORM";
    String VERSION="VERSION";
    String NATIONAL_CODE = "NATIONAL_CODE";

}
