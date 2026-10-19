package com.github.ryand6.sudokuweb.domain.lobby.player;

import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LobbyPlayerStatusUpdate {

    private Long lobbyId;

    private Long userId;

    private LobbyStatus lobbyStatus;

}
