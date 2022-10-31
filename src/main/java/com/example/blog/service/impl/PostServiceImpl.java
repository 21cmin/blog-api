package com.example.blog.service.impl;

import com.example.blog.dto.PostDto;
import com.example.blog.dto.UserAndPost;
import com.example.blog.entity.Post;
import com.example.blog.entity.ThumbsUp;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.ThumbsUpRepository;
import com.example.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final ThumbsUpRepository thumbsUpRepository;

    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public List<Post> getPagePosts(int page) {
        PageRequest request = PageRequest.of(page, 10, Sort.by("id").descending());
        return postRepository.findAll(request).getContent();
    }

    @Override
    public List<Post> getPostsByCategory(String category) {
        return postRepository.findPostsByCategory(category);
    }

    @Override
    public Optional<Post> getPost(Long id) {
        return postRepository.findById(id);
    }

    @Override
    public Post savePost(Post post) {
        return postRepository.save(post);
    }

    @Override
    public Optional<Post> modifyPost(PostDto postDto) {
        Optional<Post> findPost = postRepository.findById(postDto.getId());
        if (findPost.isEmpty()) return Optional.empty();
        Post post = findPost.get();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setImageUrl(postDto.getImageUrl());
        post.setCategory(postDto.getCategory());
        return Optional.of(post);
    }

    @Override
    public Optional<Post> createLike(UserAndPost userAndPost) {
        Optional<Post> findPost = postRepository.findById(userAndPost.getPostId());
        if (findPost.isEmpty()) return Optional.empty();
        Optional<ThumbsUp> findLIke = findPost.get().getLikes().stream().filter(like -> like.getUsername().equals(userAndPost.getUsername())).findAny();
        if (findLIke.isPresent()) return Optional.empty();
        findPost.get().getLikes().add(new ThumbsUp(null, userAndPost.getUsername()));
        return findPost;

    }

    @Override
    public Optional<Post> deleteLike(UserAndPost userAndPost) {
        Optional<Post> findPost = postRepository.findById(userAndPost.getPostId());
        if (findPost.isEmpty()) return Optional.empty();
        Optional<ThumbsUp> findLIke = findPost.get().getLikes().stream().filter(like -> like.getUsername().equals(userAndPost.getUsername())).findAny();
        if (findLIke.isEmpty()) return Optional.empty();
        findPost.get().setLikes(
                findPost.get().getLikes().stream().filter(like -> !like.getUsername().equals(userAndPost.getUsername())).collect(Collectors.toSet())
        );
        thumbsUpRepository.deleteById(findLIke.get().getId());
        return findPost;
    }


    @Override
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
