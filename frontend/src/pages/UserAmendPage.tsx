import { useNavigate } from "react-router-dom";
import { getAuthContext } from "../auth/AuthContextProvider";
import { UserForm } from "../components/UserForm";
import { processUserAmend } from "../api/user/processUserAmend";

export function UserAmendPage() {
  const { user, refreshUserAuth } = getAuthContext();
  const navigate = useNavigate();

  async function handleAmend(username: string) {
    await processUserAmend(username);
    await refreshUserAuth();
    navigate("/dashboard", { replace: true });
  }

  return (
    <div>
      <h1>Amend User</h1>
      <label>Current username:</label>
      <div>{user?.username ?? ""}</div>
      <UserForm
        onSubmit={handleAmend}
        submitLabel="Update Account"
      />
    </div>
  );
}