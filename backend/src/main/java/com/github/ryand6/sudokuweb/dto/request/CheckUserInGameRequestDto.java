package com.github.ryand6.sudokuweb.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CheckUserInGameRequestDto {

    private Long currentGameId;

    private Long userId;

}
