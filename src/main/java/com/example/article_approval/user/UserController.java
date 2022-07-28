package com.example.article_approval.user;

import com.example.article_approval.article.Article;
import com.example.article_approval.article.ArticleRepository;
import com.example.article_approval.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private TaskRepository taskRepository;

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User findUserById(@PathVariable(value = "id") String id) {
        try {
            return userRepository.findUserById(id).get();
        }
        catch(NoSuchElementException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User with this ID doesn't exist!"
            );
        }
    }

    @PostMapping
    public User createUser(@Validated @RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping(value = "/articles/created/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Article> findAllArticlesCreatedByUser(@PathVariable(value = "id") String id) {
        return articleRepository.findArticlesByCreatorId(id).get();
    }

    @GetMapping(value = "/articles/toApprove/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Object> findAllArticlesToApprove(@PathVariable(value = "id") String id) {
        return taskRepository.findTasksBySentToIdAndStatus(id, "PENDING");
    }
}
