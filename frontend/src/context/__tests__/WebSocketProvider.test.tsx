import React from "react";
import { waitFor, act, cleanup, render } from "@testing-library/react";
import { describe, expect, vi } from "vitest";
import { useWebSocketContext } from "../WebSocketProvider";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client"; 
import { getCsrfToken } from "../../api/csrf/getCsrfToken";
// import { useCurrentUser } from "../../hooks/useCurrentUser";
import { handleUserWebSocketMessages } from "../../services/websocket/handleUserWebSocketMessages";
// import { useQueryClient, useQuery, type UseQueryResult } from "@tanstack/react-query";
import { renderWithRouterAndContext } from "../../setupTests";
import { getCurrentUser } from "../../api/user/getCurrentUser";
import { QueryClient, type UseQueryResult } from "@tanstack/react-query";
import { useCurrentUser } from "../../hooks/useCurrentUser";
import type { ScoreDto } from "../../types/dto/entity/ScoreDto";
import type { UserDto } from "../../types/dto/entity/UserDto";

const queryClient = new QueryClient();

const mockScore: ScoreDto = {
    id: 1, 
    totalScore: 0, 
    gamesPlayed: 0
}

// stub user object
const mockUser: UserDto = {
    id: 1,
    username: "testUser",
    isOnline: true,
    score: mockScore
};

const mockCsrfResponse = {
    headerName: "X-XSRF-TOKEN", 
    token: "abc",
    parameterName: "_csrf"
};

/* ------------------ Spy on @stomp/stompjs Client ------------------ */
const subscribeSpy = vi.spyOn(Client.prototype, "subscribe");
const unsubscribeSpy = vi.spyOn(Client.prototype, "unsubscribe");
const publishSpy = vi.spyOn(Client.prototype, "publish");
const activateSpy = vi.spyOn(Client.prototype, "activate");

/* ------------------ Mock SockJS ------------------ */
vi.mock("sockjs-client", () => ({
    // provider only supplies the SockJS instance into Client via webSocketFactory.
    // An empty stub object is fine for unit testing.
    SockJS: vi.fn().mockImplementation(() => ({})),
}));

/* ------------------ Mock your app modules ------------------ */

vi.mock("../../api/csrf/getCsrfToken", () => ({
    getCsrfToken: vi.fn(),
}));
const mockedGetCsrfToken = vi.mocked(getCsrfToken);

vi.mock("../../api/user/getCurrentUser", () => ({
    getCurrentUser: vi.fn(),
}));
const mockedGetCurrentUser = vi.mocked(getCurrentUser);

vi.mock("../../hooks/useCurrentUser", () => ({
    useCurrentUser: vi.fn(),
}));
const mockedUseCurrentUser = vi.mocked(useCurrentUser);

mockedUseCurrentUser.mockReturnValue({
    data: mockUser,
    status: "success",
    isLoading: false,
    isFetching: false,
    error: null,
    refetch: vi.fn(),
    isError: false,
    isSuccess: true,
} as unknown as UseQueryResult<UserDto, Error>);

vi.mock("../../services/websocket/handleUserWebSocketMessages", () => ({
    handleUserWebSocketMessages: vi.fn(),
}));
const mockedHandleUserWebSocketMessages = vi.mocked(handleUserWebSocketMessages);

// vi.mock("@tanstack/react-query", () => ({ useQueryClient: vi.fn() }));

/* ------------------ Test helpers & cleanup ------------------ */
afterEach(() => {
    vi.clearAllMocks();
    cleanup();
});

/* Consumer that exposes the context to the test by calling onReady(ctx) */
function Consumer({ onReady }: { onReady: (ctx: any) => void }) {
    const ctx = useWebSocketContext();
    React.useEffect(() => {
        onReady(ctx);
    }, [ctx, onReady]);
    return null;
}


/* ------------------ Tests ------------------ */
// describe("WebSocketProvider (Vitest)", () => {

//     test("creates and activates client when user present and token available; onConnect subscribes to /user/queue/updates", async () => {
//         mockedGetCurrentUser.mockResolvedValue(mockUser);
//         mockedGetCsrfToken.mockResolvedValue(mockCsrfResponse);

//         let clientInstance: Client | undefined;

//         let ctx: any = null;
//         renderWithRouterAndContext(
//             queryClient,
//             <Consumer onReady={(c) => ctx = c } />
//         );

//         const clientSpy = ctx.clientRef.current;

//         // let effects flush
//         await act(async () => {});


//         console.log("effect running, currentUser=", mockedGetCurrentUser);

//         await waitFor(() => {
//             console.log('mockedUseCurrentUser call count:', mockedUseCurrentUser.mock.calls.length);
//             console.log("mockedGetCurrentUser", mockedGetCurrentUser.mock.results);
//             console.log("mockedGetCsrfToken", mockedGetCsrfToken.mock.results);
//             console.log("clientRef inside waitFor", ctx.clientRef.current);
//             expect(ctx.clientRef.current).not.toBeNull();
//         });

//         expect(activateSpy).toHaveBeenCalled();

//         expect(clientInstance?.connectHeaders).toEqual({
//             [mockCsrfResponse.headerName]: mockCsrfResponse.token
//         })

//         expect(subscribeSpy).toHaveBeenCalledWith("/user/queue/updates", expect.any(Function));

//     });

// });
