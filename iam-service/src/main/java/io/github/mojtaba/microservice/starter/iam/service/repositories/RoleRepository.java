package io.github.mojtaba.microservice.starter.iam.service.repositories;

import io.github.mojtaba.microservice.starter.iam.service.entities.Role;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.IAMException;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.enums.BusinessException;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends MongoRepository<Role, String> {

    @Query("{'title':?0}")
    Optional<Role> findByTitle(String title);

    default Role findBackOfficeMember(){
        return findByTitle(Role.BACKOFFICE_MEMBER_ROLE_TITLE)
                .orElseThrow(() -> new IAMException(BusinessException.BACKOFFICEMEMBER_ROLE_NOT_FOUND));
    }

    default Role findAdmin(){
        return findByTitle(Role.ADMIN_ROLE_TITLE)
                .orElseThrow(() -> new IAMException(BusinessException.BACKOFFICEMEMBER_ROLE_NOT_FOUND));
    }

}
