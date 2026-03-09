package com.github.ryand6.sudokuweb.events;

import com.github.ryand6.sudokuweb.dto.entity.lobby.LobbyDto;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class LobbyUpdateEvent {

    private LobbyDto lobbyDto;

}
