package io.github.mojtaba.microservice.starter.saga.orchestrator.jwt;

import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.util.Optional;

public enum TokenIssuer {
    CITY_DI_SSO("citydi-sso"), CITY_DI_SSO_IAM("citydi-sso-iam");

    private final String value;

    TokenIssuer(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Optional<TokenIssuer> fromValue(String value) {
        return StringUtils.isBlank(value) ? Optional.empty() : Arrays.stream(TokenIssuer.values())
                .filter(tokenIssuer -> tokenIssuer.getValue().equalsIgnoreCase(value))
                .findFirst();
    }
}
