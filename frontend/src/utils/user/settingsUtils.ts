import type { UserSettingsDto } from "@/types/dto/entity/user/UserSettingsDto";

export function applySettingsUpdate<K extends keyof UserSettingsDto>(
    key: K,
    value: UserSettingsDto[K], 
    settings: UserSettingsDto
): UserSettingsDto {
    if (!isValidSettingKey(key, settings)) {
        console.error(`Invalid setting key: ${key}`);
        return settings;
    }
    return {
        ...settings,
        [key]: value
    };
}

function isValidSettingKey(
    key: string, 
    settings: UserSettingsDto
): key is keyof UserSettingsDto {
    return key in settings;
}