package com.github.ryand6.sudokuweb.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProviderLinkRequestDto {

    @NotNull(message = "Provider name is required")
    String providerName;

}
