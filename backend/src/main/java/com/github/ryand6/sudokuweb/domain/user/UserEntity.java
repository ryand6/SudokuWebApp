package com.github.ryand6.sudokuweb.domain.user;

import com.github.ryand6.sudokuweb.domain.game.GameEntity;
import com.github.ryand6.sudokuweb.domain.user.settings.UserSettingsEntity;
import com.github.ryand6.sudokuweb.domain.user.stats.UserStatsEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

// Entity that maps to users db table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    // Name of OAuth2 provider User is registered using
    @Column(name = "provider", nullable = false)
    private String provider;

    // Unique OAuth2 provider ID used to authenticate
    @Column(name = "provider_id", nullable = false)
    private String providerId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "is_online")
    private boolean isOnline;

    @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private UserStatsEntity userStatsEntity;

    @OneToOne(mappedBy = "userEntity", cascade = CascadeType.ALL)
    private UserSettingsEntity userSettingsEntity;

    // Overwrite to prevent circular referencing/lazy loading of referenced/nested entities
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEntity userEntity = (UserEntity) o;
        return id != null && id.equals(userEntity.id);
    }

    // Overwrite to prevent circular referencing/lazy loading of referenced/nested entities
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + provider.hashCode();
        result = 31 * result + providerId.hashCode();
        return result;
    }

}
