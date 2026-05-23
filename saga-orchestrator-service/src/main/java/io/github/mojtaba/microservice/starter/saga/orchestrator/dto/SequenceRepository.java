package io.github.mojtaba.microservice.starter.saga.orchestrator.dto;


import io.github.mojtaba.microservice.starter.saga.orchestrator.model.SagaSequence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SequenceRepository extends MongoRepository<SagaSequence, String> {

}
