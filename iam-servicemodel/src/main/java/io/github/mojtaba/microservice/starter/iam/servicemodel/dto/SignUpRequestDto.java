package io.github.mojtaba.microservice.starter.iam.servicemodel.dto;

import io.github.mojtaba.microservice.starter.iam.servicemodel.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SignUpRequestDto {

    @NotBlank
    @Size(min = 4, max = 24)
    private String username;

    @NotBlank
    @Size(min = 6, max = 24)
    private String password;

    @NotNull
    private Status status;

    @NotEmpty
    private List<String> assignedRoles;

    private BaseUserDto baseUser;

    private Boolean isBaseUser;
    public String getUsername() {
        return StringUtils.isNotBlank(username) ? username.toLowerCase() : null;
    }
}
