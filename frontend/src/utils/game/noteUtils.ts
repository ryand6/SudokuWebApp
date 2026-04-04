// Checks if a note is present in bit representation of cell notes
export function hasNote(notes: number, note: number): boolean {
    return ((notes >> (note - 1)) & 1) === 1;
}

export function toggleNote(notes: number, note: number): number {
    return notes ^ (1 << (note - 1));
}

export function clearNote(notes: number, note: number): number {
    return ~(1 << (note - 1)) & notes;
}

export function clearNotes(): number {
    return 0;
}