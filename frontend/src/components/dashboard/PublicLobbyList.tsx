import { useTenPublicLobbies } from "@/hooks/lobby/useTenPublicLobbies";
import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { useEffect, useState } from "react";
import { SpinnerButton } from "../ui/custom/SpinnerButton";
import { ErrorAlert } from "../ui/custom/ErrorAlert";
import { useInView } from "react-intersection-observer";
import { LobbyResultRow } from "../lobby/LobbyResultRow";

export function PublicLobbyList() {

    const {data: publicLobbies, isLoading, isError, error, hasNextPage, fetchNextPage, isFetchingNextPage } = useTenPublicLobbies();

    if (isError) {
        console.log(error);
        return <ErrorAlert />;
    }

    if (isLoading) return <SpinnerButton />;

    const { ref, inView } = useInView({
        // Call more page results as soon as the 'sentinel' div is in view
        threshold: 0,
    });

    useEffect(() => {
        if (inView && hasNextPage && !isFetchingNextPage) {
            fetchNextPage();
        }
    }, [inView, hasNextPage, fetchNextPage, isFetchingNextPage])

    const lobbies = publicLobbies?.pages.flat() ?? [];

    // UPDATE
    const handleClick = (lobbyId: number) => {

    };

    return (
        <div className="flex flex-col scroll-auto" >
            {lobbies.map((lobby) => (
                <LobbyResultRow lobby={lobby} handleClick={handleClick}></LobbyResultRow>
            ))}
            <div ref={ref} className="h-5" />
        </div>
    );
}