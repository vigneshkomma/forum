package com.example.forum.service;

import com.example.forum.domain.Comment;
import com.example.forum.domain.Post;
import com.example.forum.domain.User;
import com.example.forum.dto.CommentView;
import com.example.forum.exception.ResourceNotFoundException;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository comments;
    private final PostRepository posts;
    private final UserRepository users;

    public CommentService(CommentRepository comments,
                          PostRepository posts,
                          UserRepository users) {
        this.comments = comments;
        this.posts = posts;
        this.users = users;
    }

    public List<CommentView> commentsFor(Long postId) {
        return comments.findForPost(postId);
    }

    @Transactional
    public Comment addComment(Long postId, Long authorId, String body) {
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Comment must not be empty");
        }

        Post post = posts.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post " + postId + " not found"));
        User author = users.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + authorId + " not found"));

        return comments.save(new Comment(post, author, body.trim()));
    }
}
