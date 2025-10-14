import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export function useValidatePrivateLobbyToken(token?: string) {
    const navigate = useNavigate();

    useEffect(() => {
        if (!token || !token.trim()) {
            toast.error("Lobby does not exist");
            navigate("/dashboard", { replace: true });
        }
    }, [token, navigate]);
}