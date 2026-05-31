import { useWebSocketContext } from "@/context/WebSocketProvider";
import { useSimulateDisconnect } from "@/hooks/ws/useSimulateDisconnect";


export function WsDevToolbar() {
    const { isConnected, nextReconnectAt } = useWebSocketContext();
    const simulateDisconnect = useSimulateDisconnect();

    return (
        <div className="fixed bottom-4 right-4 z-50 flex items-center gap-3 rounded-lg border bg-background px-4 py-2 text-xs shadow-lg">
            <div className="flex items-center gap-1.5">
                <span className={`h-2 w-2 rounded-full ${isConnected ? "bg-green-500" : "bg-red-500"}`} />
                <span className="text-muted-foreground">
                    {isConnected ? "Connected" : nextReconnectAt ? `Reconnecting in ${Math.max(0, Math.ceil((nextReconnectAt - Date.now()) / 1000))}s` : "Disconnected"}
                </span>
            </div>
            <button
                onClick={simulateDisconnect}
                disabled={!isConnected}
                className="rounded border px-2 py-0.5 text-destructive hover:bg-destructive/10 disabled:cursor-not-allowed disabled:opacity-40"
            >
                Drop connection
            </button>
        </div>
    );
}
