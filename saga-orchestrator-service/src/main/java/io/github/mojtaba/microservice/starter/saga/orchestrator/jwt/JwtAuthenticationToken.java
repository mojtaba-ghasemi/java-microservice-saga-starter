package io.github.mojtaba.microservice.starter.saga.orchestrator.jwt;


import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Mazaher Namjoofar(maz.namjo@gmail.com) on 1/2/2021.
 */
public class JwtAuthenticationToken implements Authentication {

    private JwtDataDto jwtDataDto;


    public JwtAuthenticationToken(JwtDataDto jwtDataDto) {
        this.jwtDataDto = jwtDataDto;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(this.jwtDataDto.getRolesName())) {
            this.jwtDataDto.getRolesName().forEach(r -> {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(r);
                authorities.add(authority);
            });
        }
        if(CollectionUtils.isNotEmpty(this.jwtDataDto.getPermissionName())) {
            this.jwtDataDto.getPermissionName().forEach(r -> {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(r);
                authorities.add(authority);
            });
        }
        return authorities;
    }

    @Override
    public JwtDataDto getCredentials() {
        return jwtDataDto;
    }

    @Override
    public JwtDataDto getDetails() {
        return jwtDataDto;
    }

    @Override
    public Object getPrincipal() {
        return jwtDataDto.getUsername();
    }

    @Override
    public boolean isAuthenticated() {
        //todo:
        return true;
    }

    @Override
    public void setAuthenticated(boolean b) throws IllegalArgumentException {
        return;
    }

    @Override
    public String getName() {
        return jwtDataDto.getUsername();
    }
}
