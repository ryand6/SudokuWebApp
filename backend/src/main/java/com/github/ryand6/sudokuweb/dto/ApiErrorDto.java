package com.github.ryand6.sudokuweb.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiErrorDto {

    private String errorMessage;

}
