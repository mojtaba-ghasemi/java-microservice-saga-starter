package io.github.mojtaba.microservice.starter.iam.servicemodel.util.serializer;

import io.github.mojtaba.microservice.starter.iam.servicemodel.util.DateUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeJsonSerializer extends JsonSerializer<LocalDateTime> {

    // ISO 8601
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern(DateUtil.DATE_TIME_DEFAULT_PATTERN);

    @Override
    public void serialize(
            LocalDateTime localDateTime,
            JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(dateFormat.format(localDateTime));
    }
}
