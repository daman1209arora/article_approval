package com.example.article_approval;

import com.example.article_approval.article.Article;
import com.example.article_approval.article.ArticleController;
import com.example.article_approval.article.ArticleRepository;
import com.example.article_approval.task.Task;
import com.example.article_approval.task.TaskRepository;
import com.example.article_approval.user.User;
import com.example.article_approval.user.UserRepository;
import com.example.article_approval.workflow.Workflow;
import com.example.article_approval.workflow.WorkflowRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ArticleControllerTests {
    private MockMvc mockMvc;
    private ArticleRepository articleRepository = mock(ArticleRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private WorkflowRepository workflowRepository = mock(WorkflowRepository.class);

    private TaskRepository taskRepository = mock(TaskRepository.class);
    @InjectMocks
    private ArticleController articleController;


    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(articleController).build();
    }

    @Test
    public void findArticleById_success() throws Exception {
        Article article1 = new Article("article1Id", "creator1Id", "workflow1Id", "documentContent1", "latestTaskId1", "PENDING");
        Mockito.when(articleRepository.findArticleById(article1.getId())).thenReturn(Optional.ofNullable(article1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/article/"+article1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(article1.getId())))
                .andExpect(jsonPath("$.creatorId", is(article1.getCreatorId())))
                .andExpect(jsonPath("$.workflowId", is(article1.getWorkflowId())))
                .andExpect(jsonPath("$.documentContent", is(article1.getDocumentContent())))
                .andExpect(jsonPath("$.latestTaskId", is(article1.getLatestTaskId())))
                .andExpect(jsonPath("$.articleStatus", is(article1.getArticleStatus())));
    }
    @Test
    public void findArticleById_failure() throws Exception {
        String unknownId = "unknownId";
        Mockito.when(articleRepository.findArticleById(unknownId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/article/"+unknownId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createArticle_success() throws Exception {
        Article article = new Article("article1Id", "creator1Id", "workflow1Id", "content1", "latestTask1Id", "PENDING");
        Workflow workflow = new Workflow(article.getWorkflowId(), "workflowName", new ArrayList<>());
        User user = new User(article.getCreatorId(), "name1", "user1@gmail.com", true, "role1");
        Mockito.when(workflowRepository.findWorkflowById(article.getWorkflowId())).thenReturn(Optional.of(workflow));
        Mockito.when(userRepository.findUserById(article.getCreatorId())).thenReturn(Optional.of(user));
        Mockito.when(articleRepository.save(article)).thenReturn(article);

        String content = objectWriter.writeValueAsString(article);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/article/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(article.getId())))
                .andExpect(jsonPath("$.creatorId", is(article.getCreatorId())))
                .andExpect(jsonPath("$.documentContent", is(article.getDocumentContent())));
    }

    @Test
    public void createArticle_withNonExistingCreator_failure() throws Exception {
        Article article = new Article("article1Id", "creator1Id", "workflow1Id", "content1", "latestTask1Id", "PENDING");
        Workflow workflow = new Workflow(article.getWorkflowId(), "workflowName", new ArrayList<>());
        Mockito.when(workflowRepository.findWorkflowById(article.getWorkflowId())).thenReturn(Optional.of(workflow));
        Mockito.when(userRepository.findUserById(article.getCreatorId())).thenReturn(Optional.empty());
        Mockito.when(articleRepository.save(article)).thenReturn(article);

        String content = objectWriter.writeValueAsString(article);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/article/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createArticle_withCreatorNotHavingPermission_failure() throws Exception {
        Article article = new Article("article1Id", "creator1Id", "workflow1Id", "content1", "latestTask1Id", "PENDING");
        Workflow workflow = new Workflow(article.getWorkflowId(), "workflowName", new ArrayList<>());
        User user = new User(article.getCreatorId(), "name1", "user1@gmail.com", false, "role1");
        Mockito.when(workflowRepository.findWorkflowById(article.getWorkflowId())).thenReturn(Optional.of(workflow));
        Mockito.when(userRepository.findUserById(article.getCreatorId())).thenReturn(Optional.of(user));
        Mockito.when(articleRepository.save(article)).thenReturn(article);

        String content = objectWriter.writeValueAsString(article);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/article/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }



    @Test
    public void initiateArticleApproval_success() throws Exception {
        User user1 = new User("user1Id", "username1", "user1@gmail.com", true, "user1Role");
        User user2 = new User("user2Id", "username2", "daman1209arora@gmail.com", true, "user2Role");
        User user3 = new User("user3Id", "username3", "user3@gmail.com", true, "user3Role");
        List<String> workflowUsers = new ArrayList<>(Arrays.asList(user2.getId(), user3.getId()));
        Workflow workflow = new Workflow("workflow1Id", "workflowName", workflowUsers);
        Article article = new Article("article1Id", user1.getId(), workflow.getId(), "content1", "", "PENDING");
        Task taskToSave = new Task(null, article.getId(), user1.getId(), user2.getId(), "", "PENDING");
        Task task = new Task("task1Id", article.getId(), user1.getId(), user2.getId(), "", "PENDING");
        Article modifiedArticle = new Article("article1Id", user1.getId(), workflow.getId(), "content1", task.getId(), "PENDING");

        User user = new User(article.getCreatorId(), "name1", "user1@gmail.com", false, "role1");
        Map<String, String> body = new HashMap<>();
        body.put("articleId", article.getId());
        body.put("userId", user.getId());
        Mockito.when(articleRepository.findArticleById(article.getId())).thenReturn(Optional.of(article));
        Mockito.when(workflowRepository.findWorkflowById(workflow.getId())).thenReturn(Optional.of(workflow));
        Mockito.when(taskRepository.save(taskToSave)).thenReturn(task);
        Mockito.when(articleRepository.save(modifiedArticle)).thenReturn(modifiedArticle);
        Mockito.when(userRepository.findUserById(user2.getId())).thenReturn(Optional.of(user2));

        String content = objectWriter.writeValueAsString(body);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/article/initiate/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk());
    }

    @Test
    public void initiateArticleApproval_withArticleNotExisting_failure() throws Exception {
        String fakeArticleId = "fakeArticleId";
        String fakeUserId = "fakeUserId";
        Map<String, String> body = new HashMap<>();
        body.put("articleId", fakeArticleId);
        body.put("userId", fakeUserId);
        Mockito.when(articleRepository.findArticleById(fakeArticleId)).thenReturn(Optional.empty());
        String content = objectWriter.writeValueAsString(body);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/article/initiate/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }


    @Test
    public void finishTask_invalidArticle_failure() throws Exception {
        String fakeArticleId = "fakeArticleId";
        String fakeUserId = "fakeUserId";
        Map<String, String> body = new HashMap<>();
        body.put("articleId", fakeArticleId);
        body.put("userId", fakeUserId);
        Mockito.when(articleRepository.findArticleById(fakeArticleId)).thenReturn(Optional.empty());
        String content = objectWriter.writeValueAsString(body);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/article/finish/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }
}
