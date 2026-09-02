package com.tranquility.auth.dto;

public record RegisterRequest(
        String name,
        String email,
        String password
) { }