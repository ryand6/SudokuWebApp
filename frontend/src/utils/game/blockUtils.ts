import type { CellState } from "@/pages/GamePage";
import { GRID_SIZE } from "./gameConstants";

/**
 * Maps a nested array of cell states in grid to an array of blocks, where each element contains all the cell states in the block ordered by grid position
 * @param {CellState[][]} boardState - nested array representing all the cell states in the sudoku board, in grid order
 * @returns {CellState[][]} - the array of blocks, where each element is a nested array containing each of the blocks cell states
 */
export function mapBoardToBlocks(boardState: CellState[][]): CellState[][] {
    const blocks = [];
    // Build nested array of sudoku blocks
    for (let i = 0; i < GRID_SIZE; i += 3) {
        for (let j = 0; j < GRID_SIZE; j += 3) {
            blocks.push(fillBlock(i, j, boardState));
        }
    }
    return blocks;
}

/**
 * Fills a block array with all the relevant cell states
 * @param {number} i - the row index to begin iteration
 * @param {number} j - the col index to begin iteration
 * @param {CellState[][]} boardState - nested array representing all the cell states in the sudoku board, in grid order
 * @returns {CellState[]} - the block array containing all corresponding cell states
 */
function fillBlock(i: number, j: number, boardState: CellState[][]): CellState[] {
    let block: CellState[] = [];
    for (let x = i; x < i + 3; x++) {
        for (let y = j; y < j + 3; y++) {
            block.push(boardState[x][y]);
        }
    }
    return block;
}

