package com.example.forum.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "post_likes")
public class PostLike {

    @EmbeddedId
    private PostLikeId id;

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected PostLike() {
    }

    public PostLike(Post post, User user) {
        this.post = post;
        this.user = user;
        this.id = new PostLikeId(post.getId(), user.getId());
    }

    @PrePersist
    void onCreate(){
        this.createdAt = Instant.now();
    }

    public PostLikeId getId() {
        return id;
    }

    public Post getPost() {
        return post;
    }

    public User getUser() {
        return user;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
