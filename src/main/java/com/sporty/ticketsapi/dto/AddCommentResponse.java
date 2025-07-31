package com.sporty.ticketsapi.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class AddCommentResponse {
    private UUID commentId;
    private String visibility;
    private LocalDateTime createdAt;
}
