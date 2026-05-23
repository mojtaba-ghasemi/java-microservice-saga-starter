package io.github.mojtaba.microservice.starter.saga.orchestrator.jwt;

import io.jsonwebtoken.Claims;

public interface CustomClaims extends Claims {

    String PERMISSIONS = "PERMISSIONS";
    String ROLES = "ROLES";
    String MOBILE = "MOBILE";
    String CIF = "CIF";
    String USERNAME="USERNAME";
    String USERIP = "USERIP";
    String PLATFORM = "PLATFORM";
    String VERSION = "VERSION";
    String DEVICE = "DEVICE";
    String CUSTOMERSESSIONID = "CUSTOMERSESSIONID";
}

