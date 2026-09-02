package com.tranquility.user.repository;

import com.tranquility.user.dto.LeaderboardDto;
import com.tranquility.user.dto.UserOverview;
import com.tranquility.user.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, UserRepositoryCustom {
    <T> Optional<T> findByEmail(String email, Class<T> type);

    @Query("""
        SELECT new com.tranquility.user.dto.UserOverview(
            u.email,
            p.username, p.name, p.bio, p.profilePicUrl,
            s.analysisCount, s.totalPoints, s.currentStreak, s.longestStreak, s.lastAnalysisAt
        )
        FROM User u
        JOIN u.profile p
        JOIN u.stats s
        WHERE u.id = :userId
        """)
    Optional<UserOverview> findUserOverview(@Param("userId") UUID userId);

//    Higher points first -> Higher streak first -> smaller email first
    @Query("""
            SELECT new com.tranquility.user.dto.LeaderboardDto(
                u.email,
                p.name, p.profilePicUrl,
                s.analysisCount, s.totalPoints, s.currentStreak
            )
            FROM User u
            JOIN u.profile p
            JOIN u.stats s
            ORDER BY
            s.totalPoints DESC,
            s.currentStreak DESC,
            u.email ASC
            """)
    List<LeaderboardDto> findLeaderboardFirstPage(Pageable pageable);

    //    Higher points first -> Higher streak first -> smaller email first
    @Query("""
            SELECT new com.tranquility.user.dto.LeaderboardDto(
                u.email,
                p.name, p.profilePicUrl,
                s.analysisCount, s.totalPoints, s.currentStreak
            )
            FROM User u
            JOIN u.profile p
            JOIN u.stats s
            WHERE s.totalPoints < :totalPoints
            OR (
                s.totalPoints = :totalPoints
                AND s.currentStreak < :currentStreak
            ) OR (
                s.totalPoints = :totalPoints
                AND s.currentStreak = :currentStreak
                AND u.email > :email
            )
            ORDER BY
            s.totalPoints DESC,
            s.currentStreak DESC,
            u.email ASC
            """)
    List<LeaderboardDto> findLeaderboardNextPage(
            @Param("totalPoints") int totalPoints,
            @Param("currentStreak") int currentStreak,
            @Param("email") String email,
            Pageable pageable
    );

//    -------------------------- User Profile --------------------
    @Query("""
        SELECT u.profile.profilePicUrl
        FROM User u
        WHERE u.id = :userId
    """)
    Optional<String> findProfilePicUrlByUserId(@Param("userId") UUID userId);
}