package com.github.ryand6.sudokuweb.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiErrorDto {

    private String errorMessage;

}
