export function getLocalTime(timestamp: string) {
    return new Date(timestamp).toLocaleTimeString(undefined, {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
    });
    // const hours = date.getHours();
    // const minutes = date.getMinutes();
    // const hh = hours.toString().padStart(2, '0');
    // const mm = minutes.toString().padStart(2, '0');
    // return `${hh}:${mm}`;
}