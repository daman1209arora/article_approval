package com.example.article_approval.workflow;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface WorkflowRepository extends MongoRepository<Workflow, String> {
    @Query
    Optional<Workflow> findWorkflowById(String id);
}
