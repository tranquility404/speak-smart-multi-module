package com.tranquility.user.entity;

import com.tranquility.common.model.AuditableEntity;
import com.tranquility.common.model.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_email", columnList = "email", unique = true)   // most commonly queried
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // allows hibernate to use new User() but not us in code
public class User extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)    // default length = 255
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)    // it will store it as String not Ordinal (1,2,3)
    @Column(nullable = false, length = 30)
    private Role role;

    private Instant passwordUpdatedAt;

    @OneToOne(
            mappedBy = "user",  // there will be no profile_id as user_profiles.id = users.id mapped by user field in that entity
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private UserProfile profile;

    @OneToOne(
            mappedBy = "user",  // there will be no stats_id as user_stats.id = users.id mapped by user field in that entity
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private UserStats stats;

    public void setProfile(UserProfile profile) {
        this.profile = profile;
        profile.setUser(this);
    }

    public void setStats(UserStats stats) {
        this.stats = stats;
        stats.setUser(this);
    }

    public static User create(String email, String password, Role role, UserProfile profile, UserStats stats) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setPasswordUpdatedAt(Instant.now());
        user.setRole(role);
        user.setProfile(profile);
        user.setStats(stats);
        return user;
    }
}

/*
    users {
        id,
        email,
        password,
        role,
        password_updated_at
    }

    users.id = user_profiles.id
    id = pk + fk
 */