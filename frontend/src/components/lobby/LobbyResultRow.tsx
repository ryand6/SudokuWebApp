import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "../ui/card";
import { Button } from "../ui/button";

export function LobbyResultRow({ lobby, handleClick }: { lobby: LobbyDto, handleClick: (id: number) => void }) {
    return (
        <div className="flex w-full h-[10%] p-2" >
            <Card>
                <CardHeader>
                    <CardTitle>{lobby.lobbyName}</CardTitle>
                    {lobby.inGame && <CardDescription>Lobby is currently in game</CardDescription>}
                </CardHeader>
                <CardContent>
                    <h2>Current Lobby Settings</h2>
                    <div className="flex gap-4">
                        <div className="flex-1 text-center p-2">Difficulty: {lobby.difficulty}</div>
                        <div className="flex-1 text-center p-2">Time limit: {lobby.timeLimit}</div>
                    </div>
                </CardContent>
                <CardFooter>
                    <div className="flex justify-between">
                        <div>{lobby.lobbyPlayers.length} / 4</div>
                        <Button variant="outline" onClick={() => handleClick(lobby.id)} >Join Lobby</Button>
                    </div>
                </CardFooter>
            </Card>
        </div>
    )
}