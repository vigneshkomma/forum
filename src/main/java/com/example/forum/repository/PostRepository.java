package com.example.forum.repository;

import com.example.forum.domain.Post;
import com.example.forum.dto.PostSummary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(value = """
            select new com.example.forum.dto.PostSummary(
                p.id,
                p.title,
                p.body,
                p.author.username,
                p.createdAt,
                (select count(c) from Comment c where c.post = p),
                (select count(l) from PostLike l where l.post = p)
            )
            from Post p
            order by p.createdAt desc, p.id desc
            """,
            countQuery = "select count(p) from Post p")
    Page<PostSummary> findMainPage(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("select p from Post p where p.id = :id")
    Optional<Post> findWithAuthorById(@Param("id") Long id);

    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
}