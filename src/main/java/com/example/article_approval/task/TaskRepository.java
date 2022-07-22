package com.example.article_approval.task;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {
    @Query
    Optional<Task> findTaskById(String id);

    @Query(value="{ 'sentToId' : ?0, 'status' : ?1 }", fields="{ 'articleId' : 1, '_id': 0}")
    Optional<List<Object>> findTasksBySentToIdAndStatus(String sentToId, String status);
}
