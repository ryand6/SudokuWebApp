import type { GameResult } from "@/types/enum/GameResult";

export function StatusPill({ result }: { result: GameResult }) {
    if (result === "PENDING") {
        return (
            <div 
                className="inline-flex items-center gap-2 px-4 py-1.5 
                            rounded-full mt-2.5 border border-result-pending-border bg-result-pending"
            >
                <span className="w-2 h-2 rounded-full bg-result-pending-foreground animate-pulse" />
                <span
                    className="font-display text-xs font-medium tracking-wide text-result-pending-foreground"
                >
                    Waiting for results…
                </span>
            </div>
        );
    }
 
    if (result === "WIN") {
        return (
            <div
                className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full 
                            mt-2.5 bg-result-win text-result-win-forefround font-display"
            >
                <span className="text-xs font-medium tracking-wide">🏆 You won!</span>
            </div>
        );
    }
 
    if (result === "DRAW") {
        return (
            <div
                className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full 
                            mt-2.5 border border-result-draw-border bg-result-draw 
                            text-result-draw-foreground font-display"
            >
                <span className="text-xs font-medium tracking-wide">🤝 It's a draw!</span>
            </div>
        );
    }
 
    if (result === "LOSS") {
        return (
            <div
                className="inline-flex items-center gap-2 px-4 py-1.5 rounded-full 
                            mt-2.5 border border-result-loss-border bg-result-loss 
                            text-result-loss-foreground font-display"
            >
                <span className="text-xs font-medium tracking-wide">💢 Defeated</span>
            </div>
        );
    }
    return <></>;
    
}
