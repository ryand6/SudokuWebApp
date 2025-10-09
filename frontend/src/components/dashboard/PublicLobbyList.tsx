import { useTenPublicLobbies } from "@/hooks/lobby/useTenPublicLobbies";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { useEffect, useState } from "react";

export function PublicLobbyList() {

    const {data: publicLobbies, fetchNextPage} = useTenPublicLobbies();
    useEffect(() => {
        if (!publicLobbies) return
    })

    return <></>;
}