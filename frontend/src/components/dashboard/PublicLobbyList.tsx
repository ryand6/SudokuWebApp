import { useGetTenPublicLobbies } from "@/api/rest/lobby/query/useGetTenPublicLobbies";
import { SpinnerButton } from "../ui/custom/SpinnerButton";
import { ErrorAlert } from "../ui/custom/ErrorAlert";
import { useInView } from "react-intersection-observer";
import { LobbyResultRow } from "./LobbyResultRow";
import { useJoinPublicLobby } from "@/api/rest/lobby/mutate/useJoinPublicLobby";
import { Button } from "../ui/button";
import { useGetCurrentUser } from "@/api/rest/users/query/useGetCurrentUser";
import { toast } from "react-toastify";
import { useHandleFetchNextLobbyListPage } from "@/hooks/lobby/useHandleFetchNextLobbyListPage";
import { useMemo } from "react";

export function PublicLobbyList({
    gameType
}: {
    gameType: string
}) {

    const {data: publicLobbies, isLoading: isLoadingLobbies, isError: isLobbiesError, error: lobbyError, hasNextPage, fetchNextPage, isFetchingNextPage, refetch } = useGetTenPublicLobbies();
    const {data: currentUser, isLoading: isLoadingUser, isError: isUserError, error: userError} = useGetCurrentUser();
    const joinPublicLobby = useJoinPublicLobby();

    const { ref, inView } = useInView({
        // Call more page results as soon as the 'sentinel' div is in view
        threshold: 0,
    });

    // custom hook used to handle fetching next lobby list page
    useHandleFetchNextLobbyListPage(inView, hasNextPage, isFetchingNextPage, fetchNextPage);

    if (isLobbiesError || isUserError) {
        if (isLobbiesError) console.log(lobbyError);
        else console.log(userError);
        return <ErrorAlert />;
    }

    if (isLoadingLobbies || isLoadingUser) return <SpinnerButton />;

    if (!currentUser) {
        toast.error("An error has occurred.", {containerId: "foreground"});
        return null;
    }

    const lobbies = publicLobbies?.pages.flat() ?? [];

    const filteredLobbies = useMemo(() => {
        if (lobbies.length === 0) return [];
        return lobbies.filter((lobby) => lobby.lobbySettings.gameType === gameType.toUpperCase());
    }, [gameType])

    const handleClick = (lobbyId: number) => {
        joinPublicLobby.mutate(lobbyId);
    };

    return (
        <div className="flex flex-col items-center min-h-0 overflow-y-auto px-5 pb-5 gap-2">
            <div className="flex justify-center">
                <Button className="rounded-3xl text-xs! cursor-pointer bg-secondary/70 hover:bg-secondary" onClick={() => refetch()} variant={"secondary"}>⭯ Refresh</Button>
            </div>
            <div className="flex flex-col w-full gap-2">
                {filteredLobbies.map((lobby, key) => (
                    <LobbyResultRow 
                        lobbyId={lobby.id}
                        userId={currentUser.id}
                        lobbyName={lobby.lobbyName}
                        lobbyPlayers={lobby.lobbyPlayers}
                        hostName={lobby.host.username}
                        inGame={lobby.inGame}
                        difficulty={lobby.lobbySettings.difficulty}
                        timeLimit={lobby.lobbySettings.timeLimit}
                        gameMode={lobby.lobbySettings.gameMode}
                        gameType={lobby.lobbySettings.gameType}
                        createdAt={lobby.createdAt}
                        handleClick={handleClick} 
                        key={key}
                    ></LobbyResultRow>
                ))}
                <div ref={ref} className="h-5" />
            </div>
        </div>

    );
}