import type { CellCoordinates } from "@/types/game/GameTypes";
import type { Dispatch, SetStateAction } from "react";

export function UserActionBar(
    {
        playerHighlightedCell,
        setNotesModeOn
    }: {
        playerHighlightedCell: CellCoordinates | undefined,
        setNotesModeOn: Dispatch<SetStateAction<boolean>>
    }
) {
    const numberInputArray: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9];

    return (
        <div 
            className="flex flex-row justify-between 
                        p-1 border-2 rounded border-border bg-primary-foreground"
        >
            {numberInputArray.map((num) => {
                return (
                    <div className="flex justify-center p-6 text-4xl hover:bg-sidebar-primary rounded cursor-pointer">
                        {num}
                    </div>
                )
            })}
            <div 
                onClick={() => setNotesModeOn(prev => !prev)}
                className="flex justify-center p-6 text-2xl hover:bg-sidebar-primary rounded cursor-pointer">
                Notes
            </div>
            <div
                className="flex justify-center p-6 text-2xl hover:bg-sidebar-primary rounded cursor-pointer">
                Clear
            </div>
        </div>
    )
}