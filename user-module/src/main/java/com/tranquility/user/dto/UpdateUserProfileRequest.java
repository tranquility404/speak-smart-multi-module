package com.tranquility.user.dto;

import jakarta.validation.constraints.Size;
import org.openapitools.jackson.nullable.JsonNullable;

public record UpdateUserProfileRequest(
        JsonNullable<@Size(max = 50) String> username,
        JsonNullable<@Size(max = 100) String> name,
        JsonNullable<@Size(max = 500) String> bio
) { }