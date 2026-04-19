import type { UserSettingsDto } from "@/types/dto/entity/user/UserSettingsDto";
import { Button } from "../ui/button";
import { Sheet, SheetContent, SheetDescription, SheetHeader, SheetTitle, SheetTrigger } from "../ui/sheet";
import { Separator } from "../ui/separator";
import { useWebSocketContext } from "@/context/WebSocketProvider";
import { sendUserSettingsUpdate } from "@/api/ws/user/sendUserSettingsUpdate";
import { RadioGroup, RadioGroupItem } from "../ui/radio-group";
import { Field, FieldContent, FieldDescription, FieldLabel, FieldTitle } from "../ui/field";
import { Switch } from "../ui/switch";

export function UserSettings({
    settings
}: {
    settings: UserSettingsDto
}) {
    const { send } = useWebSocketContext();

    const handleUpdate = (setting: keyof UserSettingsDto, value: any) => {
        sendUserSettingsUpdate(send, {field: setting, value: value});
    }

    return (
         <Sheet>
            <SheetTrigger asChild>
                <Button variant="outline">Settings</Button>
            </SheetTrigger>
            <SheetContent showCloseButton={true}>
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
                        <RadioGroup defaultValue="DEFAULT" className="grid grid-cols-2 grid-rows-2 gap-2" onValueChange={(value: any) => handleUpdate('theme', value)}>
                            <FieldLabel htmlFor="default-radio">
                                <Field orientation="horizontal" className="cursor-pointer">
                                    <FieldContent>
                                        <FieldTitle>Theme 1</FieldTitle>
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
                    <div id="opponent-highlight-setting">
                        <Field orientation="horizontal" className="max-w-sm">
                            <FieldContent>
                                <FieldLabel htmlFor="opponent-highlight-switch">
                                    Opponent Highlighted Squares
                                </FieldLabel>
                                <FieldDescription>
                                    Show corner highlights on the board to indicate which squares opponents are currently focusing on.
                                </FieldDescription>
                            </FieldContent>
                            <Switch 
                                id="opponent-highlight-switch" 
                                checked={settings.opponentHighlightedSquaresEnabled}
                                onCheckedChange={(checked) => handleUpdate('opponentHighlightedSquaresEnabled', checked)}
                            />
                        </Field>
                    </div>
                </div>
            </SheetContent>
        </Sheet>
    )
}