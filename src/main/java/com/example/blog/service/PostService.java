package com.example.blog.service;

import com.example.blog.entity.Post;
import com.example.blog.entity.Role;

import java.util.List;
import java.util.Optional;

public interface PostService {
    List<Post> getAllPosts();

    List<Post> getPagePosts(int page);

    Optional<Post> getPost(Long id);

    Post savePost(Post post);

    void deletePost(Long id);
}
