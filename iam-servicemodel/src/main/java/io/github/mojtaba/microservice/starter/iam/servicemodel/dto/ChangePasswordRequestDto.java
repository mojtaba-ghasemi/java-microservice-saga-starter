package io.github.mojtaba.microservice.starter.iam.servicemodel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangePasswordRequestDto {
    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 6, max = 24)
    private String oldPassword;
    @NotBlank
    @Size(min = 6, max = 24)
    private String newPassword;

    public String getUsername() {
        return StringUtils.isNotBlank(username) ? username.toLowerCase() : null;
    }
}

