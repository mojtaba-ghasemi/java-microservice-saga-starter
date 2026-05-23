package io.github.mojtaba.microservice.starter.saga.orchestrator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UploadDocumentTokenData implements Serializable {
    public static final String SESSION_EXPIRE_DATE = "SESSION_EXPIRE_DATE";
    public static final String USERNAME = "USERNAME";

    private Date sessionExpireDate;
}