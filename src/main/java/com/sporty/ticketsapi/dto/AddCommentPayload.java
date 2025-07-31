package com.sporty.ticketsapi.dto;

import com.sporty.ticketsapi.entity.CommentVisibility;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCommentPayload {
    @NotEmpty
    private String authorId;
    @NotEmpty
    private String content;
    @NotNull
    private CommentVisibility visibility;
}
