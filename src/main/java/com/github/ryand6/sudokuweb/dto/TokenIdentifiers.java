package com.github.ryand6.sudokuweb.dto;

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
