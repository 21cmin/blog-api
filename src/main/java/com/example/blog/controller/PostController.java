package com.example.blog.controller;

import com.example.blog.dto.PostDto;
import com.example.blog.entity.Member;
import com.example.blog.entity.Post;
import com.example.blog.entity.ThumbsUp;
import com.example.blog.service.MemberService;
import com.example.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {
    private final PostService postService;
    private final MemberService memberService;
    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        return ResponseEntity.ok().body(posts);
    }

    @GetMapping("/page")
    public ResponseEntity<List<PostDto>> getPagePosts(@RequestParam int page) {
        List<Post> posts = postService.getPagePosts(page);
        List<PostDto> postDto = createPostDtoList(posts);
        return ResponseEntity.ok().body(postDto);
    }

    @PostMapping
    public ResponseEntity<?> savePost(@RequestBody PostDto postDto) {
        Optional<Post> newPost = createPostFromPostDto(postDto);
        if (newPost.isEmpty()) return ResponseEntity.status(BAD_REQUEST).build();
        postService.savePost(newPost.get());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<?> updatePost(@RequestBody PostDto postDto) {
        Optional<Post> findPost = postService.getPost(postDto.getId());
        if (findPost.isEmpty()) return ResponseEntity.status(BAD_REQUEST).build();
        Post post = findPost.get();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setImageUrl(postDto.getImageUrl());
        post.setCategory(postDto.getCategory());
        return ResponseEntity.status(ACCEPTED).build();
    }

    private Optional<Post> createPostFromPostDto(PostDto postDto) {
        Optional<Member> member = memberService.findMemberByName(postDto.getUsername());
        if (member.isEmpty()) return Optional.empty();
        return Optional.of(new Post(
                null,
                postDto.getTitle(),
                postDto.getDescription(),
                postDto.getCategory(),
                postDto.getImageUrl(),
                null,
                member.get(),
                new HashSet<>()
        ));
    }

    private List<PostDto> createPostDtoList(List<Post> postList) {
        return postList.stream().map(post -> new PostDto(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getImageUrl(),
                post.getCategory(),
                post.getMember().getUsername(),
                post.getCreatedAt().toString(),
                post.getLikes().stream().map(ThumbsUp::getUsername).collect(Collectors.toList())
        )).collect(Collectors.toList());
    }
}
