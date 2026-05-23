package io.github.mojtaba.microservice.starter.iam.service.mapper;


import io.github.mojtaba.microservice.starter.iam.service.entities.User;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.UserDto;
import org.mapstruct.Mapper;

/**
 * author - mojtaba ghasemi
 * created on - 11/10/2025
 */

@Mapper(componentModel = "spring",uses ={RoleMapper.class})
public interface UserMapper extends BaseMapper<User, UserDto> {

}
