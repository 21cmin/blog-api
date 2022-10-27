package com.example.blog;

import com.example.blog.entity.Post;
import com.example.blog.service.MemberService;
import com.example.blog.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@Slf4j
@SpringBootApplication
public class BlogApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApiApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(PostService postService, MemberService memberService) {
        return args -> {
            memberService.makeRole();
            for (int i = 0; i < 20; i++) {
                Post post = new Post(null, String.format("post %d", i), "description", "art");
                postService.savePost(post);
            }
        };
    }

}
