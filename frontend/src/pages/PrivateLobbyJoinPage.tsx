import { SpinnerButton } from "@/components/ui/custom/SpinnerButton";
import { useJoinPrivateLobby } from "@/api/rest/lobby/mutate/useJoinPrivateLobby";
import { useValidatePrivateLobbyToken } from "@/hooks/lobby/useValidatePrivateLobbyToken";
import { useEffect } from "react";
import { useParams } from "react-router-dom";

export function PrivateLobbyJoinPage() {
    const { token } = useParams();

    useValidatePrivateLobbyToken(token);

    const joinPrivateLobby = useJoinPrivateLobby();

    useEffect(() => {
        if (token) joinPrivateLobby.mutate(token);
    }, [token])

    return <SpinnerButton />
}