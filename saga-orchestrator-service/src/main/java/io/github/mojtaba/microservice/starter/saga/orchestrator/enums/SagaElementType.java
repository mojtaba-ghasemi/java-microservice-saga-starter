package io.github.mojtaba.microservice.starter.saga.orchestrator.enums;

import lombok.Getter;

@Getter
public enum SagaElementType {
    Process("PROC"), Sequence("SEQ"), Command("CMD");

    private String shortForm;

    SagaElementType(String shortForm) {
        this.shortForm = shortForm;
    }
}
