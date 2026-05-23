package io.github.mojtaba.microservice.starter.iam.service.mapper;
import io.github.mojtaba.microservice.starter.iam.service.entities.Role;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.RoleDto;
import org.mapstruct.Mapper;

/**
 * author - mojtaba ghasemi
 * created on - 11/10/2025
 */

@Mapper(componentModel = "spring")
public interface RoleMapper extends BaseMapper<Role, RoleDto> {

}
