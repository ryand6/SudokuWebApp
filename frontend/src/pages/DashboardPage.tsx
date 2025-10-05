import { useState } from "react";
import { useCurrentUser } from "../hooks/users/useCurrentUser";
import { useUserRank } from "../hooks/users/useUserRank";
import { useTopFivePlayers } from "../hooks/users/useTopFivePlayers";
import { Link } from "react-router-dom";

export function DashboardPage() {

    const { data: currentUser } = useCurrentUser();
    const { data: userRankDto } = useUserRank();
    const { data: topFivePlayersDto } = useTopFivePlayers();

    const [isModalOpen, setModalOpen] = useState(false);

    return (
        <div className="flex flex-col h-screen">
            <header className="bg-[#333] text-white py-[10px] px-[20px] flex justify-between items-center">
                <div>
                    Welcome, <span>{currentUser?.username ?? "User"}</span> | Score: <span>{currentUser?.score.totalScore ?? 0}</span> | Rank: <span>#{userRankDto?.userRank ?? "0"}</span>
                </div>
            </header>
            <div id="dashboard-content" className="flex flex-1">
                <div id="actions-pane" className="p-[15px] m-[15px] bg-[#fff] border-[8px] border-blue-200 rounded-md h-1/3 flex flex-col w-1/4 shadow-[0_2px_5px_rgba(0,0,0,0.1)]">
                    <h2>Lobby Actions</h2>
                    <Link to="/lobby/create-lobby">Create New Lobby</Link>
                    <button>Join Lobby</button>
                </div>
                <div id="mini-leaderboard" className="p-[15px] m-[15px] bg-[#fff] border-[8px] border-blue-200 rounded-md h-1/3 flex flex-col w-1/4 shadow-[0_2px_5px_rgba(0,0,0,0.1)]">
                    <h3>Top 5 Players</h3>
                    <ol>
                        {topFivePlayersDto?.topFivePlayers.map((player, index) => 
                            <li><span>{player.username}</span> | Score: <span>{player.score.totalScore}</span> | Rank: #<span>{index + 1}</span></li>
                        )}
                    </ol>
                </div>

            </div>
        </div>
    );
}