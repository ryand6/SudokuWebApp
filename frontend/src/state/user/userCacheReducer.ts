import type { UserDto } from "@/types/dto/entity/user/UserDto";
import type { UserEvent } from "./userEvents";
import { applySettingsUpdate } from "@/utils/user/settingsUtils";

export function userCacheReducer(
    existingData: UserDto | undefined,
    event: UserEvent
) {
    switch (event.type) {
        case "USER_UPDATED": {
            return event.userData;
        }
        case "USER_SETTINGS_UPDATED": {
            console.log(`Updating setting ${event.field} to value ${event.value}`);
            if (!existingData) {
                return existingData;
            }
            return {
                ...existingData,
                userSettings: applySettingsUpdate(event.field, event.value, existingData.userSettings)
            };
        }
        case "USER_SETTINGS_FIELD_REJECTED": {
            console.log(`Setting field rejection for field: ${event.field} and value: ${event.value}`);
            return existingData;
        }
        case "USER_SETTINGS_VALUE_REJECTED": {
            console.log(`Setting value rejection for field: ${event.field} and value: ${event.value}`);
            if (!existingData) {
                return existingData;
            }
            return {
                ...existingData,
                userSettings: applySettingsUpdate(event.field, event.value, existingData.userSettings)
            };
        }
    }

}