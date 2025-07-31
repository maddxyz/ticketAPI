package com.sporty.ticketsapi.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class UpdateTicketStatusResponse {
    private UUID ticketId;
    private String status;
    private LocalDateTime updatedAt;
}
