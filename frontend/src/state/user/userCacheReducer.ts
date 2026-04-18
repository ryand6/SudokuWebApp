import type { UserDto } from "@/types/dto/entity/user/UserDto";
import type { UserEvent } from "./userEvents";

export function userCacheReducer(
    existingData: UserDto | undefined,
    event: UserEvent
) {
    switch (event.type) {
        case "USER_UPDATED": {
            return event.userData;
        }
        case "USER_SETTINGS_UPDATED": {
            if (!existingData) {
                return existingData;
            }
            return {
                ...existingData,
                [event.field]: event.value
            };
        }
        case "USER_SETTINGS_FIELD_REJECTED": {
            console.error(`Setting update rejected for field: ${event.field}`);
            return existingData;
        }
        case "USER_SETTINGS_VALUE_REJECTED": {
            if (!existingData) {
                return existingData;
            }
            return {
                ...existingData,
                [event.field]: event.value
            };
        }
    }

}