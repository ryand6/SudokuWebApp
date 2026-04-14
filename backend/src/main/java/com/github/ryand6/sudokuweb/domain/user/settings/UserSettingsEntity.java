package com.github.ryand6.sudokuweb.domain.user.settings;

import com.github.ryand6.sudokuweb.domain.user.UserEntity;
import com.github.ryand6.sudokuweb.enums.Theme;
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
    @MapsId
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity userEntity;

    //#######################//
    //    Visual Settings    //
    //#######################//

    @Column(name = "theme")
    private Theme theme = Theme.DEFAULT;

    @Column(name = "opponent_highlighted_squares")
    private boolean opponentHighlightedSquaresEnabled = true;

    @Column(name = "highlighted_houses")
    private boolean highlightedHousesEnabled = true;

    @Column(name = "highlighted_firsts")
    private boolean highlightedFirstsEnabled = true;

    //#######################//
    //    Audio Settings     //
    //#######################//

    @Column(name = "audio")
    private boolean audioEnabled = false;

    //#######################//
    // Notification Settings //
    //#######################//

    @Column(name = "game_chat_notifications")
    private boolean gameChatNotificationsEnabled = true;

    @Column(name = "score_notifications")
    private boolean scoreNotificationsEnabled = true;

    @Column(name = "streak_notifications")
    private boolean streakNotificationsEnabled = true;

}
