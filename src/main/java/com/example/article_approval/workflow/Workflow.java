package com.example.article_approval.workflow;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document("workflows")
public class Workflow {
    @Id
    private String id;
    private String workflowName;
    private List<String> members;

    public Workflow(String workflowName, List<String> members) {
        this.workflowName = workflowName;
        this.members = members;
    }

    @Override
    public String toString() {
        return "ID: " + id.toString() + " Workflow Name: " + workflowName + " Members: [" + String.join(",",members) + "]";
    }

    public String getId() { return id; }
    public String getWorkflowName() { return workflowName; }
    public List<String> getMembers() { return members; }
}
