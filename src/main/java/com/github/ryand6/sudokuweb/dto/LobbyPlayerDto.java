package com.github.ryand6.sudokuweb.dto;

import com.github.ryand6.sudokuweb.domain.LobbyPlayerId;
import lombok.*;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
/*
LobbyPlayerDto used for transferring necessary Lobby entity fields to frontend
*/
public class LobbyPlayerDto {

    private LobbyPlayerId id;

    private Instant joinedAt;

}
