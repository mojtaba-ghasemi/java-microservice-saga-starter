package io.github.mojtaba.microservice.starter.iam.service.repositories;

import io.github.mojtaba.microservice.starter.iam.service.entities.Role;
import io.github.mojtaba.microservice.starter.iam.service.entities.User;
import io.github.mojtaba.microservice.starter.iam.service.exceptions.util.TypeUtil;
import io.github.mojtaba.microservice.starter.iam.servicemodel.dto.UserSearchRequestDto;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DynamicUserRepositoryImpl implements DynamicUserRepository {

    private final MongoTemplate mongoTemplate;
    private static final String LAST_LOGIN_LITERAL = "lastLogin";

    @Override
    public Page<User> findAll(UserSearchRequestDto request) {


        Pageable page = createPageFromRequest(request);

        Query query = new Query();

        query.with(page);

        addCriteriaForLastLoginTime(request, query);

        if (StringUtils.isNotBlank(request.getId())) {
            query.addCriteria(Criteria.where("id").is(request.getId()));
        }
        if (StringUtils.isNotBlank(request.getUsername())) {
            query.addCriteria(Criteria.where("username").regex(request.getUsername(), "i"));
        }
        if (request.getStatus() != null) {
            query.addCriteria(Criteria.where("status").is(request.getStatus().name()));
        }
        if (!CollectionUtils.isEmpty(request.getRoles())) {
            List<Object> roleIds = mongoTemplate
                    .find(Query.query(Criteria.where("title")
                            .in(request.getRoles())), Role.class).stream()
                    .map(Role::getId)
                    .map(idStr -> {
                        if(TypeUtil.isValidUUID(idStr)){
                            return idStr;
                        }
                        return new ObjectId(idStr);
                    })
                    .collect(Collectors.toList());
            query.addCriteria(Criteria.where("assignedRoles.$id").in(roleIds));
        }
        return PageableExecutionUtils
                .getPage(mongoTemplate.find(query, User.class),
                        page,
                        () -> mongoTemplate.count(query.limit(-1).skip(-1), User.class));
    }


    private void addCriteriaForLastLoginTime(UserSearchRequestDto request,
                                             Query query) {
        if (request.getLastLoginFrom() != null && request.getLastLoginTo() != null) {
            query.addCriteria(Criteria.where(LAST_LOGIN_LITERAL)
                    .gte(request.getLastLoginFrom())
                    .lte(request.getLastLoginTo()));
        } else if (request.getLastLoginFrom() != null) {
            query.addCriteria(Criteria.where(LAST_LOGIN_LITERAL)
                    .gte(request.getLastLoginFrom()));
        } else if (request.getLastLoginTo() != null) {
            query.addCriteria(Criteria.where(LAST_LOGIN_LITERAL)
                    .lt(request.getLastLoginTo()));
        }
    }


    private Pageable createPageFromRequest(UserSearchRequestDto request) {
        if (StringUtils.isNotBlank(request.getSortBy()) && request.getDirection() != null) {
            return PageRequest.of(request.getPageNumber(), request.getPageSize()
                    , request.getDirection(), request.getSortBy());
        }
        if (StringUtils.isNotBlank(request.getSortBy())) {
            return PageRequest.of(request.getPageNumber(), request.getPageSize(),
                    Sort.Direction.ASC, request.getSortBy());
        }
        if (request.getDirection() != null) {
            return PageRequest.of(request.getPageNumber(), request.getPageSize(),
                    request.getDirection(), "id");
        }
        return PageRequest.of(request.getPageNumber(), request.getPageSize());
    }

}
