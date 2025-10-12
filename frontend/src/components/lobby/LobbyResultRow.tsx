import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "../ui/card";
import { Button } from "../ui/button";

export function LobbyResultRow({ lobby, handleClick }: { lobby: LobbyDto, handleClick: (id: number) => void }) {
    return (
        <div className="flex w-full p-2" >
            <Card className="w-full gap-2">
                <CardHeader>
                    <CardTitle className="font-bold text-xl">{lobby.lobbyName}</CardTitle>
                    {lobby.inGame && <CardDescription>Lobby is currently in game</CardDescription>}
                </CardHeader>
                <CardContent>
                    <h3>Current Lobby Settings</h3>
                    <div className="flex gap-4">
                        <div className="flex-1 text-left">Difficulty: {lobby.difficulty?.toLowerCase()}</div>
                        <div className="flex-1 text-left">Time limit: {lobby.timeLimit.toLowerCase()}</div>
                    </div>
                </CardContent>
                <CardFooter>
                    <div className="flex justify-between items-end">
                        <div className="flex-1">{lobby.lobbyPlayers.length} / 4 Players</div>
                        <Button variant="outline" className="flex-1" onClick={() => handleClick(lobby.id)} >Join Lobby</Button>
                    </div>
                </CardFooter>
            </Card>
        </div>
    )
}