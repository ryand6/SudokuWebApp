import { useState } from "react";
import { useCurrentUser } from "../hooks/useCurrentUser";

export function DashboardPage() {

    const { data: currentUser } = useCurrentUser();

    const [isModalOpen, setModalOpen] = useState(false);

    return (
        <div>
            <header>
                <div>
                    Welcome, <span>{currentUser?.username ?? "User"}</span> | Score: <span>{currentUser?.score.totalScore ?? 0}</span> | Rank: <span></span>
                </div>
            </header>
        </div>
    );
}