package com.example.forum.repository;

import com.example.forum.domain.Comment;
import com.example.forum.dto.CommentView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            select new com.example.forum.dto.CommentView(
                c.id,
                c.body,
                c.author.username,
                c.createdAt,
                (select count(cl) from CommentLike cl where cl.comment = c)
            )
            from Comment c
            where c.post.id = :postId
            order by c.createdAt asc, c.id asc
            """)
    List<CommentView> findForPost(@Param("postId") Long postId);

    long countByPostId(Long postId);
}