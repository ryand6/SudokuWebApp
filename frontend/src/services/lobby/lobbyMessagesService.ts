// Functions for handling client side retrieval and deletion of session stored lobby chat messages

export function updateLobbyMessages(lobbyId: number, updatedMessages: {username: string, message: string}[]) {
    const sessionStorageKey = `lobbyChat${lobbyId}`;
    // const existingMessages: {user: string, message: string}[] = getLobbyMessages(lobbyId);
    // const newMessage = {user: username, message: message};
    // let updatedMessages = [...existingMessages, newMessage];
    // // Maximum of 100 messages allowed in a lobby chat at any one time
    // if (updatedMessages.length > 100) {
    //     updatedMessages = updatedMessages.slice(updatedMessages.length - 100);
    // }
    sessionStorage.setItem(sessionStorageKey, JSON.stringify(updatedMessages));
    return updatedMessages;
}

export function getLobbyMessages(lobbyId: number): {username: string, message: string}[] {
    const sessionStorageKey = `lobbyChat${lobbyId}`;
    return JSON.parse(sessionStorage.getItem(sessionStorageKey) || "[]");
}

export function removeLobbyMessage(lobbyId: number) {
    const sessionStorageKey = `lobbyChat${lobbyId}`;
    sessionStorage.removeItem(sessionStorageKey);
}