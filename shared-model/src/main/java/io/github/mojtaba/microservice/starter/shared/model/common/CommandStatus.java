package io.github.mojtaba.microservice.starter.shared.model.common;

public enum CommandStatus {


    CREATED,
    CHECK_PRE_EXECUTION_CONDITION,
    EXECUTE_INTERMEDIATE_PIPE,
    IN_PROGRESS,
    COMPLETED,
    ROLL_BACKED,
    ROLL_BACK_IN_PROGRESS,
    UNSUCCESSFUL_ROLLBACK,
    NO_RESPONSE,
    SKIPPED,
    COMMAND_TRANSFER_FAILED

}
