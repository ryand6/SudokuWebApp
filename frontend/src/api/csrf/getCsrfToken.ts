// Retrieves xor encoded csrf token from backend, for use in requests that require this such as websocket connections
export async function getCsrfToken(): Promise<{ parameterName: string, token: string, headerName: string }> {
    const response = await fetch("/csrf", { 
        credentials: "include" 
    });
    const data = await response.json();
    return data;
}