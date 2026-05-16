import { useEffect, type Dispatch, type SetStateAction } from "react";

export function useShowGameResults(
    finishedGame: boolean | undefined,
    endedPrematurely: boolean | undefined,
    setShowGameResultsModal: Dispatch<SetStateAction<boolean>>
) {
    useEffect(() => {
        if (finishedGame === undefined || endedPrematurely === undefined) return;
        const showModal = finishedGame || endedPrematurely;
        setShowGameResultsModal(showModal);
    }, [finishedGame, setShowGameResultsModal]);
}
