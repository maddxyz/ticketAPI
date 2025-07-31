package com.sporty.ticketsapi.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateTicketPayload {
    @NotEmpty
    private String userId;
    @NotEmpty
    private String subject;
    @NotEmpty
    private String description;
}
