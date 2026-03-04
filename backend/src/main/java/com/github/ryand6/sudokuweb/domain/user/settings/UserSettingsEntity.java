package com.github.ryand6.sudokuweb.domain.user.settings;

import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "user_settings")
public class UserSettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_settings_id_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity userEntity;

    @Column(name = "is_dark_mode_active")
    private boolean isDarkModeActive = false;

    @Column(name = "is_sound_active")
    private boolean isSoundActive = true;

}
