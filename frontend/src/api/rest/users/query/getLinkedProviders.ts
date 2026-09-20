import type { OAuthProvider } from "@/pages/LinkAdditionalProvidersPage";

export async function getLinkedProviders(): Promise<OAuthProvider[]> {
    const response = await fetch("/api/users/get-linked-providers", {
        method: "GET",
        credentials: "include",
        headers: { "Accept": "application/json" }
    });
    return await response.json();
}