import type { TimeLimitPreset } from "@/types/enum/TimeLimitPreset";

export function updateLobbyTimeLimit(send: (dest: string, body: any) => void, lobbyId: number, timeLimit: TimeLimitPreset) {
    send(`/app/lobby/${lobbyId}/update-time-limit`, {timeLimit: timeLimit});
}