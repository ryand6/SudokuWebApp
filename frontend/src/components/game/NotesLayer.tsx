import type { PlayerColour } from "@/types/enum/PlayerColour";
import { getCellIndex, playerColourClassNamePicker } from "@/utils/game/cellUtils";
import { hasNote } from "@/utils/game/noteUtils";
import React from "react";

const NotesLayer = React.memo(function NotesLayer(
    {
        row, 
        col, 
        value, 
        notes,
        userId,
        isRejected,
        notesModeOn,
        highlightedCellNumber,
        playerColour
    }: {
        row: number, 
        col: number, 
        value: string | undefined, 
        notes: number,
        userId: number,
        isRejected: boolean,
        notesModeOn: boolean,
        highlightedCellNumber: string | undefined,
        playerColour: PlayerColour
    }
) {
    const notesAllowed: boolean = !value || isRejected;
    const highlightedNumberClassName = "rounded-b-full m-1" + playerColourClassNamePicker[playerColour].light;

    return (
        <div className="fixed pointer-events-none m-2 z-10">
            <div className="grid grid-cols-3 grid-rows-3 w-auto h-auto">
                {
                    notesAllowed && Array.from([0, 1, 2]).map((row, r) => 
                        Array.from([0, 1, 2]).map((col, c) => {
                            const note = getCellIndex(row, col);
                            const notePresent = hasNote(notes, note);
                            
                            return (
                                <div 
                                    key={`${r}-${c}`}
                                    className={`${!notesModeOn && 'text-gray-500 text-opacity-50'}
                                                ${(Number(highlightedCellNumber) === note) && notePresent && highlightedNumberClassName}`}
                                >
                                    { notePresent && note }
                                </div>
                            )
                        })
                    )
                }
            </div>
        </div>
    )
})

export default NotesLayer;