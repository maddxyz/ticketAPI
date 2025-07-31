package com.sporty.ticketsapi.controller;

import com.sporty.ticketsapi.dto.AddCommentPayload;
import com.sporty.ticketsapi.dto.CreateTicketPayload;
import com.sporty.ticketsapi.dto.CreateTicketResponse;
import com.sporty.ticketsapi.dto.AddCommentResponse;
import com.sporty.ticketsapi.dto.UpdateTicketStatusPayload;
import com.sporty.ticketsapi.dto.UpdateTicketStatusResponse;
import com.sporty.ticketsapi.entity.Comment;
import com.sporty.ticketsapi.entity.Ticket;
import com.sporty.ticketsapi.entity.TicketStatus;
import com.sporty.ticketsapi.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<CreateTicketResponse> createTicket(@Valid @RequestBody CreateTicketPayload createTicketDto) {
        CreateTicketResponse response = ticketService.createTicket(createTicketDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Ticket>> getTickets(@RequestParam(required = false) TicketStatus status,
                                   @RequestParam(required = false) String userId,
                                   @RequestParam(required = false) String assigneeId) {
        List<Ticket> tickets = ticketService.findAllTickets(status, userId, assigneeId);
        return new ResponseEntity<>(tickets, HttpStatus.OK);
    }

    @PatchMapping("/{ticketId}/status")
    public ResponseEntity<UpdateTicketStatusResponse> updateTicketStatus(@PathVariable UUID ticketId, @Valid @RequestBody UpdateTicketStatusPayload updateTicketStatusDto) {
        UpdateTicketStatusResponse response = ticketService.updateTicketStatus(ticketId, updateTicketStatusDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<AddCommentResponse> addComment(@PathVariable UUID ticketId, @Valid @RequestBody AddCommentPayload addCommentDto) {
        AddCommentResponse response = ticketService.addComment(ticketId, addCommentDto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{ticketId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable UUID ticketId, @RequestParam(defaultValue = "user") String userType) {
        List<Comment> comments = ticketService.getComments(ticketId, userType);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}
