package com.example.article_approval.user;

import com.example.article_approval.article.Article;
import com.example.article_approval.article.ArticleRepository;
import com.example.article_approval.task.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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
        Optional<User> user = userRepository.findUserById(id);
        if(user.isPresent())
            return user.get();
        else
            return null;
    }

    @PostMapping
    public User createUser(@Validated @RequestBody User user) {
        return userRepository.save(user);
    }

    @GetMapping(value = "/articles/created/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Article> findAllDocsCreatedByUser(@PathVariable(value = "id") String id) {
        Optional<User> user = userRepository.findUserById(id);
        if(user.isEmpty())
            return new ArrayList<>();
        Optional<List<Article>> articles = articleRepository.findArticlesByCreatorId(id);
        if(articles.isEmpty())
            return new ArrayList<>();
        return articles.get();
    }

    @GetMapping(value = "/articles/toApprove/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Object> findAllDocsToApprove(@PathVariable(value = "id") String id) {
        Optional<List<Object>> optionalTaskList = taskRepository.findTasksBySentToIdAndStatus(id, "PENDING");
        if(optionalTaskList.isEmpty())
            return new ArrayList<>();
        return optionalTaskList.get();
    }



}
