package com.github.ryand6.sudokuweb.dto.request;

import com.github.ryand6.sudokuweb.enums.LobbyStatus;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LobbyPlayerStatusUpdateRequestDto {

    private Long lobbyId;

    private Long userId;

    private LobbyStatus lobbyStatus;

}
