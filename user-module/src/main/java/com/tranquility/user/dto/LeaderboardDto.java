package com.tranquility.user.dto;

public record LeaderboardDto(
        String email,

        String name,
        String profilePicUrl,

        int analysisCount,
        int totalPoints,
        int currentStreak
) { }