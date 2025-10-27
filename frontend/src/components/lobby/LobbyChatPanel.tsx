import { useLobbyMessages } from "@/hooks/lobby/useLobbyMessages"
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";

export function LobbyChatPanel({lobby}: {lobby: LobbyDto}) {

    const {data: messages} = useLobbyMessages(lobby.id);

    return (
        <></>
    )
}