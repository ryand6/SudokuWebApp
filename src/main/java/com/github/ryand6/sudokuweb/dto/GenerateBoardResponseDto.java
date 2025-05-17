package com.github.ryand6.sudokuweb.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GenerateBoardResponseDto {

    private String initialBoard;

    private String solution;

}
