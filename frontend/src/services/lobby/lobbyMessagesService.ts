// Functions for handling client side retrieval and deletion of session stored lobby chat messages

export function addLobbyMessage(key: string, message: string) {
    const existingMessages: string[] = getLobbyMessages(key);
    const updatedMessages = [...existingMessages, message];
    sessionStorage.setItem(key, JSON.stringify(updatedMessages));
    return updatedMessages;
}

export function getLobbyMessages(key: string): string[] {
    return JSON.parse(sessionStorage.getItem(key) || "[]");
}

export function removeLobbyMessage(key: string) {
    sessionStorage.removeItem(key);
}