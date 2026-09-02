package com.tranquility.user.model;

public record LeaderboardCursor(
        int totalPoints,
        int currentStreak,
        String email
) { }