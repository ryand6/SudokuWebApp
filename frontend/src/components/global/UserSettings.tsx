import type { UserSettingsDto } from "@/types/dto/entity/user/UserSettingsDto";
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from "../ui/sheet";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { sendUserSettingsUpdate } from "@/api/ws/user/sendUserSettingsUpdate";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Field, FieldContent, FieldDescription, FieldLabel, FieldTitle } from "../ui/field";
import { Switch } from "../ui/switch";
import { userCacheDispatcher } from "@/state/user/userCacheDispatcher";
import type { QueryClient } from "@tanstack/react-query";
import { IconSettings } from '@tabler/icons-react';
import { useIsMobile } from "@/hooks/global/useIsMobile";

export function UserSettings({
    settings,
    queryClient
}: {
    settings: UserSettingsDto,
    queryClient: QueryClient
}) {
    const { send } = useWebSocketContext();

    const handleUpdate = (setting: keyof UserSettingsDto, value: any) => {
        console.log(`Updating setting ${setting} to value ${value}`);
        userCacheDispatcher(queryClient, {type: "USER_SETTINGS_UPDATED", field: setting, value: value});
        sendUserSettingsUpdate(send, {field: setting, value: value});
    }

    const isMobile = useIsMobile();
    const iconSize: number = isMobile ? 12 : 24;

    type BooleanKeys<T> = {
        [K in keyof T]: T[K] extends boolean ? K : never
    }[keyof T]

    type BooleanSettings = BooleanKeys<UserSettingsDto>

    const themeOptions = [
        {value: 'HEARTHSIDE', label: 'Hearthside', colours: ['#964000', '#5C6941', '#3D2E1E', '#D6CBAF']},
        {value: 'MIDNIGHT', label: 'Midnight', colours: ['#6C63FF', '#00BFA5', '#0B0D18', '#2A2D3E']},
        {value: 'CLASSIC', label: 'Classic', colours: ['#3A6EA8', '#4A5A7A', '#1E2E45', '#D0DCE8']},
        {value: 'DUSK', label: 'Dusk', colours: ['#B85880', '#5A8A72', '#2E1E40', '#D8D0E8']}
    ];
    const visualSettings: { field: BooleanSettings; label: string; description: string }[] = [
        {field: 'opponentHighlightedSquaresEnabled', label: 'Opponent Highlighted Squares', description: 'Show corner highlights on the board to indicate which squares opponents are currently focusing on.'},
        {field: 'highlightedHousesEnabled', label: 'Highlight Houses', description: 'Highlight squares that are in the same row, column, or box as the currently highlighted square.'},
        {field: 'highlightedFirstsEnabled', label: 'Highlight Firsts', description: 'Highlight squares with the colour of the player that was first to fill them without making a mistake.'}
    ];

    const notificationsSettings: { field: BooleanSettings; label: string; description: string }[] = [
        {field: 'gameChatNotificationsEnabled', label: 'Game Chat Notifications', description: 'Receive notifications for game chat messages.'},
        {field: 'scoreNotificationsEnabled', label: 'Score Notifications', description: 'Receive notifications for your score updates.'},
        {field: 'streakNotificationsEnabled', label: 'Streak Notifications', description: 'Receive notifications for your streak updates.'}
    ];

    const audioSettings: { field: BooleanSettings; label: string; description: string }[] = [
        {field: 'audioEnabled', label: 'Audio', description: 'Enable or disable audio.'},
    ];

    return (
         <Sheet>
            <SheetTrigger asChild>
                <div 
                    className="flex items-center justify-center p-1.5 rounded-lg border-1
                            bg-secondary-foreground/10 border-secondary-foreground/50 text-secondary-foreground cursor-pointer
                            hover:bg-primary/10"

                    >
                    <IconSettings size={iconSize} />
                </div>
            </SheetTrigger>
            <SheetContent 
                showCloseButton={true} 
                className="font-display [&>button]:p-1 [&>button]:bg-muted/20 [&>button]:text-muted [&>button]:hover:bg-muted/50 [&>button]:border-1 [&>button]:border-muted [&>button]:cursor-pointer"
            >
                <SheetHeader className="bg-sidebar">
                    <SheetTitle className="text-sidebar-foreground text-xl tracking-wide">Settings</SheetTitle>
                    <SheetDescription>
                        Applied across all your sessions.
                    </SheetDescription>
                </SheetHeader>
                <div id="user-settings-content" className="overflow-y-auto">
                    <div id="user-account-settings" className="flex flex-col gap-4 px-4 pb-4 border-b-2 border-muted">
                        <h2 className="tracking-widest text-muted-foreground">ACCOUNT</h2>
                    </div>
                    <div id="theme-settings" className="flex flex-col gap-4 px-4 py-4 border-b-2 border-muted">
                        <h2 className="tracking-widest text-muted-foreground">THEME</h2>
                        <RadioGroup value={settings.theme} className="grid grid-cols-2 grid-rows-2 gap-2" onValueChange={(value: any) => handleUpdate('theme', value)}>
                            {themeOptions.map((theme) => (
                                <div key={theme.value}>
                                    <RadioGroupItem value={theme.value} id={`theme-${theme.value}`} className="peer sr-only" />
                                    <label 
                                        htmlFor={`theme-${theme.value}`}
                                        className="block cursor-pointer rounded-lg border-3 border-muted hover:border-primary/50
                                                peer-data-[state=checked]:border-primary overflow-hidden"
                                    >
                                        <div className="grid grid-cols-2 grid-rows-2 aspect-[2/1]">
                                            {theme.colours.map((colour, index) => (
                                                <div key={index} className="w-full h-full" style={{ backgroundColor: colour }} />
                                            ))}
                                        </div>
                                        <div className="flex items-center justify-center gap-1 border-t border-muted bg-background px-2 py-1.5">
                                            <span className="text-xs font-semibold">
                                                {theme.label}
                                            </span>
                                            {settings.theme === theme.value && (
                                                <span className="text-primary text-xs font-bold">
                                                    ✓
                                                </span>
                                            )}
                                        </div>
                                    </label>
                                </div>
                            ))}
                        </RadioGroup>
                    </div>
                    <div id="user-visual-settings" className="flex flex-col gap-4 px-4 py-4 border-b-2 border-muted">
                        <h2 className="tracking-widest text-muted-foreground">VISUAL</h2>
                        {visualSettings.map((setting) => (
                            <div id={`${setting.field}-setting`} key={setting.field}>
                                <Field orientation="horizontal" className="max-w-sm">
                                    <FieldContent>
                                        <FieldLabel htmlFor={`${setting.field}-switch`} className="text-md">
                                            {setting.label}
                                        </FieldLabel>
                                        <FieldDescription>
                                            {setting.description}
                                        </FieldDescription>
                                    </FieldContent>
                                    <Switch 
                                        id={`${setting.field}-switch`} 
                                        checked={settings[setting.field]}
                                        onCheckedChange={(checked) => handleUpdate(setting.field, checked)}
                                    />
                                </Field>
                            </div>
                        ))}
                    </div>
                    <div id="user-notifications-settings" className="flex flex-col gap-4 px-4 py-4">
                        <h2 className="tracking-widest text-muted-foreground">NOTIFICATIONS</h2>
                        {notificationsSettings.map((setting) => (
                            <div id={`${setting.field}-setting`} key={setting.field}>
                                <Field orientation="horizontal" className="max-w-sm">
                                    <FieldContent>
                                        <FieldLabel htmlFor={`${setting.field}-switch`} className="text-md" >
                                            {setting.label}
                                        </FieldLabel>   
                                        <FieldDescription>
                                            {setting.description}
                                        </FieldDescription> 
                                    </FieldContent>
                                    <Switch 
                                        id={`${setting.field}-switch`} 
                                        checked={settings[setting.field]}
                                        onCheckedChange={(checked) => handleUpdate(setting.field, checked)}
                                    />
                                </Field>
                            </div>
                        ))}
                    </div>
                    {/* <div id="user-audio-settings" className="flex flex-col gap-4 px-4 pt-4">
                        <h2 className="tracking-widest text-muted-foreground">AUDIO</h2>
                        {audioSettings.map((setting) => (
                            <div id={`${setting.field}-setting`} key={setting.field}>
                                <Field orientation="horizontal" className="max-w-sm">           
                                    <FieldContent>
                                        <FieldLabel htmlFor={`${setting.field}-switch`}>
                                            {setting.label}
                                        </FieldLabel>
                                        <FieldDescription>
                                            {setting.description}
                                        </FieldDescription>
                                    </FieldContent>
                                    <Switch
                                        id={`${setting.field}-switch`}
                                        checked={settings[setting.field]}
                                        onCheckedChange={(checked) => handleUpdate(setting.field, checked)} 
                                    />
                                </Field>
                            </div>
                        ))}
                    </div> */}
                </div>
            </SheetContent>            
        </Sheet>
    )
}