import { getEpochTimeFromTimestamp } from "./getEpochTimeFromTimestamp";

// Compare timestamp to current time and find difference in minutes
export function computeTimeDifferenceMinutes(timestamp: string): number {
    const now = new Date();
    const diffMs = now.getTime() - getEpochTimeFromTimestamp(timestamp);
    return Math.floor(diffMs / 1000 / 60);
}