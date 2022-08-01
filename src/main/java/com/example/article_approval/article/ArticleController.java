package com.example.article_approval.article;

import com.example.article_approval.mail.Mailer;
import com.example.article_approval.task.Task;
import com.example.article_approval.task.TaskRepository;
import com.example.article_approval.user.User;
import com.example.article_approval.user.UserRepository;
import com.example.article_approval.workflow.Workflow;
import com.example.article_approval.workflow.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/article")
public class ArticleController {
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkflowRepository workflowRepository;
    @Autowired
    private TaskRepository taskRepository;

    @GetMapping("/{id}")
    public Article findArticleById(@PathVariable(value = "id") String id) {
        try {
            return articleRepository.findArticleById(id).get();
        } catch(NoSuchElementException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Article with this ID doesn't exist!"
            );
        }
    }

    @PostMapping
    public Article createArticle(@Validated @RequestBody Article article) {
        String creatorId = article.getCreatorId();
        try {
            User creator = userRepository.findUserById(creatorId).get();
            if(!creator.getCanCreateDoc()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Creator with ID "+ creatorId + " can not create an article!"
                );
            }
        } catch (NoSuchElementException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User with this ID doesn't exist!"
            );
        }

        String workflowId = article.getWorkflowId();
        try {
            workflowRepository.findWorkflowById(workflowId).get();
        } catch (NoSuchElementException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Workflow with this ID doesn't exist!"
            );
        }
        return articleRepository.save(article);
    }

    @PostMapping("/initiate/")
    public Task initiateArticleApproval(@Validated @RequestBody Map<String, String> body) {
        String articleId = body.get("articleId");
        String userId = body.get("userId");
        System.out.println("Got " + articleId + " " + userId);
        Optional<Article> optionalArticle = articleRepository.findArticleById(articleId);
        if(optionalArticle.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Article with this ID doesn't exist!"
            );
        }
        Article article = optionalArticle.get();
        String creatorId = article.getCreatorId();
        if(userId.equals(creatorId)) {
            String latestTaskId = article.getLatestTaskId();
            Boolean noTask = latestTaskId.equals("");
            if(noTask) {
                return createAndAssignNextTask(articleId, article, creatorId);
            }
            else {
                Task latestTask = taskRepository.findTaskById(latestTaskId).get();
                if (latestTask.getSentToId().equals(creatorId) && latestTask.getStatus().equals("PENDING")) {
                    // Change status of latestTask
                    latestTask.setStatus("APPROVED");
                    taskRepository.save(latestTask);
                    return createAndAssignNextTask(articleId, article, creatorId);
                }
                else {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "User can't initiate task approval!"
                    );
                }

            }
        }
        else {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "This user can't initiate approval!"
            );
        }
    }

    private Task createAndAssignNextTask(String articleId, Article article, String creatorId) {
        Workflow workflow = workflowRepository.findWorkflowById(article.getWorkflowId()).get();
        String firstApprover = workflow.getMembers().get(0);
        Task task = new Task(null, articleId, creatorId, firstApprover, "", "PENDING");
        Task createdTask = taskRepository.save(task);
        String newTaskId = createdTask.getId();
        article.setLatestTaskId(newTaskId);
        articleRepository.save(article);

        String destEmailId = userRepository.findUserById(firstApprover).get().getEmailId();
        Mailer.sendEmail(destEmailId, article.getDocumentContent(), "Document for approval");
        return createdTask;
    }

    @PostMapping("/finish/")
    public ResponseEntity finishTask(@Validated @RequestBody Map<String, String> body) {
        String userId = body.get("userId");
        String taskId = body.get("taskId");
        String action = body.get("action");
        String comment = body.get("comment");

        Optional<Task> optionalTask = taskRepository.findTaskById(taskId);
        if(optionalTask.isEmpty())
            return ResponseEntity.notFound().build();
        Task task = optionalTask.get();
        String articleId = task.getArticleId();

        Optional<Article> optionalArticle = articleRepository.findArticleById(articleId);
        if(optionalArticle.isEmpty())
            return ResponseEntity.notFound().build();
        Article article = optionalArticle.get();

        String workflowId = article.getWorkflowId();
        String creatorId = article.getCreatorId();
        if(article.getLatestTaskId().equals(taskId)) {
            if(task.getSentToId().equals(userId) && task.getStatus().equals("PENDING")) {
                Optional<Workflow> optionalWorkflow = workflowRepository.findWorkflowById(workflowId);
                if(optionalWorkflow.isEmpty())
                    return ResponseEntity.notFound().build();
                Workflow workflow = optionalWorkflow.get();

                if(action.equals("REJECT")) {
                    // Create new task for creator
                    String newTaskId = taskRepository.save(new Task(null, articleId, userId, creatorId, comment, "PENDING")).getId();

                    article.setLatestTaskId(newTaskId);
                    articleRepository.save(article);

                    task.setStatus("REJECTED");
                    taskRepository.save(task);

                    String destEmailId = userRepository.findUserById(creatorId).get().getEmailId();
                    Mailer.sendEmail(destEmailId, article.getDocumentContent(), "Document rejected");

                    return ResponseEntity.ok().build();
                }
                else {
                    // Check if someone is next in line
                    List<String> members = workflow.getMembers();
                    int index = members.indexOf(userId);
                    if(index == (members.size() - 1)) {
                        article.setLatestTaskId("");
                        article.setArticleStatus("APPROVED");

                    }
                    else {
                        String nextUserId = members.get(index+1);
                        // Create new task for next approver
                        String newTaskId = taskRepository.save(new Task(null, articleId, userId, nextUserId, comment, "PENDING")).getId();
                        article.setLatestTaskId(newTaskId);

                        String destEmailId = userRepository.findUserById(nextUserId).get().getEmailId();
                        Mailer.sendEmail(destEmailId, article.getDocumentContent(), "Mail for approval");
                    }
                    articleRepository.save(article);
                    task.setStatus("APPROVED");
                    taskRepository.save(task);
                    return ResponseEntity.ok().build();
                }
            }
            else
                return ResponseEntity.notFound().build();
        }
        else
            return ResponseEntity.notFound().build();
    }


}
