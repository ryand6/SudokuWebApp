import { submitCellUpdate } from "@/api/ws/game/playerstate/submitCellUpdate";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { gamePlayerStateCacheDispatcher } from "@/state/game/player/gamePlayerStateCacheDispatcher";
import { CellUpdateValidationError, NotesUpdateValidationError } from "@/state/game/player/gamePlayerStateCacheReducer";
import type { PlayerColour } from "@/types/enum/PlayerColour";
import type { CellCoordinates, PrivateCellState } from "@/types/game/GameTypes";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";
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
        playerColours,
        queryClient
    }: {
        gameId: number,
        userId: number,
        initialBoardState: string,
        playerHighlightedCell: CellCoordinates | undefined,
        highlightedCellState: PrivateCellState | undefined,
        notesModeOn: boolean,
        setNotesModeOn: Dispatch<SetStateAction<boolean>>,
        playerColours: Record<number, PlayerColour>,
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
            try {
                gamePlayerStateCacheDispatcher(queryClient, gameId, userId, {
                    type: "NOTE_UPDATE",
                    row: playerHighlightedCell.row,
                    col: playerHighlightedCell.col,
                    note: num
                });
            } catch (err) {
                if (err instanceof NotesUpdateValidationError) {
                    return;
                }
                console.error("Issue found when attempting NOTE_UPDATE: ", err);
                return;
            }
            // IMPLEMENT SUBMIT NOTE UPDATE WS
            return;
        }
    }, [gameId, userId, initialBoardState, queryClient, send]);

    const handleCellClear = useCallback((playerHighlightedCell: CellCoordinates | undefined) => {
        if (!playerHighlightedCell) return;
        gamePlayerStateCacheDispatcher(queryClient, gameId, userId, {
            type: "CELL_CLEAR",
            row: playerHighlightedCell.row,
            col: playerHighlightedCell.col,
        })
        // IMPLEMENT SUBMIT NOTE UPDATE WS (0 VALUE)
    }, [gameId, userId, queryClient, send]); 

    const noteShineClassName = playerColourClassNamePicker[playerColours[userId]].shine + " font-semibold";

    return (
        <div className="flex flex-col h-auto max-h-[300px] w-full border-2 rounded-sm border-border bg-primary-foreground p-4 gap-2">
            <div className="flex justify-evenly">
                <div 
                    onClick={() => setNotesModeOn(prev => !prev)}
                    className="flex justify-center items-center h-full w-[30%] text-lg md:text-xl lg:text-2xl hover:bg-sidebar-primary rounded cursor-pointer elevated">
                    {notesModeOn ? "Notes (on)" : "Notes (off)"}
                </div>
                <div
                    onClick={() => handleCellClear(playerHighlightedCell)}
                    className="flex justify-center items-center h-full w-[30%] text-lg md:text-xl lg:text-2xl hover:bg-sidebar-primary rounded cursor-pointer elevated">
                    Clear
                </div>
            </div>
            <div 
                className="flex flex-row items-center justify-between gap-0.5 h-auto"
            >
                {numberInputArray.map((num, index) => {
                    const noteActive: boolean = notesModeOn && highlightedCellState !== undefined && hasNote(highlightedCellState.notes, num);

                    return (
                        <div 
                            onClick={() => onNumberInputClick(num, playerHighlightedCell, notesModeOn)}
                            className={`flex h-full justify-center items-center w-[10%] py-4 md:py-5 lg:py-6 text-2xl md:3-xl lg:text-4xl 
                                        rounded-2xl cursor-pointer elevated
                                        ${playerColourClassNamePicker[playerColours[userId]].hover}
                                        ${noteActive && noteShineClassName}`}
                            key={index}    
                        >
                            {num}
                        </div>
                    )
                })}
                
            </div>
            
        </div>
    )
}