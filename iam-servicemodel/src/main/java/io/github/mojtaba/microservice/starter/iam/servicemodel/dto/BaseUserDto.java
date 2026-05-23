package io.github.mojtaba.microservice.starter.iam.servicemodel.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseUserDto {
    private String id;
    private String baseUsername;
    private String baseId;
    private String nationalCode;
    private Date creationDate;
    private Date modificationDate;
    private String creator;
    private String lastModifier;
}