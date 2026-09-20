import { useEffect, useState } from "react";
import { Check, Github, Facebook, Link2 } from "lucide-react";

import { Button } from "@/components/ui/button";
import { getLinkedProviders } from "@/api/rest/users/query/getLinkedProviders";
import { IconArrowBigRight } from "@tabler/icons-react";
import { useNavigate } from "react-router-dom";

export type OAuthProvider = "github" | "facebook" | "google";

const providers: {
    id: OAuthProvider;
    name: string;
}[] = [
    {
        id: "github",
        name: "GitHub",
    },
    {
        id: "facebook",
        name: "Facebook",
    },
    {
        id: "google",
        name: "Google",
    },
];

export function LinkAdditionalProvidersPage() {
    const [linkedProviders, setLinkedProviders] = useState<OAuthProvider[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [linkingProvider, setLinkingProvider] = useState<OAuthProvider | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        async function loadProviders() {
            try {
                const linked = await getLinkedProviders();
                setLinkedProviders(linked);
            } catch {
                setError("Unable to load your linked providers.");
            } finally {
                setLoading(false);
            }
        }

        loadProviders();
    }, []);

    function isLinked(provider: OAuthProvider) {
        return linkedProviders.includes(provider);
    }

    function handleLink(provider: OAuthProvider) {
        setLinkingProvider(provider);
        // Start Spring Security's OAuth flow directly.
        window.location.assign(`${import.meta.env.VITE_API_BASE_URL}/oauth2/authorization/${provider}`);
    }

    if (loading) {
        return (
            <div className="mx-auto max-w-md p-6">
                <p className="text-sm text-muted-foreground">
                    Loading linked providers...
                </p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="mx-auto max-w-md p-6">
                <p className="text-sm text-destructive">
                    {error}
                </p>
            </div>
        );
    }

    return (
        <div className="mx-auto max-w-md space-y-6 p-6">
            <div>
                <h1 className="text-2xl font-semibold">
                    Link Additional Providers
                </h1>

                <p className="mt-2 text-sm text-muted-foreground">
                    Link additional sign-in providers to your account.
                </p>
            </div>

            <div className="space-y-3">
                {providers.map(provider => {
                    const linked = isLinked(provider.id);

                    return (
                        <div
                            key={provider.id}
                            className="flex items-center justify-between rounded-lg border p-4"
                        >
                            <div className="flex items-center gap-3">
                                <div className="flex h-10 w-10 items-center justify-center rounded-md bg-muted">
                                    {provider.id === "github" && (
                                        <Github className="h-5 w-5" />
                                    )}

                                    {provider.id === "facebook" && (
                                        <Facebook className="h-5 w-5" />
                                    )}

                                    {provider.id === "google" && (
                                        <span className="text-lg font-bold">
                                            G
                                        </span>
                                    )}
                                </div>

                                <div>
                                    <p className="font-medium">
                                        {provider.name}
                                    </p>

                                    <p className="text-sm text-muted-foreground">
                                        {linked
                                            ? "Linked to your account"
                                            : "Not linked"}
                                    </p>
                                </div>
                            </div>

                            {linked ? (
                                <div className="flex items-center gap-1 text-sm text-muted-foreground">
                                    <Check className="h-4 w-4" />
                                    Linked
                                </div>
                            ) : (
                                <Button
                                    size="sm"
                                    disabled={linkingProvider !== null}
                                    onClick={() => handleLink(provider.id)}
                                >
                                    <Link2 className="mr-2 h-4 w-4" />
                                    {linkingProvider === provider.id
                                        ? "Connecting..."
                                        : "Link"}
                                </Button>
                            )}
                        </div>
                    );
                })}
            </div>
            <div className="flex justify-end items-center">
                <Button
                    variant="outline"
                    className="border-muted-foreground text-muted-foreground font-semibold py-2 cursor-pointer"
                    onClick={() => navigate("/dashboard", {replace: true})}
                >
                    Go to Dashboard
                    <IconArrowBigRight />
                </Button>
            </div>
                        
        </div>
    );
}