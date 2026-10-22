package com.example.forum.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "posts")
public class Post {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy").withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm").withZone(ZoneId.systemDefault());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected  Post(){
    }

    public Post(User author, String title, String body) {
        this.author = author;
        this.title = title;
        this.body = body;
    }

    @PrePersist
    void onCreate(){
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate(){
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public User getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAtDisplay() {
        return createdAt == null ? "" : DATE_FORMAT.format(createdAt);
    }

    public String getCreatedAtFullDisplay() {
        return createdAt == null ? "" : DATE_TIME_FORMAT.format(createdAt);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
