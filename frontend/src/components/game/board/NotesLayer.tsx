import type { PlayerColour } from "@/types/enum/PlayerColour";
import { playerColourClassNamePicker } from "@/utils/game/gameColourUtils";
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
    const highlightedNumberClassName = "rounded-full " + playerColourClassNamePicker[playerColour].strong;

    return (
        <div className="absolute inset-0 pointer-events-none m-2 z-10 flex items-center justify-center">
            <div className="grid grid-cols-3 grid-rows-3 w-full h-full">
                {
                    notesAllowed && Array.from([0, 1, 2]).map((row, r) => 
                        Array.from([0, 1, 2]).map((col, c) => {
                            const note = (row * 3) + col + 1;
                            const notePresent = hasNote(notes, note);                            
                            return (
                                <div 
                                    key={`${r}-${c}`}
                                    className={`aspect-square text-xs flex items-center justify-center 
                                                ${!notesModeOn && 'text-gray-500 text-opacity-50'}
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