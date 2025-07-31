package com.sporty.ticketsapi.dto;

import com.sporty.ticketsapi.entity.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTicketStatusPayload {
    @NotNull
    private TicketStatus status;
}
