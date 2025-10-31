// Functions for handling client side retrieval and deletion of session stored lobby chat messages

export function addLobbyMessage(key: string, username: string, message: string) {
    const existingMessages: {user: string, message: string}[] = getLobbyMessages(key);
    const newMessage = {user: username, message: message};
    const updatedMessages = [...existingMessages, newMessage];
    sessionStorage.setItem(key, JSON.stringify(updatedMessages));
    return updatedMessages;
}

export function getLobbyMessages(key: string): {user: string, message: string}[] {
    return JSON.parse(sessionStorage.getItem(key) || "[]");
}

export function removeLobbyMessage(key: string) {
    sessionStorage.removeItem(key);
}