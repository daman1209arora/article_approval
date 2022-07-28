package com.example.article_approval;

import com.example.article_approval.user.User;
import com.example.article_approval.user.UserRepository;
import com.example.article_approval.workflow.Workflow;
import com.example.article_approval.workflow.WorkflowController;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class WorkflowControllerTests {
    private MockMvc mockMvc;
    private WorkflowRepository workflowRepository = mock(WorkflowRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    @InjectMocks
    private WorkflowController workflowController;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(workflowController).build();
    }

    @Test
    public void findWorkflowById_success() throws Exception {
        String[] members = new String[] {"memberId1", "memberId2"};
        Workflow workflow1 = new Workflow("workflow1Id", "workflow1Name", new ArrayList<>(Arrays.asList(members)));
        Mockito.when(workflowRepository.findWorkflowById(workflow1.getId())).thenReturn(Optional.ofNullable(workflow1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/workflow/"+workflow1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(workflow1.getId())))
                .andExpect(jsonPath("$.workflowName", is(workflow1.getWorkflowName())))
                .andExpect(jsonPath("$.members[0]", is(workflow1.getMembers().get(0))))
                .andExpect(jsonPath("$.members[1]", is(workflow1.getMembers().get(1))));
    }
    @Test
    public void findWorkflowById_failure() throws Exception {
        String unknownId = "unknownId";
        Mockito.when(workflowRepository.findWorkflowById(unknownId)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/workflow/"+unknownId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createWorkflow_success() throws Exception {
        User user1 = new User("user1Id", "username1", "user1@gmail.com", true, "user1Role");
        User user2 = new User("user2Id", "username2", "user2@gmail.com", true, "user2Role");
        List<String> userIdList = new ArrayList<>(Arrays.asList(user1.getId(), user2.getId()));
        Workflow workflow1 = new Workflow("workflow1Id", "workflowname1", userIdList);
        Mockito.when(userRepository.existsById(user1.getId())).thenReturn(true);
        Mockito.when(userRepository.existsById(user2.getId())).thenReturn(true);
        Mockito.when(workflowRepository.save(workflow1)).thenReturn(workflow1);

        String content = objectWriter.writeValueAsString(workflow1);
        System.out.println("CONTENT: " + content);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/workflow/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(workflow1.getId())))
                .andExpect(jsonPath("$.workflowName", is(workflow1.getWorkflowName())))
                .andExpect(jsonPath("$.members", is(workflow1.getMembers())));
    }

    @Test
    public void createWorkflow_failure() throws Exception {
        User user1 = new User("user1Id", "username1", "user1@gmail.com", true, "user1Role");
        User user2 = new User("user2Id", "username2", "user2@gmail.com", true, "user2Role");
        List<String> userIdList = new ArrayList<>(Arrays.asList(user1.getId(), user2.getId()));
        Workflow workflow1 = new Workflow("workflow1Id", "workflowname1", userIdList);
        Mockito.when(userRepository.existsById(user1.getId())).thenReturn(true);
        Mockito.when(userRepository.existsById(user2.getId())).thenReturn(false);
        Mockito.when(workflowRepository.save(workflow1)).thenReturn(workflow1);

        String content = objectWriter.writeValueAsString(workflow1);
        System.out.println("CONTENT: " + content);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/api/workflow/")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(content);

        mockMvc.perform(mockRequest)
                .andExpect(status().isBadRequest());
    }

}
