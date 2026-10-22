package com.example.forum.service;

import com.example.forum.domain.Comment;
import com.example.forum.domain.CommentLike;
import com.example.forum.domain.Post;
import com.example.forum.domain.PostLike;
import com.example.forum.domain.PostLikeId;
import com.example.forum.domain.User;
import com.example.forum.dto.LikeResult;
import com.example.forum.exception.ResourceNotFoundException;
import com.example.forum.repository.CommentLikeRepository;
import com.example.forum.repository.CommentRepository;
import com.example.forum.repository.PostLikeRepository;
import com.example.forum.repository.PostRepository;
import com.example.forum.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class LikeService {

    private final PostLikeRepository postLikes;
    private final CommentLikeRepository commentLikes;
    private final PostRepository posts;
    private final CommentRepository comments;
    private final UserRepository users;

    public LikeService(PostLikeRepository postLikes,
                       CommentLikeRepository commentLikes,
                       PostRepository posts,
                       CommentRepository comments,
                       UserRepository users) {
        this.postLikes = postLikes;
        this.commentLikes = commentLikes;
        this.posts = posts;
        this.comments = comments;
        this.users = users;
    }

    public long countPostLikes(Long postId) {
        return postLikes.countByPostId(postId);
    }

    public boolean hasLikedPost(Long postId, Long userId) {
        return postLikes.existsById(new PostLikeId(postId, userId));
    }

    public Set<Long> likedCommentIds(Long userId, Collection<Long> commentIds) {
        if (commentIds.isEmpty()) {
            return Set.of();
        }
        return commentLikes.findLikedCommentIds(userId, commentIds);
    }

    @Transactional
    public LikeResult togglePostLike(Long postId, Long userId) {
        Post post = posts.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post " + postId + " not found"));
        User user = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " not found"));

        int deleted = postLikes.deleteByPostAndUser(postId, userId);
        boolean liked;
        if (deleted > 0) {
            liked = false;
        } else {
            postLikes.save(new PostLike(post, user));
            liked = true;
        }

        return new LikeResult(liked, postLikes.countByPostId(postId));
    }

    @Transactional
    public LikeResult toggleCommentLike(Long commentId, Long userId) {
        Comment comment = comments.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment " + commentId + " not found"));
        User user = users.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " not found"));

        int deleted = commentLikes.deleteByCommentAndUser(commentId, userId);
        boolean liked;
        if (deleted > 0) {
            liked = false;
        } else {
            commentLikes.save(new CommentLike(comment, user));
            liked = true;
        }

        return new LikeResult(liked, commentLikes.countByCommentId(commentId));
    }
}
