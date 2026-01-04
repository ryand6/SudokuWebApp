import { useGetTenPublicLobbies } from "@/hooks/lobby/useGetTenPublicLobbies";
import { useEffect } from "react";
import { SpinnerButton } from "../ui/custom/SpinnerButton";
import { ErrorAlert } from "../ui/custom/ErrorAlert";
import { useInView } from "react-intersection-observer";
import { LobbyResultRow } from "../lobby/LobbyResultRow";
import { useJoinPublicLobby } from "@/hooks/lobby/useJoinPublicLobby";
import { Button } from "../ui/button";
import { useGetCurrentUser } from "@/hooks/users/useGetCurrentUser";
import { toast } from "react-toastify";

export function PublicLobbyList() {

    const {data: publicLobbies, isLoading: isLoadingLobbies, isError: isLobbiesError, error: lobbyError, hasNextPage, fetchNextPage, isFetchingNextPage, refetch } = useGetTenPublicLobbies();
    const {data: currentUser, isLoading: isLoadingUser, isError: isUserError, error: userError} = useGetCurrentUser();
    const joinPublicLobby = useJoinPublicLobby();

    const { ref, inView } = useInView({
        // Call more page results as soon as the 'sentinel' div is in view
        threshold: 0,
    });

    useEffect(() => {
        if (inView && hasNextPage && !isFetchingNextPage) {
            fetchNextPage();
        }
    }, [inView, hasNextPage, fetchNextPage, isFetchingNextPage])

    if (isLobbiesError || isUserError) {
        if (isLobbiesError) console.log(lobbyError);
        else console.log(userError);
        return <ErrorAlert />;
    }

    if (isLoadingLobbies || isLoadingUser) return <SpinnerButton />;

    if (!currentUser) {
        toast.error("An error has occurred.");
        return null;
    }

    const lobbies = publicLobbies?.pages.flat() ?? [];

    const handleClick = (lobbyId: number) => {
        joinPublicLobby.mutate(lobbyId);
    };

    return (
        <div>
            <div className="flex justify-center">
                <Button className="rounded-3xl bg-sidebar-primary cursor-pointer" onClick={() => refetch()}>⭯ Refresh</Button>
            </div>
            <div className="flex flex-col scroll-auto" >
                {lobbies.map((lobby, key) => (
                    <LobbyResultRow lobby={lobby} currentUser={currentUser} handleClick={handleClick} key={key}></LobbyResultRow>
                ))}
                <div ref={ref} className="h-5" />
            </div>
        </div>

    );
}