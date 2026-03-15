import React from "react";

const SudokuCell = React.memo(function SudokuCell(
    {
        row, 
        col, 
        value, 
        notes, 
        isHighlighted, 
        onClick,
        className
    }: {
        row: number, 
        col: number, 
        value: string | undefined, 
        notes: number, 
        isHighlighted: boolean, 
        onClick: () => void,
        className: string
    }
    ) {
    return (
        <div onClick={onClick}
            className={`w-full h-full flex items-center justify-center 
                        text-xl font-semibold cursor-pointer box-border
                        animate-fill-cell 
                        ${isHighlighted ? "bg-yellow-300" : "bg-primary-foreground"}
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