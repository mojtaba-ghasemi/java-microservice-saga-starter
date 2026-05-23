package io.github.mojtaba.microservice.starter.iam.service.services;

import io.github.mojtaba.microservice.starter.iam.service.entities.Role;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.IAMException;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.enums.BusinessException;
import io.github.mojtaba.microservice.starter.iam.service.mapper.RoleMapper;
import io.github.mojtaba.microservice.starter.iam.service.repositories.RoleRepository;
import io.github.mojtaba.microservice.starter.iam.service.repositories.UserRepository;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.RoleDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final UserRepository userRepository;

    public List<RoleDto> getAll(RoleDto roleDto) {
        return roleRepository
                .findAll(Example.of(roleMapper.mapToEntity(roleDto)))
                .stream()
                .map(roleMapper::mapToDto)
                .collect(Collectors.toList());
    }

    public void saveRole(RoleDto roleDto) {
        roleDto.setTitle(roleDto.getTitle());
        roleRepository.save(roleMapper.mapToEntity(roleDto));
    }

    public void deleteByTitle(String roleTitle) {

        Role role = roleRepository
                .findByTitle(roleTitle.toUpperCase())
                .orElseThrow(() -> new IAMException(BusinessException.ROLE_NOT_FOUNT_EXCEPTION));

        if (userRepository.userExistsByThisRole(role)) {
            throw new IAMException(BusinessException.ROLE_EXISTS_FOR_USER);
        }

        roleRepository.delete(role);
    }

}
