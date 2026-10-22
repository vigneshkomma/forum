package com.example.forum.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CommentLikeId implements Serializable {

    @Column(name = "comment_id")
    private Long commentId;

    @Column(name = "user_id")
    private Long userId;

    protected CommentLikeId() {
    }

    public CommentLikeId(Long commentId, Long userId) {
        this.commentId = commentId;
        this.userId = userId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public Long getUserId() {
        return userId;
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(!(o instanceof CommentLikeId other)) return false;
        return Objects.equals(commentId, other.commentId) && Objects.equals(userId, other.userId);
    }

    @Override
    public int hashCode(){
        return Objects.hash(commentId, userId);
    }
}
