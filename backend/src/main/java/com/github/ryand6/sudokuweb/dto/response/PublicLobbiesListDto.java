package com.github.ryand6.sudokuweb.dto.response;

import com.github.ryand6.sudokuweb.dto.entity.LobbyDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublicLobbiesListDto {

    private List<LobbyDto> publicLobbies;

}
