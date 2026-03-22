export type SharedGameStateDto = {
    cellFirstOwnership: Record<number, number>,
    currentSharedBoardState: string | null
}