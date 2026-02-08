import { CELL_COUNT } from "./gameConstants";

export function decodeNotes(base64Notes: string): Uint16Array {
    const binaryString: string = atob(base64Notes);

    if (binaryString.length !== (CELL_COUNT * 2)) {
        throw new Error(`Invalid notes length: expected ${CELL_COUNT * 2} bytes, got ${binaryString.length}`);
    }

    // Store data in byte array first as this reflects the backend data structure (2 bytes used per each cell)
    const byteArray: Uint8Array = new Uint8Array(binaryString.length);
    
    for (let i = 0; i < binaryString.length; i++) {
        byteArray[i] = binaryString.charCodeAt(i);
    }

    // Use a data view to specify endianness when mapping byte array to bitmask array - required as integers span 2 bytes, so endianness must be uniform, not platform specific
    const dataView = new DataView(byteArray.buffer);

    // Convert data to 16 bit array for easy bitwise operations, where each element is the bitmask of the corresponding cell's notes
    const bitmaskArray = new Uint16Array(CELL_COUNT);

    for (let i = 0; i < bitmaskArray.length; i++) {
        // Specify little endianness when transforming byte pair into 16 bit array
        bitmaskArray[i] = dataView.getUint16(i * 2, true);
        if (bitmaskArray[i] > 511) {
            throw new Error("Invalid mask value");
        }
    }

    return bitmaskArray;
}