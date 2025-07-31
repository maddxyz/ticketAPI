package com.sporty.ticketsapi.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum CommentVisibility {
    PUBLIC,
    INTERNAL;

    @JsonCreator
    public static CommentVisibility fromString(String value) {
        return CommentVisibility.valueOf(value.toUpperCase());
    }
}
