package com.example.article_approval;

import com.example.article_approval.article.Article;
import com.example.article_approval.article.ArticleController;
import com.example.article_approval.article.ArticleRepository;
import com.example.article_approval.task.TaskRepository;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.example.article_approval.user.User;
import com.example.article_approval.user.UserController;
import com.example.article_approval.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTests {
    private MockMvc mockMvc;
    private UserRepository userRepository = mock(UserRepository.class);
    private ArticleRepository articleRepository = mock(ArticleRepository.class);
    private TaskRepository taskRepository = mock(TaskRepository.class);

    @InjectMocks
    private UserController userController;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void findUserById_success() throws Exception {
        User user1 = new User("user1Id", "username1", "user1@gmail.com", true, "user1Role");
        Mockito.when(userRepository.findUserById(user1.getId())).thenReturn(Optional.ofNullable(user1));

        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/user/"+user1.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(user1.getId())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.emailId", is(user1.getEmailId())))
                .andExpect(jsonPath("$.canCreateDoc", is(user1.getCanCreateDoc())))
                .andExpect(jsonPath("$.role", is(user1.getRole())));
    }
    @Test
    public void findUserById_failure() throws Exception {
        String unknownId = "unknownId";
        Mockito.when(userRepository.findUserById(unknownId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user/"+unknownId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void createUser_success() throws Exception {
        User user1 = new User("user1Id", "username1", "user1@gmail.com", true, "user1Role");
        Mockito.when(userRepository.save(user1)).thenReturn(user1);

        String content = objectWriter.writeValueAsString(user1);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                                        .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(user1.getId())))
                .andExpect(jsonPath("$.name", is(user1.getName())))
                .andExpect(jsonPath("$.emailId", is(user1.getEmailId())))
                .andExpect(jsonPath("$.canCreateDoc", is(user1.getCanCreateDoc())))
                .andExpect(jsonPath("$.role", is(user1.getRole())));
    }

    @Test
    public void createUser_failure() throws Exception {
        User user1 = new User("user1Id", "username1", "", true, "user1Role");
        Mockito.when(userRepository.save(user1)).thenReturn(user1);

        String content = objectWriter.writeValueAsString(user1);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/user/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

    @Test
    public void findAllArticlesCreatedByUser_success() throws Exception {
        User user1 = new User("user1Id", "username1", "user1@gmail.com", true, "user1Role");
        Article article1 = new Article("article1Id", user1.getId(), "workflow1Id", "documentContent1", "latestTaskId1", "PENDING");
        Article article2 = new Article("article2Id", user1.getId(), "workflow2Id", "documentContent2", "latestTaskId2", "PENDING");
        Article article3 = new Article("article3Id", user1.getId(), "workflow3Id", "documentContent3", "latestTaskId3", "APPROVED");

        List<Article> articleList = new ArrayList<>(Arrays.asList(article1, article2, article3));
        Mockito.when(articleRepository.findArticlesByCreatorId(user1.getId())).thenReturn(Optional.of(articleList));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user/articles/created/"+user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$[0].id", is(article1.getId())))
                .andExpect(jsonPath("$[1].workflowId", is(article2.getWorkflowId())))
                .andExpect(jsonPath("$[2].documentContent", is(article3.getDocumentContent())));
    }


    @Test
    public void findAllArticlesToApprove_success() throws Exception {
        User user1 = new User("user1Id", "username1", "user1@gmail.com", true, "user1Role");
        Article article1 = new Article("article1Id", user1.getId(), "workflow1Id", "documentContent1", "latestTaskId1", "PENDING");
        Article article2 = new Article("article2Id", user1.getId(), "workflow2Id", "documentContent2", "latestTaskId2", "PENDING");
        Article article3 = new Article("article3Id", user1.getId(), "workflow3Id", "documentContent3", "latestTaskId3", "APPROVED");

        List<Object> articleList = new ArrayList<>(Arrays.asList(article1, article2, article3));
        Mockito.when(taskRepository.findTasksBySentToIdAndStatus(user1.getId(), "PENDING")).thenReturn(articleList);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user/articles/toApprove/"+user1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$[0].id", is(article1.getId())))
                .andExpect(jsonPath("$[1].workflowId", is(article2.getWorkflowId())))
                .andExpect(jsonPath("$[2].documentContent", is(article3.getDocumentContent())));
    }
}
