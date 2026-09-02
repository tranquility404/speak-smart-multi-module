package com.tranquility.user.entity;

import com.tranquility.common.model.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserProfile extends AuditableEntity {

    @Id
    private UUID id;    // this id acts as pk + fk and is same as users.id. No new id is generated

    // optional = false means this entity will not exist without a parent & LAZY fetch mean, user won't be fetched from db unless accessed
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId // meaning use users.id as this entity's id therefore, users.id is same as user_profiles.id
    @JoinColumn(name = "id")    // represents relationship by 'id' col in this table
    private User user;

    @Column(nullable = false, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String bio;

    private String profilePicUrl;

    public static UserProfile create(String name, String username, String profilePicUrl) {
        UserProfile profile = new UserProfile();
        profile.setName(name);
        profile.setUsername(username);
        profile.setProfilePicUrl(profilePicUrl);
        return profile;
    }
}

/*
    user_profiles {
        id,
        username,
        name,
        bio,
        profile_pic_url
    }

    users.id = user_profiles.id
    users.id = user_stats.id
    id = pk + fk
 */