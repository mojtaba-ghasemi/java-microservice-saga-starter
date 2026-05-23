package io.github.mojtaba.microservice.starter.saga.orchestrator.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtDataDto implements Serializable {

    private String username;
    private String lastname;
    private List<String> rolesName;
    private List<String> permissionName;
    private String cif;
    private String mobile;
    private String customerId;
    private String userIP;

}
