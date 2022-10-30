package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostDto {
    private Long id;
    private String title;
    private String description;
    private String imageUrl;
    private String category;
    private String username;
    private String createdAt;
    private List<String> likes;
}
