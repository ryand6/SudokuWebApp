import { useEffect, type Dispatch, type SetStateAction } from "react";

export function useSetShowGameResultsModal(
    finishedGame: boolean | undefined,
    setShowGameResultsModal: Dispatch<SetStateAction<boolean>>
) {
    useEffect(() => {
        if (finishedGame === undefined) return;
        setShowGameResultsModal(finishedGame);
    }, [finishedGame, setShowGameResultsModal]);
}
