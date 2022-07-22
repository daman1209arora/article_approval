package com.example.article_approval.task;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("tasks")
public class Task {
    @Id
    private String id;
    private String articleId;
    private String sentFromId;
    private String sentToId;
    private String comment;
    private String status;

    public Task(String articleId, String sentFromId, String sentToId, String comment, String status) {
        this.articleId = articleId;
        this.sentFromId = sentFromId;
        this.sentToId = sentToId;
        this.comment = comment;
        this.status = status;
    }
    @Override
    public String toString() {
        return "ID:" + id.toString() + " ArticleId: " + articleId + " SentFromId: " + sentFromId + " SentToId: " + sentToId + " Comment: " + comment + " Status: " + status;
    }

    public String getId() { return id; }
    public String getArticleId() { return articleId; }
    public String getSentFromId() { return sentFromId; }
    public String getSentToId() { return sentToId;}
    public String getComment() { return comment; }
    public String getStatus() { return status; }

    public void setId(String id) { this.id = id; }
    public void setArticleId(String articleId) { this.articleId = articleId; }
    public void setSentFromId(String sentFromId) { this.sentFromId = sentFromId; }
    public void setSentToId(String sentToId) { this.sentToId = sentToId; }
    public void setComment(String comment) { this.comment = comment; }
    public void setStatus(String status) { this.status = status; }
}