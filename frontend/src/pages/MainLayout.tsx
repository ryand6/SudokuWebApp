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
		<div className="min-h-screen h-full flex flex-col border-border border-2 md:border-4">
			<header className="bg-secondary h-[8%] text-secondary-foreground px-4 py-3">
				<div className="w-full h-full mx-auto flex items-center justify-between px-5">
					<h1 className="font-bold text-lg md:text-3xl">
						Tomo Sudoku
					</h1>
					{
						user && (
							<UserSettings settings={user.userSettings} queryClient={queryClient} />
						)
					}
				</div>
			</header>

			<main className="h-[92%] md:h-[90%]">
				{
					(!isConnected && user) ? 
						<WebSocketReconnectScreen />
					: 
						<Outlet />
				}
			</main>

			{/* <footer className="bg-footer text-footer-foreground px-4 py-2 text-sm text-center">
				© RD
			</footer> */}

		</div>
	);
}