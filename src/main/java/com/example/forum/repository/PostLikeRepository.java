package com.example.forum.repository;

import com.example.forum.domain.PostLike;
import com.example.forum.domain.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Set;

public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {

    long countByPostId(Long postId);

    @Query("""
            select l.post.id
            from PostLike l
            where l.user.id = :userId
              and l.post.id in :postIds
            """)
    Set<Long> findLikedPostIds(@Param("userId") Long userId,
                               @Param("postIds") Collection<Long> postIds);

    @Modifying
    @Query("delete from PostLike l where l.post.id = :postId and l.user.id = :userId")
    int deleteByPostAndUser(@Param("postId") Long postId,
                            @Param("userId") Long userId);
}