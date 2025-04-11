package com.proyecto.blog.service;

import com.proyecto.blog.dto.PostDTOandNameAuthor;
import com.proyecto.blog.model.Post;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface IPostService {

    PostDTOandNameAuthor createPost(Post post);
    Optional<PostDTOandNameAuthor> getPostById(Long id);
    List<PostDTOandNameAuthor> getAllPosts();
    PostDTOandNameAuthor updatePost(Long id, Post postDetails, Authentication authentication);
    boolean deletePost(Long id);
}
