import { useMutation, useQueryClient } from "@tanstack/react-query";
import { userLogout } from "../api/rest/users/userLogout";
import { useNavigate } from "react-router-dom";

export function useLogout() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: userLogout,
    onSuccess: () => {
      // Invalidate the "currentUser" query so the cache is cleared
      queryClient.setQueryData(["currentUser"], null);
      // redirect to home or login page
      navigate("/", { replace: true });
    },
    onError: (err: any) => {
      // Handle any error for display in UI
      console.error("Logout failed:", err?.message || err);
    },
  });
}