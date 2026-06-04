import { submitCellUpdate } from "@/api/ws/game/playerstate/submitCellUpdate";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { gamePlayerStateCacheDispatcher } from "@/state/game/player/gamePlayerStateCacheDispatcher";
import { CellUpdateValidationError, NotesUpdateValidationError } from "@/state/game/player/gamePlayerStateCacheReducer";
import type { CellCoordinates, PrivateCellState } from "@/types/game/GameTypes";
import { hasNote } from "@/utils/game/noteUtils";
import type { QueryClient } from "@tanstack/react-query";
import { useCallback, type Dispatch, type SetStateAction } from "react";
import { IconPencil, IconEraser } from '@tabler/icons-react';

export function UserActionBar(
    {
        gameId,
        userId,
        initialBoardState,
        playerHighlightedCell,
        highlightedCellState,
        isMobile,
        notesModeOn,
        setNotesModeOn,
        queryClient
    }: {
        gameId: number,
        userId: number,
        initialBoardState: string,
        playerHighlightedCell: CellCoordinates | undefined,
        highlightedCellState: PrivateCellState | undefined,
        isMobile: boolean,
        notesModeOn: boolean,
        setNotesModeOn: Dispatch<SetStateAction<boolean>>,
        queryClient: QueryClient
    }
) {
    const numberInputArray: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9];

    const iconSize: number = isMobile ? 16 : 24;
    const iconStroke: number = isMobile ? 2 : 3;

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

    return (
        <div className="flex flex-col h-auto w-full border-t-2 md:border-t-4 border-border bg-card p-3 gap-3">
            <div className="flex gap-2 justify-evenly items-center">
                <div 
                    onClick={() => setNotesModeOn(prev => !prev)}
                    className={`flex gap-1 justify-center items-center h-full w-full text-xl py-3
                            rounded-md cursor-pointer border-2 font-display font-medium
                            ${notesModeOn ? "border-secondary text-secondary bg-secondary/10 hover:bg-secondary/20" : "border-accent-foreground text-accent-foreground bg-accent-foreground/10 hover:bg-accent-foreground/20"}`}>
                    <span><IconPencil size={iconSize} stroke={iconStroke} /></span>
                    <span>{notesModeOn ? "Notes (on)" : "Notes (off)"}</span>
                </div>
                <div
                    onClick={() => handleCellClear(playerHighlightedCell)}
                    className="flex gap-1 justify-center items-center h-full w-full text-xl font-medium font-display py-3
                    rounded-md cursor-pointer border-2 border-destructive/70 text-destructive/70 bg-destructive/10 hover:bg-destructive/20">
                    <span><IconEraser size={iconSize} stroke={iconStroke} /></span>
                    <span>Clear</span>
                </div>
            </div>
            <div 
                className="flex flex-row items-center justify-between gap-0.5 h-auto pb-3"
            >
                {numberInputArray.map((num, index) => {
                    const noteActive: boolean = notesModeOn && highlightedCellState !== undefined && hasNote(highlightedCellState.notes, num);

                    return (
                        <div 
                            onClick={() => onNumberInputClick(num, playerHighlightedCell, notesModeOn)}
                            className={`flex h-full justify-center items-center w-[10%] font-semibold py-7 text-3xl
                                        rounded-2xl cursor-pointer font-mono border-2 border-muted hover:border-primary hover:text-primary
                                        ${noteActive && "bg-primary/20 text-primary border-primary"}`}
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