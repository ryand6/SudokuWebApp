import { useGetCurrentUser } from "@/api/rest/users/query/useGetCurrentUser";
import { UserSettings } from "@/components/global/UserSettings";
import { WebSocketReconnectScreen } from "@/components/global/WebSocketReconnectScreen";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { useQueryClient } from "@tanstack/react-query";
import { Outlet, useNavigate } from "react-router-dom";

export default function MainLayout() {
	const queryClient = useQueryClient();
	const navigate = useNavigate();
	const { isConnected } = useWebSocketContext();
	const { data: user } = useGetCurrentUser();

	if (user) {
		document.documentElement.classList.remove(
			"theme-midnight", "theme-classic", "theme-dusk"
		);
		if (user.userSettings.theme !== "HEARTHSIDE") {
			document.documentElement.classList.add(`theme-${user.userSettings.theme.toLowerCase()}`);
		}
	}

	console.log("User: ", user);

	return (
		<div className="min-h-screen md:h-screen flex flex-col">
			<header className="bg-secondary h-16 text-secondary-foreground px-4 py-3">
				<div className="w-full h-full mx-auto flex items-center justify-between px-5">
					<h1 
						className="font-bold text-3xl cursor-pointer"
						onClick={() => navigate("/")}
					>
						Tomo Sudoku
					</h1>
					{
						user && (
							<UserSettings settings={user.userSettings} queryClient={queryClient} navigate={navigate} />
						)
					}
				</div>
			</header>

			<main className="flex-1 md:min-h-0">
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