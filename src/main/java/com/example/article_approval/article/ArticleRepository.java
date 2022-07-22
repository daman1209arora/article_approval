package com.example.article_approval.article;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, String> {
    @Query
    Optional<Article> findArticleById(String id);

    @Query
    Optional<List<Article>> findArticlesByCreatorId(String creatorId);
}
