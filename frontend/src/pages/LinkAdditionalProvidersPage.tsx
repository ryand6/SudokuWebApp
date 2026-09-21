import { useEffect, useState } from "react";
import { Button } from "@/components/ui/button";
import { getLinkedProviders } from "@/api/rest/users/query/getLinkedProviders";
import { IconArrowBigRight, IconBrandFacebook, IconBrandGithub, IconBrandGoogle, IconCheck, IconLink } from "@tabler/icons-react";
import { useNavigate } from "react-router-dom";
import { beginProviderLink } from "@/api/rest/users/mutate/beginProviderLink";
import { useIsMobile } from "@/hooks/global/useIsMobile";

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
    const isMobile = useIsMobile();

    const iconSize = isMobile ? 16 : 24;

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

    async function handleLink(provider: OAuthProvider) {
        setLinkingProvider(provider);
        try {
            await beginProviderLink(provider);
            // Start Spring Security's OAuth flow directly.
            window.location.assign(`${import.meta.env.VITE_API_BASE_URL}/oauth2/authorization/${provider}`);
        } catch (err: any) {
            setLinkingProvider(null);
            setError(err);
        }
    }

    if (loading) {
        return (
            <div className="font-display mx-auto max-w-md p-6">
                <p className="text-sm text-muted-foreground">
                    Loading linked providers...
                </p>
            </div>
        );
    }

    if (error) {
        return (
            <div className="font-display mx-auto max-w-md p-6">
                <p className="text-sm text-destructive">
                    {error}
                </p>
            </div>
        );
    }

    return (
        <div className="flex flex-col gap-4 font-display mx-auto max-w-lg p-6">
            <div>
                <h1 className="my-4 text-4xl font-bold tracking-wide text-foreground">
                    Link Additional Providers
                </h1>

                <p className="mt-2 text-md text-muted-foreground">
                    Link additional sign-in providers to your account.
                </p>
            </div>

            <div className="flex flex-col gap-4">
                {providers.map(provider => {
                    const linked = isLinked(provider.id);

                    return (
                        <div
                            key={provider.id}
                            className="flex items-center justify-between rounded-lg border-2 border-muted p-4"
                        >
                            <div className="flex items-center gap-3">
                                <div className="flex h-10 w-10 items-center justify-center rounded-md bg-muted">
                                    {provider.id === "github" && (
                                        <IconBrandGithub size={iconSize} />
                                    )}

                                    {provider.id === "facebook" && (
                                        <IconBrandFacebook size={iconSize} />
                                    )}

                                    {provider.id === "google" && (
                                        <IconBrandGoogle size={iconSize} />
                                    )}
                                </div>

                                <div>
                                    <p className="font-medium">
                                        {provider.name}
                                    </p>

                                    <p className="text-sm text-muted-foreground">
                                        {linked ? "Linked to your account" : "Not linked"}
                                    </p>
                                </div>
                            </div>

                            {linked ? (
                                <div className="flex items-center gap-1 text-sm text-muted-foreground">
                                    <IconCheck size={iconSize} />
                                    Linked
                                </div>
                            ) : (
                                <Button
                                    size="sm"
                                    className="cursor-pointer"
                                    disabled={linkingProvider !== null}
                                    onClick={() => handleLink(provider.id)}
                                >
                                    <IconLink size={iconSize} />
                                    {linkingProvider === provider.id ? "Connecting..." : "Link"}
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