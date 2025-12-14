export type TokenWithExpiry = {
    token: string;
    expiresAt: number; // timestamp in milliseconds
};

export type UserActiveTokensDto = {
    activeTokens: TokenWithExpiry[];
};