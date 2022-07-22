package com.example.article_approval.article;

import com.example.article_approval.mail.Mailer;
import com.example.article_approval.task.Task;
import com.example.article_approval.task.TaskRepository;
import com.example.article_approval.user.User;
import com.example.article_approval.user.UserRepository;
import com.example.article_approval.workflow.Workflow;
import com.example.article_approval.workflow.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
        Optional<Article> article = articleRepository.findArticleById(id);
        if(article.isPresent())
            return article.get();
        else
            return null;
    }

    @PostMapping
    public Article createArticle(@Validated @RequestBody Article article) {
        return articleRepository.save(article);
    }

    @PostMapping("/initiate/")
    public ResponseEntity initiateArticleApproval(@Validated @RequestBody Map<String, String> body) {
        String articleId = body.get("articleId");
        String userId = body.get("userId");
        Optional<Article> optionalArticle = articleRepository.findArticleById(articleId);
        if(optionalArticle.isEmpty())
            return ResponseEntity.notFound().build();

        Article article = optionalArticle.get();
        String creatorId = article.getCreatorId();
        if(userId.equals(creatorId)) {
            String latestTaskId = article.getLatestTaskId();
            Boolean noTask = latestTaskId.equals("");
            if(noTask) {
                return createAndAssignNextTask(articleId, article, creatorId);
            }
            else {
                Optional<Task> optionalLatestTask = taskRepository.findTaskById(latestTaskId);
                if(optionalLatestTask.isEmpty())
                    return ResponseEntity.notFound().build();
                Task latestTask = optionalLatestTask.get();
                if (latestTask.getSentToId().equals(creatorId) && latestTask.getStatus().equals("PENDING")) {
                    // Change status of latestTask
                    latestTask.setStatus("APPROVED");
                    taskRepository.save(latestTask);
                    return createAndAssignNextTask(articleId, article, creatorId);
                }
                else
                    return ResponseEntity.notFound().build();
            }
        }
        else
            return ResponseEntity.notFound().build();
    }

    private ResponseEntity createAndAssignNextTask(String articleId, Article article, String creatorId) {
        Optional<Workflow> optionalWorkflow = workflowRepository.findWorkflowById(article.getWorkflowId());
        if(optionalWorkflow.isEmpty())
            return ResponseEntity.notFound().build();
        Workflow workflow = optionalWorkflow.get();
        String firstApprover = workflow.getMembers().get(0);
        String newTaskId = taskRepository.save(new Task(articleId, creatorId, firstApprover, "", "PENDING")).getId();
        article.setLatestTaskId(newTaskId);
        articleRepository.save(article);

        String destEmailId = userRepository.findUserById(firstApprover).get().getEmailId();
        Mailer.sendEmail(destEmailId, article.getDocumentContent(), "Document for approval");
        return ResponseEntity.ok().build();
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
                    String newTaskId = taskRepository.save(new Task(articleId, userId, creatorId, comment, "PENDING")).getId();

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
                        String newTaskId = taskRepository.save(new Task(articleId, userId, nextUserId, comment, "PENDING")).getId();
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
