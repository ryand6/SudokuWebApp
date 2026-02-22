import { useNavigate } from "react-router-dom";
import { UserForm } from "../components/users/UserForm";
import { processUserAmend } from "../api/rest/users/mutate/processUserAmend";
import { useGetCurrentUser } from "../api/rest/users/query/useGetCurrentUser";

export function UserAmendPage() {
  const { data: user } = useGetCurrentUser();
  const navigate = useNavigate();

  async function handleAmend(username: string) {
    await processUserAmend(username);
    navigate("/dashboard", { replace: true });
  }

  return (
    <div className="flex justify-center min-h-screen">
      <div className="flex flex-col w-full max-w-md min-h-screen p-6">
        <h1 className="my-4 font-extrabold tracking-tight text-white">Amend User</h1>
        <label className="font-semibold text-gray-700 text-lg">Current username:</label>
        <div className="border border-gray-400 rounded-lg p-3 mt-4 mb-6 bg-gray-500">{user?.username ?? ""}</div>
        <UserForm
          onSubmit={handleAmend}
          submitLabel="Update Account"
        />
      </div>
    </div>
  );
}