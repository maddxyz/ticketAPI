package com.sporty.ticketsapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;
import com.sporty.ticketsapi.dto.CreateTicketPayload;
import com.sporty.ticketsapi.dto.CreateTicketResponse;
import com.sporty.ticketsapi.dto.UpdateTicketStatusResponse;
import com.sporty.ticketsapi.entity.Ticket;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);
    @Mapping(target = "ticketId", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "assigneeId", ignore = true)

    Ticket createTicketPayloadToTicket(CreateTicketPayload createTicketDto);
    CreateTicketResponse ticketToCreateTicketResponse(Ticket ticket);
    UpdateTicketStatusResponse ticketToUpdateTicketStatusResponse(Ticket updatedTicket);

}