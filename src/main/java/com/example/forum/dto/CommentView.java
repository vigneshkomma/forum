package com.example.forum.dto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record CommentView(
        Long id,
        String body,
        String authorUsername,
        Instant createdAt,
        Long likeCount
) {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy 'at' HH:mm").withZone(ZoneId.systemDefault());

    public String getCreatedAtDisplay() {
        return createdAt == null ? "" : DATE_TIME_FORMAT.format(createdAt);
    }
}
