package com.github.ryand6.sudokuweb.dto.response;

import com.github.ryand6.sudokuweb.dto.entity.user.UserDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TopFivePlayersDto {

    private List<UserDto> topFivePlayers;

}
