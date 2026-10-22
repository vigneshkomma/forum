package com.example.forum.service;

import com.example.forum.domain.Post;
import com.example.forum.domain.User;
import com.example.forum.dto.PostSummary;
import com.example.forum.exception.ResourceNotFoundException;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

    private static final int MAX_PAGE_SIZE = 50;

    private final PostRepository posts;
    private final UserRepository users;

    public PostService(PostRepository posts, UserRepository users) {
        this.posts = posts;
        this.users = users;
    }

    public Page<PostSummary> mainPage(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return posts.findMainPage(PageRequest.of(safePage, safeSize));
    }

    public Post getPost(Long postId) {
        return posts.findWithAuthorById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post " + postId + " not found"));
    }

    @Transactional
    public Post createPost(Long authorId, String title, String body) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be empty");
        }
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Body must not be empty");
        }

        User author = users.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + authorId + " not found"));

        return posts.save(new Post(author, title.trim(), body.trim()));
    }
}
