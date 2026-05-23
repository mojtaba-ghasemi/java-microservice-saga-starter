package io.github.mojtaba.microservice.starter.iam.service.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.IAMException;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.enums.BusinessException;
import io.github.mojtaba.microservice.starter.iam.service.repositories.RoleRepository;
import io.github.mojtaba.microservice.starter.iam.service.util.ContextUtil;
import io.github.mojtaba.microservice.starter.iam.service.util.PasswordUtil;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.SignUpRequestDto;
import io.github.mojtaba.microservice.starter.iam.servicemodel.enums.Status;
import io.github.mojtaba.microservice.starter.iam.servicemodel.util.serializer.LocalDateTimeJsonDeserializer;
import io.github.mojtaba.microservice.starter.iam.servicemodel.util.serializer.LocalDateTimeJsonSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.mojtaba.microservice.starter.iam.servicemodel.util.DateUtil.DATE_TIME_DEFAULT_PATTERN;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"username"})
@Document(collection = "user")
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {

    @Id
    private String id;

    private String username;

    private Status status;

    private BaseUser BaseUser;

    @DBRef
    private List<Role> assignedRoles;

    private String password;

    @DateTimeFormat(pattern = DATE_TIME_DEFAULT_PATTERN)
    @JsonFormat(pattern = DATE_TIME_DEFAULT_PATTERN)
    @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
    @JsonDeserialize(using = LocalDateTimeJsonDeserializer.class)
    private LocalDateTime lastLogin;
    private boolean acceptTermsAndConditions;

    public List<Role> getAssignedRoles() {
        if(CollectionUtils.isEmpty(assignedRoles)){
            assignedRoles = new ArrayList<>();
        }
        return assignedRoles;
    }

    @Transient
    public void assignRole(Role role){
        if(CollectionUtils.isEmpty(getAssignedRoles())){
            assignedRoles = new ArrayList<>();
        }
        if(!getAssignedRoles().contains(role)) {
            assignedRoles.add(role);
        }
    }

    @Transient
    public void resetRoles(){
        RoleRepository roleRepository = ContextUtil.getContext().getBean(RoleRepository.class);
        assignedRoles = new ArrayList<>();
        assignedRoles.add(roleRepository.findBackOfficeMember());
    }

    @Transient
    public void init(SignUpRequestDto requestDto) {
        PasswordUtil passwordUtil = ContextUtil.getContext().getBean(PasswordUtil.class);
        RoleRepository roleRepository = ContextUtil.getContext().getBean(RoleRepository.class);
        setUsername(requestDto.getUsername());
        setPassword(passwordUtil.hashPassword(requestDto.getUsername(), requestDto.getPassword()));
        setStatus(requestDto.getStatus());
        List<Role> roleList = requestDto
                .getAssignedRoles()
                .stream()
                .map(roleTitle -> roleRepository.findByTitle(roleTitle)
                        .orElseThrow(() -> new IAMException(BusinessException.ROLE_NOT_FOUNT_EXCEPTION)))
                .collect(Collectors.toList());
        setAssignedRoles(roleList);
    }

    @Transient
    public void unAssign(Role role){
        getAssignedRoles().remove(role);
    }
}
