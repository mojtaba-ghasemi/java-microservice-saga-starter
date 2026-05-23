package io.github.mojtaba.microservice.starter.saga.orchestrator.jwt;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
public class JWTUserDetails {

    private String username;
    private Date creationDate;
    private Set<String> roles;
    private Set<String> permissions;

    public JWTUserDetails(String username, Set<String> roles, Set<String> permissions) {
        this.username = username;
        this.roles = roles;
        this.permissions = permissions;
        this.creationDate = new Date();
    }
}
