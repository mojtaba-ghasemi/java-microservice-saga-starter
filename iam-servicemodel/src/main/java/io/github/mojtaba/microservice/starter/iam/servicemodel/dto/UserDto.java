package io.github.mojtaba.microservice.starter.iam.servicemodel.dto;


import io.github.mojtaba.microservice.starter.iam.servicemodel.enums.Status;
import io.github.mojtaba.microservice.starter.iam.servicemodel.util.serializer.LocalDateTimeJsonDeserializer;
import io.github.mojtaba.microservice.starter.iam.servicemodel.util.serializer.LocalDateTimeJsonSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import static io.github.mojtaba.microservice.starter.iam.servicemodel.util.DateUtil.DATE_TIME_DEFAULT_PATTERN;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto implements Serializable {

    private String id;

    private String username;

    private Status status;

    private List<RoleDto> assignedRoles;

    private BaseUserDto baseUser;

    @DateTimeFormat(pattern = DATE_TIME_DEFAULT_PATTERN)
    @JsonFormat(pattern = DATE_TIME_DEFAULT_PATTERN)
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
    private LocalDateTime lastLogin;
}

