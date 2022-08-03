package com.example.article_approval.workflow;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document("workflows")
public class Workflow {
    @Id
    private String id;
    private String workflowName;
    private List<String> members;
}