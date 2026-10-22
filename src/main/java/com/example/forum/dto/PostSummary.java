package com.example.forum.dto;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public record PostSummary(
        Long id,
        String title,
        String body,
        String authorUsername,
        Instant createdAt,
        Long commentCount,
        Long likeCount
) {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("MMM d, yyyy").withZone(ZoneId.systemDefault());

    public String getCreatedAtDisplay() {
        return createdAt == null ? "" : DATE_FORMAT.format(createdAt);
    }
}
