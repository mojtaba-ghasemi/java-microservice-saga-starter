package io.github.mojtaba.microservice.starter.saga.orchestrator.common;

import java.util.UUID;

public class UUIDGenerator {

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString().replace("-",""));
    }

    public static String generateRandomUUID() {
        return UUID.randomUUID().toString().replace("-","");
//        return UUID.randomUUID().toString();
    }
}
