export function convertSecondsToMinuteClock(seconds: number): string {
    const clockMinutes = Math.floor(seconds / 60);
    const clockSeconds = seconds % 60;
    const mm = clockMinutes.toString().padStart(2, '0');
    const ss = clockSeconds.toString().padStart(2, '0');
    return `${mm}:${ss}`;
}