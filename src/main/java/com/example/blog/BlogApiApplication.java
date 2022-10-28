package com.example.blog;

import com.example.blog.entity.Member;
import com.example.blog.entity.Post;
import com.example.blog.entity.Role;
import com.example.blog.service.MemberService;
import com.example.blog.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@EnableJpaAuditing
@SpringBootApplication
public class BlogApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApiApplication.class, args);
    }

    @Bean
    CommandLineRunner runner(PostService postService, MemberService memberService) {
        return args -> {
            memberService.makeRole();
            Optional<Role> role = memberService.findRoleByName("ROLE_USER");
            Member jin = new Member(null, "jin", "1234", Set.of(role.get()));
            memberService.saveMember(jin);
            Member foundMember = memberService.findMemberByName("jin").get();
            for (int i = 0; i < 20; i++) {
                Post post = new Post(null, String.format("post %d", i), "description", "art", "url", LocalDateTime.now(), foundMember, new HashSet<>());
                postService.savePost(post);
            }
        };
    }

}
