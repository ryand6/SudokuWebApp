import { getEpochTimeFromTimestamp } from "./getEpochTimeFromTimestamp";

// Compare timestamp to current time and find difference in minutes
export function computeTimeDifferenceMinutes(timestamp: string): number {
    const now = new Date();
    const diffMs = now.getTime() - getEpochTimeFromTimestamp(timestamp);
    return Math.floor(diffMs / 1000 / 60);
}

export function computeMsDifferenceBetweenTimestamps(timestamp1: string, timestamp2: string): number {
    const earlier = timestamp1 < timestamp2 ? timestamp1 : timestamp2;
    const later = timestamp1 > timestamp2 ? timestamp1 : timestamp2;
    const diffMs = getEpochTimeFromTimestamp(later) - getEpochTimeFromTimestamp(earlier);
    return diffMs;
}

export function computeSecondsLeftUntilTimestamp(timestamp: string | null): number {
    if (timestamp === null) return -1;
    const diffMs = getEpochTimeFromTimestamp(timestamp) - Date.now();

    console.log("DiffMs: ", diffMs);
    console.log("DiffSecs: ", Math.floor(diffMs / 1000));

    return Math.floor(diffMs / 1000);
}