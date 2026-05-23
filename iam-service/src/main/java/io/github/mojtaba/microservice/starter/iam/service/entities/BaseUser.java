package io.github.mojtaba.microservice.starter.iam.service.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Document(collection = "base-user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class BaseUser {
    @Id
    private String id;
    private String BaseUsername;
    private String BaseId;
    @Indexed(name = "nationalCode")
    private String nationalCode;
    private Date creationDate;
    private Date modificationDate;
    private String creator;
    private String lastModifier;
}
