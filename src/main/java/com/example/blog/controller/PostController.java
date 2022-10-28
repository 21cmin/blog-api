package com.example.blog.controller;

import com.example.blog.dto.PostDto;
import com.example.blog.entity.Post;
import com.example.blog.entity.ThumbsUp;
import com.example.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok().body(posts);
    }

    @GetMapping("/page")
    public ResponseEntity<List<PostDto>> getPagePosts(@RequestParam int page) {
        List<Post> posts = postService.getPagePosts(page);
        List<PostDto> postDto = createPostDto(posts);
        return ResponseEntity.ok().body(postDto);
    }

    @PostMapping
    public ResponseEntity<?> savePost(@RequestBody Post post) {
        postService.savePost(post);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private List<PostDto> createPostDto(List<Post> postList) {
        return postList.stream().map(post -> new PostDto(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getImageUrl(),
                post.getCategory(),
                post.getMember().getUsername(),
                post.getCreatedAt().toString(),
                post.getLikes().stream().map(ThumbsUp::getUserId).collect(Collectors.toList())
        )).collect(Collectors.toList());
    }
}
