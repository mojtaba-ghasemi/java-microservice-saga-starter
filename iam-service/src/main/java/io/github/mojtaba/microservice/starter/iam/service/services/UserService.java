package io.github.mojtaba.microservice.starter.iam.service.services;


import io.github.mojtaba.microservice.starter.iam.service.entities.BaseUser;
import io.github.mojtaba.microservice.starter.iam.service.entities.Role;
import io.github.mojtaba.microservice.starter.iam.service.entities.User;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.IAMException;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.enums.BusinessException;
import io.github.mojtaba.microservice.starter.iam.service.mapper.BaseUserMapper;
import io.github.mojtaba.microservice.starter.iam.service.mapper.RoleMapper;
import io.github.mojtaba.microservice.starter.iam.service.mapper.UserMapper;
import io.github.mojtaba.microservice.starter.iam.service.repositories.BaseUserRepository;
import io.github.mojtaba.microservice.starter.iam.service.repositories.DynamicUserRepository;
import io.github.mojtaba.microservice.starter.iam.service.repositories.UserRepository;
import io.github.mojtaba.microservice.starter.iam.service.repositories.RoleRepository;
import io.github.mojtaba.microservice.starter.iam.service.util.PasswordUtil;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordUtil passwordUtil;
    private final DynamicUserRepository dynamicUserRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final BaseUserRepository baseUserRepository;
    private final BaseUserMapper baseUserMapper;

    public UserDto signup(SignUpRequestDto requestDto) {

        if (userRepository.existsByUsername(requestDto.getUsername().toLowerCase(Locale.ROOT))) {
            throw new IAMException(BusinessException.USER_ALREADY_EXISTS);
        }

        if ((requestDto.getIsBaseUser()) &&
                (StringUtils.isBlank(requestDto.getBaseUser().getBaseId()) ||
                        StringUtils.isBlank(requestDto.getBaseUser().getBaseUsername()) ||
                        StringUtils.isBlank(requestDto.getBaseUser().getNationalCode()))) {
            throw new IAMException(BusinessException.BASE_USER_INFO_IN_INVALID);
        }

        if (requestDto.getBaseUser() != null && baseUserRepository.existsByNationalCode(requestDto.getBaseUser().getNationalCode())) {
            throw new IAMException(BusinessException.BASE_USER_ALREADY_EXISTS);
        }

        User userToSave = new User();
        userToSave.init(requestDto);

        if (requestDto.getIsBaseUser()) {
            BaseUser BaseUser = baseUserRepository.save(new BaseUser()
                    .setId(UUID.randomUUID().toString())
                    .setBaseId(requestDto.getBaseUser().getBaseId())
                    .setBaseUsername(requestDto.getBaseUser().getBaseUsername())
                    .setNationalCode(requestDto.getBaseUser().getNationalCode())
                    .setCreationDate(new Date())
                    .setCreator(requestDto.getBaseUser().getCreator()));

            userToSave.setBaseUser(BaseUser);
        }

        User savedUser = roleRepository.findByTitle(Role.BACKOFFICE_MEMBER_ROLE_TITLE)
                .map(backofficeMember -> {
                    userToSave.assignRole(backofficeMember);
                    return userRepository.save(userToSave);
                }).orElseThrow(() -> new IAMException(BusinessException.BACKOFFICEMEMBER_ROLE_NOT_FOUND));

        log.info("User {} is created with roles {}",
                savedUser.getUsername(),
                savedUser.getAssignedRoles()
        );

        return userMapper.mapToDto(savedUser);
    }

    public UserDto signupCustomer(SignUpRequestDto requestDto) {

        if (userRepository.existsByUsername(requestDto.getUsername().toLowerCase(Locale.ROOT))) {
            throw new IAMException(BusinessException.USER_ALREADY_EXISTS);
        }

        List<String> requestedRoles = requestDto.getAssignedRoles();

        if (requestedRoles == null || requestedRoles.isEmpty()) {
            throw new IAMException(BusinessException.ROLE_NOT_FOUNT_EXCEPTION);
        }

        for (String roleTitle : requestedRoles) {
            if (!Role.customerAllowedRoles.contains(roleTitle)) {
                throw new IAMException(BusinessException.CUSTOMER_INVALID_ROLE_EXCEPTION);
            }
        }
        User userToSave = new User();
        userToSave.init(requestDto);
        User savedUser = userRepository.save(userToSave);


        log.info("User {} is created with roles {}",
                savedUser.getUsername(),
                savedUser.getAssignedRoles()
        );

        return userMapper.mapToDto(savedUser);
    }

    public LoginResponseDto login(UserLoginDto loginDto) {
        return userRepository
                .findActiveUser(loginDto.getUsername())
                .filter(user ->
                        passwordUtil.checkPassword(
                              loginDto.getUsername(),
                                loginDto.getPassword(),
                                user.getPassword()
                        )
                ).map(user -> {
                    //TODO: At the first we set data in database or bank update users in panel ?!!!
                    baseUserRepository.findByUsername(loginDto.getBaseUsername())
                            .ifPresent(user::setBaseUser);
                    user.setLastLogin(LocalDateTime.now());
                    userRepository.save(user);
                    return new LoginResponseDto(roleMapper.mapToDtos(user.getAssignedRoles()) , user.isAcceptTermsAndConditions());
                })
                .orElseThrow(() -> new IAMException(BusinessException.INVALID_CREDENTIALS));
    }

    public LoginResponseDto acceptTermsAndConditions(UserLoginDto loginDto) {
        return userRepository
                .findActiveUser(loginDto.getUsername())
                .filter(user ->
                        passwordUtil.checkPassword(
                                loginDto.getUsername(),
                                loginDto.getPassword(),
                                user.getPassword()
                        )
                ).map(user -> {
                    user.setAcceptTermsAndConditions(true);
                    userRepository.save(user);
                    return new LoginResponseDto(roleMapper.mapToDtos(user.getAssignedRoles()) , user.isAcceptTermsAndConditions());
                })
                .orElseThrow(() -> new IAMException(BusinessException.INVALID_CREDENTIALS));
    }

    public BaseUserDto fetchBaseUserInfoFromUser(FetchBaseUserRequestDto fetchBankoUserRequestDto) {
        return userRepository.findActiveByUsername(fetchBankoUserRequestDto.getUsername())
                .map(user -> {
                    BaseUserDto bankoUser = new BaseUserDto();
                    if ((Objects.nonNull(user.getBaseUser())) && (StringUtils.isNotBlank(user.getBaseUser().getId()))) {
                        bankoUser = baseUserRepository.findById(user.getBaseUser().getId())
                                .map(baseUserMapper::mapToDto).orElseThrow(() ->
                                        new IAMException(BusinessException.BASE_USER_NOT_FOUND));
                    }
                    return bankoUser;
                }).orElseThrow(() -> new IAMException(BusinessException.USER_NOT_FOUND_EXCEPTION));
    }

    public Page<UserDto> getAll(UserSearchRequestDto requestDto) {
        return dynamicUserRepository
                .findAll(requestDto)
                .map(userMapper::mapToDto);
    }

    public void changePassword(ChangePasswordRequestDto requestDto) {

        User fetchedUser = userRepository
                .findActiveUser(requestDto.getUsername())
                .filter(user -> passwordUtil.checkPassword(requestDto.getUsername(),
                        requestDto.getOldPassword(), user.getPassword()))
                .orElseThrow(() -> new IAMException(BusinessException.CHANGE_PASSWORD_EXCEPTION));
        fetchedUser.setPassword(
                passwordUtil.hashPassword(
                        requestDto.getUsername(),
                        requestDto.getNewPassword()
                ));
        userRepository.save(fetchedUser);
    }

    public void resetPassword(ResetPasswordRequestDto requestDto) {

        User user = userRepository
                .findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new IAMException(BusinessException.CHANGE_PASSWORD_EXCEPTION));
        user.setPassword(
                passwordUtil.hashPassword(
                        requestDto.getUsername(),
                        requestDto.getNewPassword())
        );
        userRepository.save(user);
    }

    public void updateUser(UserUpdateRequestDto requestDto) {
        User user = userRepository
                .findById(requestDto.getId())
                .orElseThrow(() -> new IAMException(BusinessException.USER_NOT_FOUND_EXCEPTION));
        user.resetRoles();
        if (CollectionUtils.isNotEmpty(requestDto.getRoles())) {
            List<Role> roles = requestDto.getRoles()
                    .stream()
                    .map(roleTitle -> roleRepository
                            .findByTitle(roleTitle)
                            .orElseThrow(() -> new IAMException(BusinessException.ROLE_NOT_FOUNT_EXCEPTION)))
                    .collect(Collectors.toList());
            roles.forEach(user::assignRole);
        }
        if (requestDto.getStatus() != null) {
            user.setStatus(requestDto.getStatus());
        }
        if (Objects.nonNull(requestDto.getBaseUser()) && (StringUtils.isNotBlank(requestDto.getBaseUser().getId()))) {
            BaseUser BaseUser = baseUserRepository
                    .findById(requestDto.getBaseUser().getId())
                    .orElseThrow(() -> new IAMException(BusinessException.BASE_USER_NOT_FOUND));
            baseUserRepository.save(BaseUser
                    .setBaseUsername(requestDto.getBaseUser().getBaseUsername())
                    .setBaseId(requestDto.getBaseUser().getBaseId())
                    .setNationalCode(requestDto.getBaseUser().getNationalCode())
                    .setModificationDate(new Date())
                    .setLastModifier(requestDto.getBaseUser().getLastModifier()));
            user.setBaseUser(BaseUser);
        }
        userRepository.save(user);
    }


}
