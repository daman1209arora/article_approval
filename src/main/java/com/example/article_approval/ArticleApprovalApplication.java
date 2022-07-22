package com.example.article_approval;

import com.example.article_approval.article.Article;
import com.example.article_approval.article.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ArticleApprovalApplication implements CommandLineRunner {
    @Autowired
    ArticleRepository articleRepository;

    public static void main(String[] args) {
        SpringApplication.run(ArticleApprovalApplication.class, args);
    }
    public void run(String... args) {
//        articleRepository.save(
//                new Article(
//                    "62c7c4d371978b345496b606",
//                    "62c7d90e1e1c9636c29a8ef6",
//                    "Starting Spring Boot",
//                    "",
//                    "PENDING"
//                )
//        );
//        System.out.println(articleRepository.findArticleById("62d9b19999507222894b92d2"));
    }

}
