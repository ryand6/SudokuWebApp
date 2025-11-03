package com.github.ryand6.sudokuweb.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LobbyChatSubmitMessageResponseDto {

    private Long lobbyId;

    private String username;

    private String message;

}
