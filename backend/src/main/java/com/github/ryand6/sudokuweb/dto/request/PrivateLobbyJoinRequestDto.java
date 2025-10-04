package com.github.ryand6.sudokuweb.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PrivateLobbyJoinRequestDto {

    private String token;

}
