package io.github.mojtaba.microservice.starter.iam.servicemodel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserLoginDto {
    @NotBlank
    private String password;
    @NotBlank
    private String username;

    private String baseUsername;

    public String getUsername() {
        return StringUtils.isNotBlank(username) ? username.toLowerCase() : null;
    }
}
