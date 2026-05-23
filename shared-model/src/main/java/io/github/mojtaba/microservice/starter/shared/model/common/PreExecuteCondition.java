package io.github.mojtaba.microservice.starter.shared.model.common;

import java.util.Map;

public interface PreExecuteCondition {

    /**
     *
     * @param requestResponseMap
     * @return
     */
    default Boolean executeCondition(Map<String, String> requestResponseMap) {
        return executeCondition(requestResponseMap, null);
    }

    /**
     *
     * @param requestResponseMap
     * @param sequenceContext
     * @return
     */
    Boolean executeCondition(Map<String, String> requestResponseMap, Map<String, String> sequenceContext);

}
