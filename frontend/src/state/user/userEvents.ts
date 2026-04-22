import type { UserDto } from "@/types/dto/entity/user/UserDto"
import type { UserSettingsDto } from "@/types/dto/entity/user/UserSettingsDto"

export type UserEvent = 
    | {
        type: "USER_UPDATED",
        userData: UserDto
      }
    | {
        type: "USER_SETTINGS_UPDATED",
        field: keyof UserSettingsDto,
        value: any
      }
    | {
        type: "USER_SETTINGS_FIELD_REJECTED",
        field: keyof UserSettingsDto,
        value: any
      }
    | {
        type: "USER_SETTINGS_VALUE_REJECTED",
        field: keyof UserSettingsDto,
        value: any
    }
