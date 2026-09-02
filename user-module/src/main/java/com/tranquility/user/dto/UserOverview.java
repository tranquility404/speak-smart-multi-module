package com.tranquility.user.dto;

import java.time.Instant;

public record UserOverview(
    String email,

    String username,
    String name,
    String bio,
    String profilePicUrl,

    int analysisCount,
    int totalPoints,
    int currentStreak,
    int longestStreak,
    Instant lastAnalysisAt
) { }