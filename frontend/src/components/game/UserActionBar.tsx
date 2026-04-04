import { submitCellUpdate } from "@/api/ws/game/playerstate/submitCellUpdate";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { gamePlayerStateCacheDispatcher } from "@/state/game/player/gamePlayerStateCacheDispatcher";
import { CellUpdateValidationError } from "@/state/game/player/gamePlayerStateCacheReducer";
import type { CellCoordinates, PrivateCellState } from "@/types/game/GameTypes";
import { hasNote } from "@/utils/game/noteUtils";
import type { QueryClient } from "@tanstack/react-query";
import { useCallback, type Dispatch, type SetStateAction } from "react";

export function UserActionBar(
    {
        gameId,
        userId,
        initialBoardState,
        playerHighlightedCell,
        highlightedCellState,
        notesModeOn,
        setNotesModeOn,
        queryClient
    }: {
        gameId: number,
        userId: number,
        initialBoardState: string,
        playerHighlightedCell: CellCoordinates | undefined,
        highlightedCellState: PrivateCellState | undefined,
        notesModeOn: boolean,
        setNotesModeOn: Dispatch<SetStateAction<boolean>>
        queryClient: QueryClient
    }
) {
    const numberInputArray: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9];

    const { send } = useWebSocketContext();

    const onNumberInputClick = useCallback((num: number, playerHighlightedCell: CellCoordinates | undefined, notesModeOn: boolean) => {
        if (!playerHighlightedCell) return;
        if (!notesModeOn) {
            try {
                gamePlayerStateCacheDispatcher(queryClient, gameId, userId, {
                    type: "CELL_UPDATE_SUBMITTED",
                    row: playerHighlightedCell.row,
                    col: playerHighlightedCell.col,
                    value: num
                });
            } catch (err) {
                if (err instanceof CellUpdateValidationError) {
                    //console.log("Error trying to update cell value in cache: ", err);
                    return;
                }
                console.error("Issue found when attempting CELL_UPDATE_SUBMITTED: ", err);
                return;
            } 

            // Send out WS message if frontend validation in dispatch method succeeds
            submitCellUpdate(send, gameId, userId, playerHighlightedCell.row, playerHighlightedCell.col, num);
            return;
        } else {
            // IMPLEMENT NOTE SUBMISSION
        }
    }, [gameId, userId, initialBoardState, queryClient, send]);

    return (
        <div 
            className="flex flex-row justify-between 
                        p-1 border-2 rounded-sm border-border bg-primary-foreground"
        >
            {numberInputArray.map((num, index) => {
                const noteActive: boolean = notesModeOn && highlightedCellState !== undefined && hasNote(highlightedCellState.notes, num);
                return (
                    <div 
                        onClick={() => onNumberInputClick(num, playerHighlightedCell, notesModeOn)}
                        className={`flex justify-center p-6 text-4xl hover:bg-sidebar-primary rounded cursor-pointer
                                    ${noteActive && 'bg-sidebar-primary'}`}
                        key={index}    
                    >
                        {num}
                    </div>
                )
            })}
            <div 
                onClick={() => setNotesModeOn(prev => !prev)}
                className="flex justify-center p-6 text-2xl hover:bg-sidebar-primary rounded cursor-pointer">
                {notesModeOn ? "Notes (on)" : "Notes (off)"}
            </div>
            <div
                className="flex justify-center p-6 text-2xl hover:bg-sidebar-primary rounded cursor-pointer">
                Clear
            </div>
        </div>
    )
}