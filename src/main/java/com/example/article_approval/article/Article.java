package com.example.article_approval.article;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("articles")
public class Article {
    @Id
    private String id;
    private String creatorId;
    private String workflowId;
    private String documentContent;
    private String latestTaskId;

    private String articleStatus;

    public Article(String creatorId, String workflowId, String documentContent, String latestTaskId, String articleStatus) {
        super();
        this.creatorId = creatorId;
        this.workflowId = workflowId;
        this.documentContent = documentContent;
        this.latestTaskId = latestTaskId;
        this.articleStatus = articleStatus;
    }
    @Override
    public String toString() {
        return "ID:" + id.toString() + " CreatorId: " + creatorId + " WorkflowId: "
                + workflowId + " DocumentContent: " + documentContent + " LatestTaskId: " + latestTaskId
                + " ArticleStatus: " + articleStatus;
    }

    public String getId() { return id; }
    public String getCreatorId() { return creatorId; }
    public String getWorkflowId() { return workflowId; }
    public String getDocumentContent() { return documentContent;}
    public String getLatestTaskId() { return latestTaskId;}
    public String getArticleStatus() { return articleStatus;}


    public void setId(String id) { this.id = id; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public void setWorkflowId(String workflowId) { this.workflowId = workflowId; }
    public void setDocumentContent(String documentContent) { this.documentContent = documentContent; }
    public void setLatestTaskId(String latestTaskId) { this.latestTaskId = latestTaskId; }
    public void setArticleStatus(String articleStatus) { this.articleStatus = articleStatus; }
}