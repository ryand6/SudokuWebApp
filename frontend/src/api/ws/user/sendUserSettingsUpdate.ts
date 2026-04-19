import type { SingleFieldPatch } from "@/types/dto/entity/user/SingleFieldPatch";
import type { UserSettingsDto } from "@/types/dto/entity/user/UserSettingsDto";

export function sendUserSettingsUpdate(
    send: (dest: string, body: any) => void,
    singleFieldPatch: SingleFieldPatch<UserSettingsDto>
) {
    send(`/app/user/update-settings`, {field: singleFieldPatch.field, value: singleFieldPatch.value})
}