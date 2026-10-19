package com.github.ryand6.sudokuweb.domain.lobby.player;

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

}
