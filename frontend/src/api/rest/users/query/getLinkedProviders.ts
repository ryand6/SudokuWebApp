import { apiFetch } from "@/api/apiFetch";
import type { OAuthProvider } from "@/pages/LinkAdditionalProvidersPage";

export async function getLinkedProviders(): Promise<OAuthProvider[]> {
    const response = await apiFetch("/api/users/get-linked-providers", {
        method: "GET",
        credentials: "include",
        headers: { "Accept": "application/json" }
    });
    return await response.json();
}