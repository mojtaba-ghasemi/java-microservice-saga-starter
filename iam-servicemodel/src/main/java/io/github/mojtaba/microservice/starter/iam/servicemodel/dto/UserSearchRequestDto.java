package io.github.mojtaba.microservice.starter.iam.servicemodel.dto;

import io.github.mojtaba.microservice.starter.iam.servicemodel.enums.Status;
import io.github.mojtaba.microservice.starter.iam.servicemodel.util.serializer.LocalDateTimeJsonDeserializer;
import io.github.mojtaba.microservice.starter.iam.servicemodel.util.serializer.LocalDateTimeJsonSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

import static io.github.mojtaba.microservice.starter.iam.servicemodel.util.DateUtil.DATE_TIME_DEFAULT_PATTERN;


/**
 * author - sh.khalajestani
 * created on - 4/16/2022
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = true)
public class UserSearchRequestDto extends PageRequestDto {
    private String id;
    private String username;
    private List<String> roles;
    private Status status;
    private BaseUserDto baseUser;
    @DateTimeFormat(pattern = DATE_TIME_DEFAULT_PATTERN)
    @JsonFormat(pattern = DATE_TIME_DEFAULT_PATTERN)
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
    private LocalDateTime lastLoginTo;
    @DateTimeFormat(pattern = DATE_TIME_DEFAULT_PATTERN)
    @JsonFormat(pattern = DATE_TIME_DEFAULT_PATTERN)
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
    private LocalDateTime lastLoginFrom;
}
