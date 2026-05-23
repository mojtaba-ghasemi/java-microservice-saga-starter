package io.github.mojtaba.microservice.starter.shared.model.common;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DeserializableSpringDataPage<T> extends PageImpl<T> {

    private static final long serialVersionUID = 202109140934L;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public DeserializableSpringDataPage(@JsonProperty("content") List<T> content, @JsonProperty("number") int number,
                                        @JsonProperty("size") int size,
                                        @JsonProperty("totalElements") Long totalElements,
                                        @JsonProperty("pageable") JsonNode pageable,
                                        @JsonProperty("last") boolean last,
                                        @JsonProperty("totalPages") int totalPages,
                                        @JsonProperty("sort") JsonNode sort,
                                        @JsonProperty("first") boolean first,
                                        @JsonProperty("numberOfElements") int numberOfElements) {
        super(content, PageRequest.of(number, size), totalElements);
    }

    public DeserializableSpringDataPage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public DeserializableSpringDataPage(List<T> content) {
        super(content);
    }

    public DeserializableSpringDataPage() {
        super(new ArrayList<T>());
    }

}