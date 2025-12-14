package com.github.ryand6.sudokuweb.dto.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserActiveTokensDto {

    public static class TokenWithExpiry {
        public String token;
        public Long expiresAt;

        public TokenWithExpiry(String token, Long expiresAt) {
            this.token = token;
            this.expiresAt = expiresAt;
        }
    }

    private List<TokenWithExpiry> activeTokens;

}
