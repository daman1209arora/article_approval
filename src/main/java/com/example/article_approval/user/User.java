package com.example.article_approval.user;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Document("users")
public class User {
    @Id
    private String id;

    private String name;
    @NotEmpty(message = "Email can't be empty!")
    @Email(message =  "Email is improper!")
    private String emailId;

    private Boolean canCreateDoc;
    private String role;

//    @Override
//    public String toString() {
//        return "ID:" + id.toString() + " Name: " + name + " Email: " + emailId + " Role: " + role;
//    }

}
