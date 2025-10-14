import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export function useValidateLobbyId(id: number) {
    const navigate = useNavigate();

    useEffect(() => {
        if (isNaN(id)) {
            toast.error("Lobby does not exist");
            navigate("/dashboard", { replace: true });
        }
    }, [id, navigate]);
}