package io.github.mojtaba.microservice.starter.iam.service.entities;

import io.github.mojtaba.microservice.starter.iam.servicemodel.enums.Claim;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(of = {"title"})
@Document(collection = "role")
public class Role implements Serializable {

    public static final String ADMIN_ROLE_TITLE = "ADMIN";
    public static final String BACKOFFICE_MEMBER_ROLE_TITLE = "BACKOFFICE-MEMBER";
    public static final List<String> customerAllowedRoles = Arrays.asList("CUSTOMER", "PREMIUM_CUSTOMER", "GUEST");

    @Id
    private String id;

    private String title;

    private List<Claim> claims;

    @Transient
    public void addClaim(Claim claim){
        if(CollectionUtils.isEmpty(getClaims())){
            claims = new ArrayList<>();
        }
        if(!claims.contains(claim)) {
            claims.add(claim);
        }
    }

    @Transient
    public void removeClaim(Claim claim){
        if(CollectionUtils.isEmpty(getClaims())){
            return;
        }
        claims.remove(claim);
    }

}
