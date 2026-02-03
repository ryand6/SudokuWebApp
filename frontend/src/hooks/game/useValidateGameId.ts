import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export function useValidateGameId(id: number) {
    const navigate = useNavigate();

    useEffect(() => {
        if (isNaN(id)) {
            toast.error("Game does not exist");
            navigate("/dashboard", { replace: true });
        }
    }, [id, navigate]);
}