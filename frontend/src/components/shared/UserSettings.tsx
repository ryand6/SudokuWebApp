import type { UserSettingsDto } from "@/types/dto/entity/user/UserSettingsDto";
import { Button } from "../ui/button";
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from "../ui/sheet";
import { Separator } from "../ui/separator";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { sendUserSettingsUpdate } from "@/api/ws/user/sendUserSettingsUpdate";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Field, FieldContent, FieldDescription, FieldLabel, FieldTitle } from "../ui/field";
import { Switch } from "../ui/switch";
import { userCacheDispatcher } from "@/state/user/userCacheDispatcher";
import type { QueryClient } from "@tanstack/react-query";

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

    type BooleanKeys<T> = {
        [K in keyof T]: T[K] extends boolean ? K : never
    }[keyof T]

    type BooleanSettings = BooleanKeys<UserSettingsDto>

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
                <Button variant="outline">Settings</Button>
            </SheetTrigger>
            <SheetContent showCloseButton={true} className="overflow-y-auto">
                <SheetHeader>
                    <SheetTitle>Settings</SheetTitle>
                    <SheetDescription>
                        Configure your user settings here. These will be applied across all your sessions.
                    </SheetDescription>
                </SheetHeader>
                <Separator />
                <div id="user-visual-settings" className="flex flex-col gap-4 px-4">
                    <h2>Visual</h2>
                    <div id="theme-settings">
                        <h3>Theme</h3>
                        <RadioGroup defaultValue={settings.theme} className="grid grid-cols-2 grid-rows-2 gap-2" onValueChange={(value: any) => handleUpdate('theme', value)}>
                            <FieldLabel htmlFor="default-radio">
                                <Field orientation="horizontal" className="cursor-pointer">
                                    <FieldContent>
                                        <FieldTitle>Default</FieldTitle>
                                        <FieldDescription>...Theme 1 description...</FieldDescription>
                                    </FieldContent>
                                    <RadioGroupItem value="DEFAULT" id="classic-radio" />
                                </Field>
                            </FieldLabel>
                            <FieldLabel htmlFor="dark-radio">
                                <Field orientation="horizontal" className="cursor-pointer">
                                    <FieldContent>
                                        <FieldTitle>Dark</FieldTitle>
                                        <FieldDescription>...dark theme description...</FieldDescription>
                                    </FieldContent>
                                    <RadioGroupItem value="DARK" id="dark-radio" />
                                </Field>
                            </FieldLabel>
                            <FieldLabel htmlFor="warm-radio">
                                <Field orientation="horizontal" className="cursor-pointer">
                                    <FieldContent>
                                        <FieldTitle>Warm</FieldTitle>
                                        <FieldDescription>...warm theme description...</FieldDescription>
                                    </FieldContent>
                                    <RadioGroupItem value="WARM" id="warm-radio" />
                                </Field>
                            </FieldLabel>
                            <FieldLabel htmlFor="cool-radio">
                                <Field orientation="horizontal" className="cursor-pointer">
                                    <FieldContent>
                                        <FieldTitle>Cool</FieldTitle>
                                        <FieldDescription>...cool theme description...</FieldDescription>
                                    </FieldContent>
                                    <RadioGroupItem value="COOL" id="cool-radio" />
                                </Field>
                            </FieldLabel>
                        </RadioGroup>
                    </div>
                    {visualSettings.map((setting) => (
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
                    <h2>Notifications</h2>
                    {notificationsSettings.map((setting) => (
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
                    <h2>Audio</h2>
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
                </div>
            </SheetContent>
        </Sheet>
    )
}