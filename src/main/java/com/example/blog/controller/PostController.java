package com.example.blog.controller;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserAndPost;
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

    @GetMapping("{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId) {
        Optional<Post> findPost = postService.getPost(postId);
        if (findPost.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(createPostDtoFromPost(findPost.get()));
    }

    @GetMapping("/category")
    public ResponseEntity<List<PostDto>> getPostsByCategory(@RequestParam String cat) {
        List<Post> postsByCategory = postService.getPostsByCategory(cat);
        return ResponseEntity.ok().body(createPostDtoList(postsByCategory));
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
        Optional<Post> optionalPost = postService.modifyPost(postDto);
        if (optionalPost.isEmpty()) return ResponseEntity.notFound().build();
        return ResponseEntity.status(ACCEPTED).build();
    }

    @DeleteMapping("{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        log.error("get delete request {}", postId);
        postService.deletePost(postId);
        return ResponseEntity.status(ACCEPTED).build();
    }

    @PutMapping("/like/create")
    public ResponseEntity<PostDto> createLike(@RequestBody UserAndPost userAndPost) {
        Optional<Post> post = postService.createLike(userAndPost);
        if (post.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().body(createPostDtoFromPost(post.get()));
    }

    @PutMapping("/like/delete")
    public ResponseEntity<PostDto> deleteLike(@RequestBody UserAndPost userAndPost) {
        Optional<Post> post = postService.deleteLike(userAndPost);
        if (post.isEmpty()) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().body(createPostDtoFromPost(post.get()));
    }


    private Optional<Post> createPostFromPostDto(PostDto postDto) {
        Optional<Member> member = memberService.findMemberByName(postDto.getUsername());
        if (member.isEmpty()) return Optional.empty();
        return Optional.of(new Post(
                null,
                postDto.getTitle(),
                postDto.getDescription(),
                postDto.getImageUrl(),
                postDto.getCategory(),
                null,
                member.get(),
                new HashSet<>()
        ));
    }

    private PostDto createPostDtoFromPost(Post post) {
        return new PostDto(
                post.getId(),
                post.getTitle(),
                post.getDescription(),
                post.getImageUrl(),
                post.getCategory(),
                post.getMember().getUsername(),
                post.getCreatedAt().toString(),
                post.getLikes().stream().map(ThumbsUp::getUsername).collect(Collectors.toList())
        );
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
