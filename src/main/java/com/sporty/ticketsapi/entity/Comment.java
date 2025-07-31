package com.sporty.ticketsapi.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Comment {
    private UUID commentId;
    private UUID ticketId;
    private String authorId;
    private String content;
    private CommentVisibility visibility;
    private LocalDateTime createdAt;

    public Comment() {
        this.commentId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }
}
