package com.github.ryand6.sudokuweb.domain.lobby;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class LobbyGameCreationUpdate {

    private Long lobbyId;

    private Long gameId;

    private boolean inGame;

}
