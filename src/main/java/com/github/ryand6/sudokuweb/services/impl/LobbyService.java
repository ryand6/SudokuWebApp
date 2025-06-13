package com.github.ryand6.sudokuweb.services.impl;

import com.github.ryand6.sudokuweb.repositories.LobbyRepository;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.stream.Collectors;

@Service
public class LobbyService {

    private final LobbyRepository lobbyRepository;
    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 25;

    public LobbyService(LobbyRepository lobbyRepository) {
        this.lobbyRepository = lobbyRepository;
    }

    // Returns a unique join code not used in other private lobbies
    public String generateUniqueCode() {
        String code;
        do {
            code = new Random().ints(CODE_LENGTH, 0, CODE_CHARS.length())
                    .mapToObj(i -> String.valueOf(CODE_CHARS.charAt(i)))
                    .collect(Collectors.joining());
        } while (lobbyRepository.existsByJoinCode(code));
        return code;
    }

}
