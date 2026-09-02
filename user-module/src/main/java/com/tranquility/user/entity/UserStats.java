package com.tranquility.user.entity;

import com.tranquility.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_stats")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserStats extends AuditableEntity {
    @Id
    private UUID id;    // acts as pk + fk

    @OneToOne(fetch = FetchType.LAZY, optional = false) // optional = false => can't exist without User
    @MapsId // matches parent(User) Id(pk) to this (here pk)
    @JoinColumn(name = "id") // join via pk (id) of this table
    private User user;

    private int analysisCount = 0;
    private int totalPoints = 0;
    private int currentStreak = 1;
    private int longestStreak = 1;
    private Instant lastAnalysisAt;

    public static UserStats create() {
        return new UserStats();
    }
}

/*
    user_stats {
        id,
        analysis_count,
        total_points,
        current_streak,
        longest_streak,
        last_analysis_at
    }

    users.id = user_stats.id
    id = pk + fk
 */
