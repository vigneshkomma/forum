package com.example.forum.repository;

import com.example.forum.domain.CommentLike;
import com.example.forum.domain.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {

    long countByCommentId(Long commentId);

    @Query("""
            select l.comment.id
            from CommentLike l
            where l.user.id = :userId
              and l.comment.id in :commentIds
            """)
    Set<Long> findLikedCommentIds(@Param("userId") Long userId,
                                  @Param("commentIds") Collection<Long> commentIds);

    @Modifying
    @Query("delete from CommentLike l where l.comment.id = :commentId and l.user.id = :userId")
    int deleteByCommentAndUser(@Param("commentId") Long commentId,
                               @Param("userId") Long userId);
}