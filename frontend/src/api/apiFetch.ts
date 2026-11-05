// Address of the backend. Empty when running locally, so requests stay as short paths
// and Vite's dev proxy keeps working. In a deployed build it comes from VITE_API_BASE_URL,
// which is set separately for staging and production in Vercel.
const API_BASE_URL: string = import.meta.env.PROD
    ? (import.meta.env.VITE_API_BASE_URL ?? "")
    : "";

export function apiUrl(path: string): string {
    return `${API_BASE_URL}${path}`;
}

// adds the backend address to the front of the request path
export function apiFetch(path: string, init?: RequestInit): Promise<Response> {
    return fetch(apiUrl(path), init);
}

export function backendUrl(path: string): string {
    return `${import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080"}${path}`;
}