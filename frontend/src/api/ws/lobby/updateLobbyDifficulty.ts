import type { Difficulty } from "@/types/enum/Difficulty";

export function updateLobbyDifficulty(send: (dest: string, body: any) => void, lobbyId: number, difficulty: Difficulty) {
    send(`/app/lobby/${lobbyId}/update-difficulty`, {difficulty: difficulty});
}