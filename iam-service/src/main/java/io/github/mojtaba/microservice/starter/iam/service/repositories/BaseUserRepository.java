package io.github.mojtaba.microservice.starter.iam.service.repositories;

import io.github.mojtaba.microservice.starter.iam.service.entities.BaseUser;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BaseUserRepository extends MongoRepository<BaseUser,String> {
    @Query(value = "{'BaseUsername' : :#{#username}}")
    Optional<BaseUser> findByUsername(@Param("username") String username);
    @Query(value = "{'nationalCode' : :#{#nationalCode}}")
    Optional<BaseUser> findByNationalCode(@Param("nationalCode") String nationalCode);

    @Query(value = "{'nationalCode': ?0}", exists = true)
    boolean existsByNationalCode(String nationalCode);
}
