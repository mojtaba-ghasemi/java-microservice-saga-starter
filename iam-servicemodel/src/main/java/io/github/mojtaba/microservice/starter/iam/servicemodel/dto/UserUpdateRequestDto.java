package io.github.mojtaba.microservice.starter.iam.servicemodel.dto;

import io.github.mojtaba.microservice.starter.iam.servicemodel.enums.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserUpdateRequestDto {
    @NotBlank
    private String id;

    private List<String> roles;

    private Status status;

    private BaseUserDto baseUser;
    public List<String> getRoles() {
        return roles.stream().map(String::toUpperCase).collect(Collectors.toList());
    }
}
