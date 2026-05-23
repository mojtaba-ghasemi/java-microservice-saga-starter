package io.github.mojtaba.microservice.starter.shared.model.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonUtil {

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String jsonize(Object object) throws JsonProcessingException {
        return jsonize(object, JsonInclude.Include.ALWAYS);
    }

    public static String jsonize(Object object, JsonInclude.Include include) throws JsonProcessingException {
        objectMapper.setSerializationInclusion(include);
        return objectMapper.writeValueAsString(object);
    }

    public static <T> T parseJson(String value, Class<T> targetClass) throws JsonProcessingException {
        return objectMapper.readValue(value, targetClass);
    }

    public static <T> List<T> parseJsonOfList(String value, Class<T> collectionElementClazz) throws JsonProcessingException {
        CollectionType javaType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, collectionElementClazz);
        return objectMapper.readValue(value, javaType);
    }

    public static <T> List<T> parseJsonOfList(File file, Class<T> collectionElementClazz) throws IOException {
        CollectionType javaType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, collectionElementClazz);
        return objectMapper.readValue(file, javaType);
    }


}
