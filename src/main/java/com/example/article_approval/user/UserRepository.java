package com.example.article_approval.user;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    @Query
    Optional<User> findUserById(String id);

    @Query
    boolean existsById(String id);
}
