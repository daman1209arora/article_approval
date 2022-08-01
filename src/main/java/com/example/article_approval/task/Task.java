package com.example.article_approval.task;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document("tasks")
public class Task {
    @Id
    private String id;
    private String articleId;
    private String sentFromId;
    private String sentToId;
    private String comment;
    private String status;

    @Override
    public String toString() {
        return "ID:" + id.toString() + " ArticleId: " + articleId + " SentFromId: " + sentFromId + " SentToId: " + sentToId + " Comment: " + comment + " Status: " + status;
    }
}