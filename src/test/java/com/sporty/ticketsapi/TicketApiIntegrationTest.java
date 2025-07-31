package com.sporty.ticketsapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sporty.ticketsapi.dto.AddCommentPayload;
import com.sporty.ticketsapi.dto.CreateTicketPayload;
import com.sporty.ticketsapi.dto.UpdateTicketStatusPayload;
import com.sporty.ticketsapi.entity.CommentVisibility;
import com.sporty.ticketsapi.entity.TicketStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TicketApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateTicketAndListIt() throws Exception {
        CreateTicketPayload createPayload = new CreateTicketPayload();
        createPayload.setUserId("user-001");
        createPayload.setSubject("Payment issue");
        createPayload.setDescription("I was charged twice for the same order.");

        MvcResult createResult = mockMvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ticketId").exists())
                .andExpect(jsonPath("$.status").value("OPEN"))
                .andReturn();

        String ticketId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("ticketId").asText();

        mockMvc.perform(get("/tickets?userId=user-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ticketId").value(ticketId))
                .andExpect(jsonPath("$[0].subject").value("Payment issue"));
    }

    @Test
    void testUpdateTicketStatus() throws Exception {
        CreateTicketPayload createPayload = new CreateTicketPayload();
        createPayload.setUserId("user-002");
        createPayload.setSubject("Login problem");
        createPayload.setDescription("I cannot log into my account.");

        MvcResult createResult = mockMvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isCreated())
                .andReturn();

        String ticketId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("ticketId").asText();

        UpdateTicketStatusPayload updatePayload = new UpdateTicketStatusPayload();
        updatePayload.setStatus(TicketStatus.IN_PROGRESS);

        mockMvc.perform(patch("/tickets/{ticketId}/status", ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void testAddPublicCommentToTicket() throws Exception {
        CreateTicketPayload createPayload = new CreateTicketPayload();
        createPayload.setUserId("user-003");
        createPayload.setSubject("Feature request");
        createPayload.setDescription("Please add a dark mode.");

        MvcResult createResult = mockMvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isCreated())
                .andReturn();

        String ticketId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("ticketId").asText();

        AddCommentPayload commentPayload = new AddCommentPayload();
        commentPayload.setAuthorId("agent-123");
        commentPayload.setContent("We're currently investigating your issue.");
        commentPayload.setVisibility(CommentVisibility.PUBLIC);

        mockMvc.perform(post("/tickets/{ticketId}/comments", ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentPayload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").exists())
                .andExpect(jsonPath("$.visibility").value("PUBLIC"));
    }

    @Test
    void testListTicketsWithStatusFilter() throws Exception {
        CreateTicketPayload openTicketPayload = new CreateTicketPayload();
        openTicketPayload.setUserId("user-004");
        openTicketPayload.setSubject("Open Ticket");
        openTicketPayload.setDescription("This is an open ticket.");

        mockMvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(openTicketPayload)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/tickets?status=OPEN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("OPEN"));
    }

    @Test
    void testCommentVisibilityForUserAndAgent() throws Exception {
        CreateTicketPayload createPayload = new CreateTicketPayload();
        createPayload.setUserId("user-005");
        createPayload.setSubject("Comment visibility test");
        createPayload.setDescription("This is a test for comment visibility.");

        MvcResult createResult = mockMvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isCreated())
                .andReturn();

        String ticketId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("ticketId").asText();

        AddCommentPayload publicComment = new AddCommentPayload();
        publicComment.setAuthorId("agent-123");
        publicComment.setContent("This is a public comment.");
        publicComment.setVisibility(CommentVisibility.PUBLIC);

        mockMvc.perform(post("/tickets/{ticketId}/comments", ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(publicComment)))
                .andExpect(status().isCreated());

        AddCommentPayload internalComment = new AddCommentPayload();
        internalComment.setAuthorId("agent-123");
        internalComment.setContent("This is an internal comment.");
        internalComment.setVisibility(CommentVisibility.INTERNAL);

        mockMvc.perform(post("/tickets/{ticketId}/comments", ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(internalComment)))
                .andExpect(status().isCreated());

        // User should only see public comments
        mockMvc.perform(get("/tickets/{ticketId}/comments?userType=user", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].content").value("This is a public comment."));

        // Agent should see all comments
        mockMvc.perform(get("/tickets/{ticketId}/comments?userType=agent", ticketId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testInvalidStatusTransition() throws Exception {
        CreateTicketPayload createPayload = new CreateTicketPayload();
        createPayload.setUserId("user-006");
        createPayload.setSubject("Invalid transition test");
        createPayload.setDescription("This is a test for invalid status transition.");

        MvcResult createResult = mockMvc.perform(post("/tickets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isCreated())
                .andReturn();

        String ticketId = objectMapper.readTree(createResult.getResponse().getContentAsString()).get("ticketId").asText();

        UpdateTicketStatusPayload updatePayload = new UpdateTicketStatusPayload();
        updatePayload.setStatus(TicketStatus.CLOSED);

        mockMvc.perform(patch("/tickets/{ticketId}/status", ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk());

        UpdateTicketStatusPayload invalidUpdatePayload = new UpdateTicketStatusPayload();
        invalidUpdatePayload.setStatus(TicketStatus.IN_PROGRESS);

        mockMvc.perform(patch("/tickets/{ticketId}/status", ticketId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUpdatePayload)))
                .andExpect(status().isBadRequest());
    }
}