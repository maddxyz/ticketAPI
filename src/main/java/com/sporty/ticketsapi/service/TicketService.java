package com.sporty.ticketsapi.service;

import com.sporty.ticketsapi.dto.AddCommentPayload;
import com.sporty.ticketsapi.dto.AddCommentResponse;
import com.sporty.ticketsapi.dto.CreateTicketPayload;
import com.sporty.ticketsapi.dto.CreateTicketResponse;
import com.sporty.ticketsapi.dto.UpdateTicketStatusPayload;
import com.sporty.ticketsapi.dto.UpdateTicketStatusResponse;
import com.sporty.ticketsapi.entity.Comment;
import com.sporty.ticketsapi.entity.CommentVisibility;
import com.sporty.ticketsapi.entity.Ticket;
import com.sporty.ticketsapi.entity.TicketStatus;
import com.sporty.ticketsapi.exceptions.InvalidVisibilityException;
import com.sporty.ticketsapi.exceptions.TicketNotFoundException;
import com.sporty.ticketsapi.mapper.CommentMapper;
import com.sporty.ticketsapi.mapper.TicketMapper;
import com.sporty.ticketsapi.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final CommentMapper commentMapper;
    private final TicketStatusService ticketStatusService;



    public TicketService(TicketRepository ticketRepository, TicketMapper ticketMapper, CommentMapper commentMapper, TicketStatusService ticketStatusService) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.commentMapper = commentMapper;
        this.ticketStatusService = ticketStatusService;
    }

    public CreateTicketResponse createTicket(CreateTicketPayload createTicketPayload) {
        Ticket ticket = ticketMapper.createTicketPayloadToTicket(createTicketPayload);
        ticket.setStatus(TicketStatus.OPEN);
        ticket = ticketRepository.save(ticket);
        CreateTicketResponse ticketResponse = ticketMapper.ticketToCreateTicketResponse(ticket);
        return ticketResponse;
    }

    public List<Ticket> findAllTickets(TicketStatus status, String userId, String assigneeId) {
        return ticketRepository.findAll().stream()
                .filter(ticket -> status == null || ticket.getStatus() == status)
                .filter(ticket -> userId == null || (ticket.getUserId() != null && ticket.getUserId().equals(userId)))
                .filter(ticket -> assigneeId == null || (ticket.getAssigneeId() != null && ticket.getAssigneeId().equals(assigneeId)))
                .collect(Collectors.toList());
    }

    public UpdateTicketStatusResponse updateTicketStatus(UUID ticketId, UpdateTicketStatusPayload updateTicketStatusDto) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        if (!ticketStatusService.canTransitionTo(ticket.getStatus(), updateTicketStatusDto.getStatus())) {
            throw new IllegalStateException("Invalid status transition from " + ticket.getStatus() + " to " + updateTicketStatusDto.getStatus());
        }
        ticket.setStatus(updateTicketStatusDto.getStatus());
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.ticketToUpdateTicketStatusResponse(updatedTicket);
    }

    public AddCommentResponse addComment(UUID ticketId, AddCommentPayload addCommentDto) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        // Check if the authorId starts with "user" to limit their ability to only adding public comments
        String authorId = addCommentDto.getAuthorId();
        boolean isUser = authorId.startsWith("user");

        // Enforce visibility rules
        if (isUser && !addCommentDto.getVisibility().equals(CommentVisibility.PUBLIC)) {
            throw new InvalidVisibilityException("Users can only add public comments.");
        }
        Comment comment = commentMapper.addCommentPayloadToComment(addCommentDto);
        comment.setTicketId(ticket.getTicketId());
        Comment savedComment = ticketRepository.addComment(comment);
        return commentMapper.commentToAddCommentResponse(savedComment);
    }

    // Enforce visibility rules while getting comments as well
    public List<Comment> getComments(UUID ticketId, String userType) {
        List<Comment> comments = ticketRepository.findCommentsByTicketId(ticketId);
        if ("user".equalsIgnoreCase(userType)) {
            return comments.stream()
                    .filter(comment -> comment.getVisibility() == com.sporty.ticketsapi.entity.CommentVisibility.PUBLIC)
                    .collect(Collectors.toList());
        }
        return comments; // Agents can see all comments
    }
}
