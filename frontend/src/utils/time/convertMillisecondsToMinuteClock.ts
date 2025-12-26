export function convertMillisecondsToMinuteClock(milliseconds: number): string {
    const clockMilliseconds = milliseconds % 1000;
    const clockSeconds = Math.floor(milliseconds / 1000);
    const clockMinutes = Math.floor(clockSeconds / 60);
    const mm = clockMinutes.toString().padStart(2, '0');
    const ss = clockSeconds.toString().padStart(2, '0');
    const ms = clockMilliseconds.toString().padStart(3, '0');
    return `${mm}:${ss}:${ms}`;
}