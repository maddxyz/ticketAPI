package com.sporty.ticketsapi.entity;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class Ticket {
    private UUID ticketId;
    private String subject;
    private String description;
    private TicketStatus status;
    private String userId;
    private String assigneeId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Ticket() {
        this.ticketId = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
