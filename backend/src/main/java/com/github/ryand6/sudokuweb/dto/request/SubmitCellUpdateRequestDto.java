package com.github.ryand6.sudokuweb.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubmitCellUpdateRequestDto {

    @Min(0)
    @Max(8)
    int row;

    @Min(0)
    @Max(8)
    int col;

    @Min(1)
    @Max(9)
    int value;

}
