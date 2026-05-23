package io.github.mojtaba.microservice.starter.iam.servicemodel.util.serializer;

import io.github.mojtaba.microservice.starter.iam.servicemodel.util.DateUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.SneakyThrows;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeJsonDeserializer extends JsonDeserializer<LocalDateTime> {

    // ISO 8601
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_DEFAULT_PATTERN);

    @SneakyThrows
    @Override
    public LocalDateTime deserialize(
            JsonParser jsonParser,
            DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        return LocalDateTime.parse(jsonParser.getText(), dateFormat);
    }
}
