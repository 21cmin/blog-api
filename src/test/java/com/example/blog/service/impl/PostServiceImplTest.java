package com.example.blog.service.impl;

import com.example.blog.constants.Roles;
import com.example.blog.entity.Post;
import com.example.blog.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PostServiceImplTest {
    @Autowired
    private PostService postService;

    @Test
    void post() {
        Roles user = Roles.ROLE_USER;
        System.out.printf("role %s\n", user);
    }
}