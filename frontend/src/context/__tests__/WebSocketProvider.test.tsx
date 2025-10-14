import React from "react";
import { waitFor, render } from "@testing-library/react";
import { describe, expect, vi } from "vitest";
import { useWebSocketContext } from "../WebSocketProvider";
import { renderWithRouterAndContext } from "../../setupTests";
import { QueryClient, type UseQueryResult } from "@tanstack/react-query";
import type { ScoreDto } from "../../types/dto/entity/ScoreDto";
import type { UserDto } from "../../types/dto/entity/UserDto";
import * as currentUserHook from '../../hooks/users/useGetCurrentUser';
import * as wsUtils from '../../utils/initWebSocket';
import * as stompUtils from '../../utils/initStompClient';

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

// const mockCsrfResponse = {
//     headerName: "X-XSRF-TOKEN", 
//     token: "abc",
//     parameterName: "_csrf"
// };

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

    test('socket and stomp client created when user is not null', async () => {

        const currentUserSpy = vi.spyOn(currentUserHook, 'useCurrentUser').mockReturnValue({data: mockUser} as unknown as UseQueryResult<UserDto, Error>);

        // Mock the functions
        const socketSpy = vi.spyOn(wsUtils, 'initWebSocket').mockImplementation(() => {
            return {} as any; // empty object pretending to be a socket
        });

        const stompSpy = vi.spyOn(stompUtils, 'initStompClient').mockImplementation(() => {
            return {} as any; // empty object pretending to be a socket
        });

        let contextValue: any;
        renderWithRouterAndContext(queryClient, <TestConsumer onContext={(ctx) => contextValue = ctx} />);

        await waitFor(() => {
            //expect(window.fetch).toHaveBeenCalled();
            expect(currentUserSpy).toHaveReturnedWith({ data: mockUser});
            expect(socketSpy).toHaveBeenCalled();
            expect(stompSpy).toHaveBeenCalled();
        });
    })

    test('socket and stomp client not created when user is null', async () => {

        // window.fetch = vi.fn((...args) => {
        //     console.log("fetch called with:", args);
        //     return Promise.resolve(new Response(JSON.stringify({mockUser}), {
        //         status: 200,
        //         headers: { "Content-Type": "application/json" }
        //     }));
        // });

        // const currentUserSpy = vi.mock('../../hooks/useCurrentUser', () => ({
        //     useCurrentUser: vi.fn(() => ({ data: mockUser })),
        // }));

        const currentUserSpy = vi.spyOn(currentUserHook, 'useCurrentUser').mockReturnValue({data: null} as unknown as UseQueryResult<UserDto, Error>);

        // Mock the functions
        const socketSpy = vi.spyOn(wsUtils, 'initWebSocket').mockImplementation(() => {
            return {} as any; // empty object pretending to be a socket
        });

        const stompSpy = vi.spyOn(stompUtils, 'initStompClient').mockImplementation(() => {
            return {} as any; // empty object pretending to be a socket
        });

        let contextValue: any;
        renderWithRouterAndContext(queryClient, <TestConsumer onContext={(ctx) => contextValue = ctx} />);

        await waitFor(() => {
            expect(currentUserSpy).toHaveReturnedWith({ data: null});
            expect(socketSpy).not.toHaveBeenCalled();
            expect(stompSpy).not.toHaveBeenCalled();
        });
    })
});