package io.github.mojtaba.microservice.starter.iam.service.mapper;

import io.github.mojtaba.microservice.starter.iam.service.entities.BaseUser;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.BaseUserDto;
import org.mapstruct.Mapper;

/**
 * author - mojtaba ghasemi
 * created on - 11/10/2025
 */
@Mapper(componentModel = "spring")
public interface BaseUserMapper extends BaseMapper<BaseUser, BaseUserDto> {
}
