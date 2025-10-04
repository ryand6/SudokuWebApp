import { useState } from "react";
import { useCurrentUser } from "../hooks/useCurrentUser";
import { useUserRank } from "../hooks/useUserRank";

export function DashboardPage() {

    const { data: currentUser } = useCurrentUser();
    const { data: userRankDto } = useUserRank();

    const [isModalOpen, setModalOpen] = useState(false);

    return (
        <div>
            <header className="bg-[#333] text-white py-[10px] px-[20px] flex justify-between items-center">
                <div>
                    Welcome, <span>{currentUser?.username ?? "User"}</span> | Score: <span>{currentUser?.score.totalScore ?? 0}</span> | Rank: <span>#{userRankDto?.userRank ?? "0"}</span>
                </div>
            </header>
            <div id="dashboard-content">
                <div id="mini-leaderboard">
                    <h3>Top 5 Players</h3>
                    <ol>
                        
                        <li></li>
                    </ol>
                </div>

            </div>
        </div>
    );
}