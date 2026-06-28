import type { UserDto } from "@/types/dto/entity/user/UserDto"
import { IconArrowNarrowRight, IconPhotoCancel } from "@tabler/icons-react"
import type { NavigateFunction } from "react-router-dom"
import { ClassicModeCard } from "../game/modes/ClassicModeCard"
import { DominationModeCard } from "../game/modes/DominationModeCard"
import { TimeAttackModeCard } from "../game/modes/TimeAttackModeCard"
import { useEffect, useState } from "react"
import { SpinnerButton } from "../ui/custom/SpinnerButton"
import { Spinner } from "../ui/spinner"

export function HomeDesktopLayout({
    user,
    navigate
}: {
    user: UserDto | undefined,
    navigate: NavigateFunction
}) {
    const iconSize: number = 24;

    const [isImageLoading, setIsImageLoading] = useState(true);
    const [isImageError, setIsImageError] = useState(false);

    useEffect(() => {
        const img = new Image();
        img.src = "/images/developer.jpg";

        img.onload = () => {
            setIsImageLoading(false);
        };

        img.onerror = (e) => {
            setIsImageLoading(false);
            setIsImageError(true);
        };
    }, []);

    return (
        <div className="flex flex-col w-full h-full font-display">
            <div className="flex h-[40%] min-h-[300px] w-full bg-card border-b-2 border-muted items-center justify-center gap-10 px-10 py-5">
                <div className="flex flex-col items-start gap-3 max-w-[400px]">
                    <div className="tracking-wider text-xl text-muted-foreground">
                        ONLINE MULTIPLAYER SUDOKU
                    </div>
                    <div className="flex flex-col items-=start font-bold text-foreground text-2xl">
                        <span>Think fast.</span>
                        <span>Play together.</span>
                    </div>
                    <div className="text-muted-foreground text-wrap">
                        Compete against your friends or other opponents from across the world in real-time sudoku battles. Three unique game modes, global leaderboards, and ranked matches.
                    </div>
                </div>
                {
                    user ? (
                        <div className="flex flex-col gap-4 rounded-lg bg-sidebar/90 py-6 px-8">
                            <div className="text-primary-foreground font-semibold tracking-wide text-xl">
                                Welcome back, {user.username}!
                            </div>
                            <div className="text-sidebar-accent">
                                Jump back into the dashboard to start playing, view stats, or check the leaderboards.
                            </div>
                            <div className="flex justify-center">
                                <div 
                                    className="flex items-end justify-center gap-2 rounded-lg border-1 border-primary-foreground px-4 py-2 w-auto
                                                text-primary-foreground cursor-pointer hover:bg-sidebar-accent/30 font-semibold text-lg"
                                    onClick={() => navigate("/dashboard")}
                                >
                                    <span>To Dashboard</span>
                                    <span><IconArrowNarrowRight size={iconSize} /></span>
                                </div>
                            </div>
                        </div>
                    ) : (
                        <div className="flex flex-col gap-4 rounded-lg bg-sidebar/90 py-6 px-8 items-center">
                            <div className="text-primary-foreground font-semibold tracking-wide text-xl">
                                Get started
                            </div>
                            <div className="text-sidebar-accent">
                                Create a free account to start playing matches and track your stats, or log in to your existing account.
                            </div>
                            <div className="flex justify-center gap-3">
                                <div 
                                    className="flex items-center justify-center gap-2 rounded-lg border-1 border-primary-foreground px-4 py-2 w-auto
                                                text-primary-foreground cursor-pointer hover:bg-sidebar-accent/30 font-semibold text-lg"
                                    onClick={() => navigate("/dashboard")}
                                >
                                    <span>Create account</span>
                                </div>
                                <div 
                                    className="flex items-center justify-center gap-2 rounded-lg border-1 border-primary-foreground px-4 py-2 w-auto
                                                text-primary-foreground cursor-pointer hover:bg-sidebar-accent/30 font-semibold text-lg"
                                    onClick={() => navigate("/dashboard")}
                                >
                                    <span>Log in</span>
                                </div>
                            </div>
                        </div>
                    )
                }
            </div>
            <div className="flex w-full h-[30%] min-h-[250px] bg-background py-5 px-10 gap-4 justify-evenly">
                <ClassicModeCard iconSize={iconSize} />
                <DominationModeCard iconSize={iconSize} />
                <TimeAttackModeCard iconSize={iconSize} />
            </div>
            <div className="flex flex-col items-start p-5 px-10 gap-2">
                <div className="tracking-wide font-semibold text-xl text-foreground pb-2">
                    About the Developer
                </div>
                <div className="flex items-center gap-4">
                    <div>
                        {
                            isImageLoading ? (
                                <div className="flex justify-center items-center w-[75px]">
                                    <Spinner />
                                </div>
                                
                            ) : isImageError ? (
                                <div className="flex flex-col justify-center items-center w-[75px]">
                                    <span className="text-wrap text-center text-xs">
                                        No image available 
                                    </span>
                                    <IconPhotoCancel size={iconSize} />
                                </div> 
                            ) : (
                                <img 
                                    src="/images/developer.jpg" 
                                    alt="developer"
                                    className="rounded-full w-[75px] h-[75px]"    
                                />
                            )
                        }
                    </div>
                    <div className="flex flex-col gap-1">
                        <div className="font-semibold text-lg text-foreground">
                            Ryan
                        </div>
                        <div className="text-muted-foreground tracking-wide">
                            Full-stack developer
                        </div>
                        <div className="flex justify-start items-center">
                            <div className="w-auto text-foreground rounded-lg">
                                <a 
                                    href="https://github.com/ryand6"
                                    target="_blank"
                                    className="cursor-pointer"
                                >
                                    <img
                                        src="/images/GitHub_Invertocat_Black_Clearspace.png" 
                                        alt="github"
                                        className="rounded-full w-[30px] h-[30px]" 
                                    />
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="flex w-full text-sm text-muted-foreground">
                    <p>If you have any enquiries or suggestions for the site, please contact me at: <a href="mailto:tomosudoku@gmail.com" className="text-primary underline">tomosudoku@gmail.com</a></p>
                </div>
            </div>
        </div>
    )
}