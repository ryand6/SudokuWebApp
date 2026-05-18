// Convert timestamp string (e.g. Java Instant type) into epoch ms
export function getEpochTimeFromTimestamp(timestamp: string): number {
    const date = new Date(timestamp);
    return date.getTime();
}