package com.example.blog.repository;

import com.example.blog.entity.ThumbsUp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThumbsUpRepository extends JpaRepository<ThumbsUp, Long> {
}
