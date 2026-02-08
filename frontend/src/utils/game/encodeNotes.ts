import { CELL_COUNT } from "./gameConstants";

/**
 * Encode a 16 bit array into a Base64 encoded string so that data can be safely sent via JSON.
 * @param {Uint16Array} notes - 16 bit array with each element representing a sudoku board cell's bitmask of the active notes
 * @returns {string} - the Base64 encoded string
 */
export function encodeNotes(notes: Uint16Array): string {
    const buffer = new ArrayBuffer(CELL_COUNT * 2);
    const view = new DataView(buffer);

    // Ensure little endian order on byte pairs are enforced in the buffer array before converting to byte array
    for (let i = 0; i < CELL_COUNT; i++) {
        if (notes[i] > 511) {
            throw new Error("Invalid mask value");
        }
        view.setUint16(i * 2, notes[i], true);
    }

    const byteArray = new Uint8Array(buffer);

    // Unpack array, converting each to it's ASCII value and combining one string which is then encoded as Base64
    return btoa(String.fromCharCode(...byteArray));
}