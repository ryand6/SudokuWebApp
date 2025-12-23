// Convert timestamp string (e.g. Java Instant type) into epoch ms
export function getEpochTimeFromTimestamp(timestamp: string) {
    const date = new Date(timestamp);
    return date.getTime();
}