package com.github.ryand6.sudokuweb.domain.lobby.token;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenIdentifiers {

    private Long lobbyId;

    private Long userId;

}
