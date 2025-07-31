package com.sporty.ticketsapi.service;

import com.sporty.ticketsapi.dto.AddCommentPayload;
import com.sporty.ticketsapi.dto.AddCommentResponse;
import com.sporty.ticketsapi.dto.CreateTicketPayload;
import com.sporty.ticketsapi.dto.UpdateTicketStatusPayload;
import com.sporty.ticketsapi.entity.Comment;
import com.sporty.ticketsapi.entity.CommentVisibility;
import com.sporty.ticketsapi.entity.Ticket;
import com.sporty.ticketsapi.entity.TicketStatus;
import com.sporty.ticketsapi.exceptions.InvalidVisibilityException;
import com.sporty.ticketsapi.mapper.CommentMapper;
import com.sporty.ticketsapi.mapper.TicketMapper;
import com.sporty.ticketsapi.repository.TicketRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketServiceUnitTest {

    @Mock
    private TicketRepository ticketRepository;

    @Mock
    private TicketMapper ticketMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private TicketStatusService ticketStatusService;

    @InjectMocks
    private TicketService ticketService;

    @Test
    void testCreateTicket() {
        CreateTicketPayload payload = new CreateTicketPayload();
        Ticket ticket = new Ticket();
        when(ticketMapper.createTicketPayloadToTicket(any(CreateTicketPayload.class))).thenReturn(ticket);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        ticketService.createTicket(payload);

        verify(ticketRepository).save(ticket);
    }

    @Test
    void testFindAllTicketsWithStatusFilter() {
        Ticket ticket1 = new Ticket();
        ticket1.setStatus(TicketStatus.OPEN);
        Ticket ticket2 = new Ticket();
        ticket2.setStatus(TicketStatus.IN_PROGRESS);
        when(ticketRepository.findAll()).thenReturn(Arrays.asList(ticket1, ticket2));

        List<Ticket> result = ticketService.findAllTickets(TicketStatus.OPEN, null, null);

        assertEquals(1, result.size());
        assertEquals(TicketStatus.OPEN, result.get(0).getStatus());
    }

    @Test
    void testUpdateTicketStatus() {
        UUID ticketId = UUID.randomUUID();
        UpdateTicketStatusPayload payload = new UpdateTicketStatusPayload();
        payload.setStatus(TicketStatus.IN_PROGRESS);
        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.OPEN);
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(ticketStatusService.canTransitionTo(TicketStatus.OPEN, TicketStatus.IN_PROGRESS)).thenReturn(true);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        ticketService.updateTicketStatus(ticketId, payload);

        verify(ticketRepository).save(ticket);
        assertEquals(TicketStatus.IN_PROGRESS, ticket.getStatus());
    }

    @Test
    void testAddPublicComment() {
        UUID ticketId = UUID.randomUUID();
        AddCommentPayload payload = new AddCommentPayload();
        payload.setAuthorId("user-123");
        payload.setVisibility(CommentVisibility.PUBLIC);
        Ticket ticket = new Ticket();
        Comment savedComment = new Comment();
        savedComment.setCommentId(UUID.randomUUID());
        savedComment.setVisibility(CommentVisibility.PUBLIC);

        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(commentMapper.addCommentPayloadToComment(any(AddCommentPayload.class))).thenReturn(new Comment());
        when(ticketRepository.addComment(any(Comment.class))).thenReturn(savedComment);
        when(commentMapper.commentToAddCommentResponse(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            AddCommentResponse response = new AddCommentResponse();
            response.setCommentId(comment.getCommentId());
            response.setVisibility(comment.getVisibility().name());
            return response;
        });

        AddCommentResponse response = ticketService.addComment(ticketId, payload);

        assertNotNull(response.getCommentId());
        assertEquals(CommentVisibility.PUBLIC.name(), response.getVisibility());
    }

    @Test
    void testUserCannotAddInternalComment() {
        UUID ticketId = UUID.randomUUID();
        AddCommentPayload payload = new AddCommentPayload();
        payload.setAuthorId("user-123");
        payload.setVisibility(CommentVisibility.INTERNAL);
        Ticket ticket = new Ticket();
        when(ticketRepository.findById(ticketId)).thenReturn(Optional.of(ticket));

        assertThrows(InvalidVisibilityException.class, () -> ticketService.addComment(ticketId, payload));
    }

    @Test
    void testGetCommentsForUser() {
        UUID ticketId = UUID.randomUUID();
        Comment publicComment = new Comment();
        publicComment.setVisibility(CommentVisibility.PUBLIC);
        Comment internalComment = new Comment();
        internalComment.setVisibility(CommentVisibility.INTERNAL);
        when(ticketRepository.findCommentsByTicketId(ticketId)).thenReturn(Arrays.asList(publicComment, internalComment));

        List<Comment> result = ticketService.getComments(ticketId, "user");

        assertEquals(1, result.size());
        assertEquals(CommentVisibility.PUBLIC, result.get(0).getVisibility());
    }

    @Test
    void testGetCommentsForAgent() {
        UUID ticketId = UUID.randomUUID();
        Comment publicComment = new Comment();
        publicComment.setVisibility(CommentVisibility.PUBLIC);
        Comment internalComment = new Comment();
        internalComment.setVisibility(CommentVisibility.INTERNAL);
        when(ticketRepository.findCommentsByTicketId(ticketId)).thenReturn(Arrays.asList(publicComment, internalComment));

        List<Comment> result = ticketService.getComments(ticketId, "agent");

        assertEquals(2, result.size());
    }
}
