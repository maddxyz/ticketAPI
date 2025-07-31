package com.sporty.ticketsapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import com.sporty.ticketsapi.dto.AddCommentPayload;
import com.sporty.ticketsapi.dto.AddCommentResponse;
import com.sporty.ticketsapi.entity.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);
    @Mapping(target = "commentId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ticketId", ignore = true)

    Comment addCommentPayloadToComment(AddCommentPayload addCommentDto);
    AddCommentResponse commentToAddCommentResponse(Comment comment);

}
