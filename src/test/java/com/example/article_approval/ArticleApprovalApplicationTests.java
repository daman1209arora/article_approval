package com.example.article_approval;

import com.example.article_approval.user.User;
import com.example.article_approval.user.UserController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ArticleApprovalApplicationTests {

    @Autowired
    private UserController userController;

    @Test
    void contextLoads() {
    }

}
