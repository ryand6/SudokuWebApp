package com.github.ryand6.sudokuweb.domain.user.settings;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SingleFieldPatch {

    private String field;

    private Object value;

}
