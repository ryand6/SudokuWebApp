import { useMutation, useQueryClient } from "@tanstack/react-query";
import { logout } from "./logout";
import { useNavigate } from "react-router-dom";
import { queryKeys } from "@/state/queryKeys";

export function useLogout() {
  const queryClient = useQueryClient();
  const navigate = useNavigate();

  return useMutation({
    mutationFn: logout,
    onSuccess: () => {
      // Invalidate the "currentUser" query so the cache is cleared
      queryClient.setQueryData(queryKeys.user, null);
      // redirect to home or login page
      navigate("/", { replace: true });
    },
    onError: (err: any) => {
      // Handle any error for display in UI
      console.error("Logout failed:", err?.message || err);
    },
  });
}