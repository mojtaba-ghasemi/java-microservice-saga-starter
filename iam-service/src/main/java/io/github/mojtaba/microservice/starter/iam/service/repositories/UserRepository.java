package io.github.mojtaba.microservice.starter.iam.service.repositories;

import io.github.mojtaba.microservice.starter.iam.service.entities.Role;
import io.github.mojtaba.microservice.starter.iam.service.entities.User;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.IAMException;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.enums.BusinessException;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    @Query("{'username': ?0}")
    Optional<User> findByUsername(String username);

    @Query("{'username': ?0 , 'status': 'ACTIVE'}")
    Optional<User> findActiveByUsername(String username);

    @Query(value = "{'username': ?0}", exists = true)
    boolean existsByUsername(String username);

    @Query(value = "{'assignedRoles':?0}", exists = true)
    boolean userExistsByThisRole(Role role);

    default Optional<User> findActiveUser(String username) {
        Optional<User> userOptional = findActiveByUsername(username);
        if (!userOptional.isPresent()) {
            throw new IAMException(BusinessException.USER_IS_DISABLED);
        }
        return userOptional;
    }
    @Query("{'_id': ?0}")
    Optional<User> findById(String id);
}
