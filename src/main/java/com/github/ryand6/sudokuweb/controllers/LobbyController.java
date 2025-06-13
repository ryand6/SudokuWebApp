package com.github.ryand6.sudokuweb.controllers;

import com.github.ryand6.sudokuweb.services.impl.LobbyService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/lobby")
public class LobbyController {

    private final LobbyService lobbyService;

    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @GetMapping("/create")
    public String createLobbyView() {
        return "lobby/create-lobby"; // Render view "templates/lobby/create-lobby.html"
    }

    // Retrieve unique join code from backend
    @GetMapping("/generate-join-code")
    public ResponseEntity<String> generateJoinCode() {
        String code = lobbyService.generateUniqueCode();
        return ResponseEntity.ok(code);
    }

}
