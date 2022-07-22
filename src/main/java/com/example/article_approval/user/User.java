package com.example.article_approval.user;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("users")
public class User {
    @Id
    private String id;
    private String name;

    private String emailId;

    private Boolean canCreateDoc;

    private String role;

    public User(String name, String emailId, String role, Boolean canCreateDoc) {
        this.name = name;
        this.emailId = emailId;
        this.role = role;
        this.canCreateDoc = canCreateDoc;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmailId() { return emailId; }
    public String getRole() { return role;}
    public Boolean getCanCreateDoc() { return canCreateDoc; }
    @Override
    public String toString() {
        return "ID:" + id.toString() + " Name: " + name + " Email: " + emailId + " Role: " + role;
    }

}
