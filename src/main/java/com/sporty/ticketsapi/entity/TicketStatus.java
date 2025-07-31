package com.sporty.ticketsapi.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TicketStatus {
    OPEN,
    IN_PROGRESS,
    RESOLVED,
    CLOSED;

    @JsonCreator
    public static TicketStatus fromString(String value) {
        return TicketStatus.valueOf(value.toUpperCase());
    }

    
}
