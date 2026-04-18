import type { UserDto } from "@/types/dto/entity/user/UserDto"

export type UserEvent = 
    | {
        type: "USER_UPDATED",
        userData: UserDto
      }
    | {
        type: "USER_SETTINGS_UPDATED",
        field: string,
        value: any
      }
    | {
        type: "USER_SETTINGS_FIELD_REJECTED",
        field: string,
        value: any
      }
    | {
        type: "USER_SETTINGS_VALUE_REJECTED",
        field: string,
        value: any
    }
