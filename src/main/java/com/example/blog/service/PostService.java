package com.example.blog.service;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserAndPost;
import com.example.blog.entity.Post;
import com.example.blog.entity.Role;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post> getAllPosts();

    List<Post> getPagePosts(int page);

    List<Post> getPostsByCategory(String category);

    Optional<Post> getPost(Long id);

    Post savePost(Post post);

    Optional<Post> modifyPost(PostDto postDto);

    Optional<Post> createLike(UserAndPost userAndPost);

    Optional<Post> deleteLike(UserAndPost userAndPost);

    void deletePost(Long id);
}
