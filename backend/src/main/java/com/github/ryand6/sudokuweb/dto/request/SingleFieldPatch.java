package com.github.ryand6.sudokuweb.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SingleFieldPatch {

    private Long userId;

    private String field;

    private Object value;
}
