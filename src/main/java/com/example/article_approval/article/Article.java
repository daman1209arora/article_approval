package com.example.article_approval.article;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document("articles")
public class Article {
    @Id
    private String id;
    private String creatorId;
    private String workflowId;
    private String documentContent;
    private String latestTaskId;
    private String articleStatus;

    @Override
    public String toString() {
        return "ID:" + id.toString() + " CreatorId: " + creatorId + " WorkflowId: "
                + workflowId + " DocumentContent: " + documentContent + " LatestTaskId: " + latestTaskId
                + " ArticleStatus: " + articleStatus;
    }
}