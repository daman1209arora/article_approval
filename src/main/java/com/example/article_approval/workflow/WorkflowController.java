package com.example.article_approval.workflow;

import com.example.article_approval.user.User;
import com.example.article_approval.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.awt.*;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {
    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Workflow findWorkflowByID(@PathVariable(value = "id") String id) {
        try {
            return workflowRepository.findWorkflowById(id).get();
        } catch(NoSuchElementException exception) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Workflow with this ID doesn't exist!",
                    exception
            );
        }
    }

    @PostMapping
    public Workflow createWorkflow(@Validated @RequestBody Workflow workflow) {
        // Check if all members in the workflow exist.
        boolean allPresent = true;
        String notFoundId = "";
        for(int i = 0; i < workflow.getMembers().size() && allPresent; i++) {
            String userId = workflow.getMembers().get(i);
            boolean userFound = userRepository.existsById(userId);
            if(!userFound) {
                notFoundId = userId;
            }
            allPresent = allPresent & userFound;
        }

        if(!allPresent) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User with ID "+ notFoundId + " doesn't exist!"
            );
        }
        return workflowRepository.save(workflow);
    }
}
