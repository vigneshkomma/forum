package com.example.forum.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "comment_likes")
public class CommentLike {

    @EmbeddedId
    private CommentLikeId id;

    @MapsId("commentId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected CommentLike() {
    }

    public CommentLike(Comment comment, User user) {
        this.comment = comment;
        this.user = user;
        this.id = new CommentLikeId(comment.getId(), user.getId());
    }

    @PrePersist
    void onCreate() {
        this.createdAt = Instant.now();
    }

    public CommentLikeId getId() {
        return id;
    }

    public Comment getComment() {
        return comment;
    }

    public User getUser() {
        return user;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}