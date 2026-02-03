import { useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export function useHandleGetGameError(
    isError: boolean,
    error: Error | null
) {
    const navigate = useNavigate();

    useEffect(() => {
        if (isError && error) {
            toast.error(error.message);
            navigate("/dashboard", { replace: true });
        }
    }, [isError, error, navigate]);
}