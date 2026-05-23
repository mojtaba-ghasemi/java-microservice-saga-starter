package io.github.mojtaba.microservice.starter.shared.model.common;

import java.util.Map;

public interface IntermediatePipe {


    /**
     *
     * @param currentContent
     * @param input
     * @param sequenceContext
     * @return
     */
    String pipe(String currentContent, Map<String, String> input, Map<String, String> sequenceContext);

}
