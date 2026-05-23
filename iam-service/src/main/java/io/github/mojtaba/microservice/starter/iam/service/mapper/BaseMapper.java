package io.github.mojtaba.microservice.starter.iam.service.mapper;

import java.util.List;

/**
 * author - mojtaba ghasemi
 * created on - 11/10/2025
 */

public interface BaseMapper<E,D> {
    E mapToEntity(D dto);
    D mapToDto(E entity);
    List<E> mapToEntities(List<D> dto);
    List<D> mapToDtos(List<E> entities);
}
