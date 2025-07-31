package com.sporty.ticketsapi.repository;

import com.sporty.ticketsapi.entity.Comment;
import com.sporty.ticketsapi.entity.Ticket;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class TicketRepository {
    private final Map<UUID, Ticket> tickets = new ConcurrentHashMap<>();
    private final Map<UUID, List<Comment>> comments = new ConcurrentHashMap<>();

    public Ticket save(Ticket ticket) {
        tickets.put(ticket.getTicketId(), ticket);
        return ticket;
    }

    public Optional<Ticket> findById(UUID ticketId) {
        return Optional.ofNullable(tickets.get(ticketId));
    }

    public List<Ticket> findAll() {
        return new ArrayList<>(tickets.values());
    }

    public Comment addComment(Comment comment) {
        comments.computeIfAbsent(comment.getTicketId(), k -> new ArrayList<>()).add(comment);
        return comment;
    }

    public List<Comment> findCommentsByTicketId(UUID ticketId) {
        return comments.getOrDefault(ticketId, new ArrayList<>());
    }
}
