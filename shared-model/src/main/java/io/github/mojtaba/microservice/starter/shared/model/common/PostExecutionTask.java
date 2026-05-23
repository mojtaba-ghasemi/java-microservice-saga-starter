package io.github.mojtaba.microservice.starter.shared.model.common;

import java.util.Map;

public interface PostExecutionTask {
    /**
     *
     * @param requestResponseMap
     * @param sequenceContext
     */
    void execute(Map<String, String> requestResponseMap,
                 Map<String, String> sequenceContext);
}
