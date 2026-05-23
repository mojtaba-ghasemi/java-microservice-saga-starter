package io.github.mojtaba.microservice.starter.iam.servicemodel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginResponseDto {
    private List<RoleDto> roles;
    private boolean acceptTermsAndConditions;
}
