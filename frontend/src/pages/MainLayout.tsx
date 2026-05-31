import { useGetCurrentUser } from "@/api/rest/users/query/useGetCurrentUser";
import { UserSettings } from "@/components/global/UserSettings";
import { WebSocketReconnectScreen } from "@/components/global/WebSocketReconnectScreen";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { useQueryClient } from "@tanstack/react-query";
import { Outlet } from "react-router-dom";

export default function MainLayout() {
	const queryClient = useQueryClient();
	const { isConnected } = useWebSocketContext();
	const { data: user } = useGetCurrentUser();

	return (
		<div className="min-h-screen flex flex-col">
		<header className="bg-header text-header-foreground px-4 py-3">
			<div className="w-full mx-auto flex items-center justify-between px-5">
				<h1 className="font-bold text-lg">
					Tomo Sudoku
				</h1>
				{
					user && (
						<UserSettings settings={user.userSettings} queryClient={queryClient} />
					)
				}
			</div>
		</header>

		<main className="flex-1">
			{
				isConnected ? 
					<Outlet />
				: 
					<WebSocketReconnectScreen />
			}
		</main>

		<footer className="bg-footer text-footer-foreground px-4 py-2 text-sm text-center">
			© RD
		</footer>

		</div>
	);
}