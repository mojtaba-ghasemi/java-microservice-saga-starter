package io.github.mojtaba.microservice.starter.iam.service.repositories;

import io.github.mojtaba.microservice.starter.iam.service.entities.User;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.UserSearchRequestDto;
import org.springframework.data.domain.Page;

public interface DynamicUserRepository {
    Page<User> findAll(UserSearchRequestDto request);
}
