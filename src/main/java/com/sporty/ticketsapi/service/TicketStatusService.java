package com.sporty.ticketsapi.service;

import com.sporty.ticketsapi.entity.TicketStatus;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
public class TicketStatusService {

    public boolean canTransitionTo(TicketStatus currentStatus, TicketStatus nextStatus) {
        switch (currentStatus) {
            case OPEN:
                return EnumSet.of(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED, TicketStatus.CLOSED).contains(nextStatus);
            case IN_PROGRESS:
                return EnumSet.of(TicketStatus.RESOLVED, TicketStatus.CLOSED).contains(nextStatus);
            case RESOLVED:
                return EnumSet.of(TicketStatus.CLOSED).contains(nextStatus);
            case CLOSED:
                return false; // Cannot transition from closed
            default:
                return false;
        }
    }
}
