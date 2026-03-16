import type { PlayerColour } from "@/types/enum/PlayerColour";
import React from "react";

const SudokuCell = React.memo(function SudokuCell(
    {
        row, 
        col, 
        value, 
        notes, 
        playerColour,
        isSelected,
        isInRow,
        isInCol,
        isInBlock,
        isSameNumber,
        opponentsSelected,
        onSelect,
        className
    }: {
        row: number, 
        col: number, 
        value: string | undefined, 
        notes: number, 
        playerColour: PlayerColour,
        isSelected: boolean, 
        isInRow: boolean,
        isInCol: boolean,
        isInBlock: boolean,
        isSameNumber: boolean,
        opponentsSelected: PlayerColour[] | undefined
        onSelect: () => void,
        className: string
    }
    ) {
    return (
        <div onClick={onSelect}
            className={`w-full h-full flex items-center justify-center 
                        text-xl font-semibold cursor-pointer box-border
                        animate-fill-cell 
                        ${isSelected ? "bg-yellow-300" : "bg-primary-foreground"}
                        ${className}`}
            style={{ animationDelay: `${((row * 3) + col) * 15}ms`}}
        >
            <span>
                {value ? value : null}
            </span>
        </div>
    )
})

export default SudokuCell;