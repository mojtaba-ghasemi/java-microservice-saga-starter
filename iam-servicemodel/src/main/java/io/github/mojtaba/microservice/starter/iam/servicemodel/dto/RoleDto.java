package io.github.mojtaba.microservice.starter.iam.servicemodel.dto;

import io.github.mojtaba.microservice.starter.iam.servicemodel.enums.Claim;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * author - sh.khalajestani
 * created on - 4/27/2022
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RoleDto implements Serializable {

    private String id;

    private String title;

    private List<Claim> claims;

    public String getTitle() {
        return StringUtils.isNotBlank(title) ? title.toUpperCase() : null;
    }
}

