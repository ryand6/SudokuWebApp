import type { LobbyDto } from "@/types/dto/entity/LobbyDto";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "../ui/card";
import { Button } from "../ui/button";
import { isCurrentUserInLobby } from "@/utils/lobby/isCurrentUserInLobby";
import type { UserDto } from "@/types/dto/entity/UserDto";
import { computeTimeDifferenceMinutes } from "@/utils/time/computeTimeDifferenceMinutes";
import { wordToProperCase } from "@/utils/string/wordToProperCase";

export function LobbyResultRow({ lobby, currentUser, handleClick }: { lobby: LobbyDto, currentUser: UserDto, handleClick: (id: number) => void }) {
    return (
        <div className="flex w-full p-2" >
            <Card className="w-full gap-2 p-2 text-sm hover:ring-4 hover:ring-ring">
                <CardHeader>
                    <CardTitle className="font-bold text-xl">{lobby.lobbyName}</CardTitle>
                    {lobby.inGame && <CardDescription>Lobby is currently in game</CardDescription>}
                </CardHeader>
                <CardContent>
                    <div className="flex gap-4 text-xs sm:text-sm md:text-base">
                        <div className="flex-1 text-left">Difficulty: {wordToProperCase(lobby.difficulty)}</div>
                        <div className="flex-1 text-left">Time limit: {wordToProperCase(lobby.timeLimit)}</div>
                        <div className="flex-1 text-left">Host: {lobby.host.username}</div>
                        <div className="flex-1 text-left">Created {computeTimeDifferenceMinutes(lobby.createdAt)} minutes ago</div>
                    </div>
                </CardContent>
                <CardFooter className="items-stretch">
                    <div className="flex w-full text-xs sm:text-sm md:text-base">
                        <div className="flex-1">{lobby.lobbyPlayers.length} / 4 Players</div>
                        {!isCurrentUserInLobby(lobby, currentUser) && <Button className="justify-self-end cursor-pointer bg-sidebar-primary" onClick={() => handleClick(lobby.id)} >Join Lobby</Button>}
                    </div>
                </CardFooter>
            </Card>
        </div>
    )
}