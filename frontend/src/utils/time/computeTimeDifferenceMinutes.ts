// Compare timestamp to current time and find difference in minutes
export function computeTimeDifferenceMinutes(timestamp: string): number {
    const compareDate = new Date(timestamp);
    const now = new Date();
    const diffMs = now.getTime() - compareDate.getTime();
    return Math.floor(diffMs / 1000 / 60);
}