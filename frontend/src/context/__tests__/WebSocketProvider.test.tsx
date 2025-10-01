import React from "react";
import { waitFor, act, cleanup, render } from "@testing-library/react";
import { describe, expect, vi } from "vitest";
import { useWebSocketContext, WebSocketProvider } from "../WebSocketProvider";
import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client"; 
import { getCsrfToken } from "../../api/csrf/getCsrfToken";
import { handleUserWebSocketMessages } from "../../services/websocket/handleUserWebSocketMessages";
import { renderWithRouterAndContext } from "../../setupTests";
import { QueryClient, QueryClientProvider, type UseQueryResult } from "@tanstack/react-query";
import { useCurrentUser } from "../../hooks/useCurrentUser";
import type { ScoreDto } from "../../types/dto/entity/ScoreDto";
import type { UserDto } from "../../types/dto/entity/UserDto";
import * as ReactQuery from '@tanstack/react-query'

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


// Mock STOMP Client
vi.mock('@stomp/stompjs', () => ({
    Client: vi.fn().mockImplementation(() => ({
        activate: vi.fn(),
        deactivate: vi.fn(),
        subscribe: vi.fn((topic, callback) => ({
            unsubscribe: vi.fn(),
            topic,
            callback,
        })),
        publish: vi.fn(),
        connected: true,
    })),
}));

// Mock utilities
vi.mock('./utils/initWebSocket', () => ({
    initWebSocket: vi.fn().mockReturnValue({}),
}));

vi.mock('./utils/initStompClient', () => ({
    initStompClient: vi.fn(),
}));

// Mock current user hook
vi.mock('../../hooks/useCurrentUser', () => ({
    useCurrentUser: vi.fn(),
}));
const mockedUseCurrentUser = vi.mocked(useCurrentUser);


/* Consumer that exposes the context to the test by calling onReady(ctx) */
function TestConsumer({ onContext }: { onContext: (ctx: any) => void }) {
    const ctx = useWebSocketContext();
    React.useEffect(() => {
        onContext(ctx);
    }, [ctx, onContext]);
    return null;
}


/* ------------------ Tests ------------------ */
describe('WebSocketProviderTests', () => {

    beforeEach(() => {
        vi.clearAllMocks();
    });

    test('throws error if useWebSocketContext is used outside provider', () => {
        const fn = () => render(<TestConsumer onContext={() => {}} />);
        expect(fn).toThrowError('useWebSocketContext must be used inside WebSocketProvider wrapper');
    });

    test('provides subscribe, unsubscribe, send when wrapped in provider', () => {

        let contextValue: any;
        renderWithRouterAndContext(queryClient, <TestConsumer onContext={(ctx) => contextValue = ctx} />);

        expect(contextValue).toHaveProperty('subscribe');
        expect(contextValue).toHaveProperty('unsubscribe');
        expect(contextValue).toHaveProperty('send');
    });

    // test('does not initialise WebSocket if currentUser is null', () => {
    //     mockedUseCurrentUser.mockReturnValue({
    //         data: mockUser,
    //         isLoading: false,
    //         isSuccess: true,
    //     } as any);
    //     let contextValue: any;
    //     renderWithRouterAndContext(queryClient, <TestConsumer onContext={(ctx) => contextValue = ctx} />);

    //     expect(mockedUseCurrentUser).toHaveBeenCalled();
    // })
});