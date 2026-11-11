import { useMutation } from "@tanstack/react-query";
import { logout } from "./logout";

export function useLogout() {
  return useMutation({
    mutationFn: logout,
    onSuccess: async () => {
      // Full page load of the home page, clearing everything held in memory
      window.location.replace("/");
    },
    onError: (err: any) => {
      // Handle any error for display in UI
      console.error("Logout failed:", err?.message || err);
    },
  });
}