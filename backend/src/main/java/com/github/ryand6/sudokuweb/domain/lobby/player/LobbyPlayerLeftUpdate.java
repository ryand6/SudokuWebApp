package com.github.ryand6.sudokuweb.domain.lobby.player;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyCountdownDto;
import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LobbyPlayerLeftUpdate {

    private Long lobbyId;

    private Long userId;

    private boolean isActive;

    private UserDto host;

    private LobbyCountdownDto lobbyCountdown;

}
