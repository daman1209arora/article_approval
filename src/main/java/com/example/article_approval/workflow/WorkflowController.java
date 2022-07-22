package com.example.article_approval.workflow;

import com.example.article_approval.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {
    @Autowired
    private WorkflowRepository workflowRepository;

    @GetMapping(value="/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Workflow findWorkflowByID(@PathVariable(value = "id") String id) {
        Optional<Workflow> workflow = workflowRepository.findWorkflowById(id);
        if(workflow.isPresent())
            return workflow.get();
        else
            return null;
    }

    @PostMapping
    public Workflow createWorkflow(@Validated @RequestBody Workflow workflow) {
        return workflowRepository.save(workflow);
    }
}
