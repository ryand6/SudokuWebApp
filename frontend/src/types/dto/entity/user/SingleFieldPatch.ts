export type SingleFieldPatch<T> = {
    field: keyof T,
    value: T[keyof T]
}