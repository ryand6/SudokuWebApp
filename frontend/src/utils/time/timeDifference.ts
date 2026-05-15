import { getEpochTimeFromTimestamp } from "./getEpochTimeFromTimestamp";

// Compare timestamp to current time and find difference in minutes
export function computeTimeDifferenceMinutes(timestamp: string): number {
    const now = new Date();
    const diffMs = now.getTime() - getEpochTimeFromTimestamp(timestamp);
    return Math.floor(diffMs / 1000 / 60);
}

export function computeSecondsDifferenceBetweenTimestamps(timestamp1: string, timestamp2: string): number {
    const earlier = timestamp1 < timestamp2 ? timestamp1 : timestamp2;
    const later = timestamp1 > timestamp2 ? timestamp1 : timestamp2;
    const diffMs = getEpochTimeFromTimestamp(later) - getEpochTimeFromTimestamp(earlier);
    return diffMs;
}