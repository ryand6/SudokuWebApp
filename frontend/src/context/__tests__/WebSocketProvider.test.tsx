import { describe, test, expect, vi, beforeEach, afterEach } from "vitest";
import { render, act } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import type { Client } from "@stomp/stompjs";
import {
    WebSocketProvider,
    useWebSocketContext,
    MAX_RECONNECT_DELAY,
    type WebSocketContextType,
} from "../WebSocketProvider";
import { useGetCurrentUser } from "../../api/rest/users/query/useGetCurrentUser";
import { useInitClient } from "@/hooks/ws/useInitClient";
import { subscribeUserUpdates } from "@/services/websocket/subscribeUserUpdates";
import { subscribeUserErrors } from "@/services/websocket/subscribeUserErrors";

vi.hoisted(() => {
    vi.resetModules();
});


// ----------------- MOCKS -----------------
// Everything the provider depends on is replaced, so these tests only check the
// provider's own logic (subscribe / unsubscribe / send / connection state).

vi.mock("../../api/rest/users/query/useGetCurrentUser", () => ({
    useGetCurrentUser: vi.fn(),
}));
vi.mock("@/hooks/ws/useInitClient", () => ({ useInitClient: vi.fn() }));
vi.mock("@/hooks/ws/useHandleUnmount", () => ({ useHandleUnmount: vi.fn() }));
vi.mock("@/hooks/ws/useHandleUnload", () => ({ useHandleUnload: vi.fn() }));
vi.mock("@/services/websocket/subscribeUserUpdates", () => ({ subscribeUserUpdates: vi.fn() }));
vi.mock("@/services/websocket/subscribeUserErrors", () => ({ subscribeUserErrors: vi.fn() }));

// ----------------- HELPERS -----------------

const mockUser = { id: 1, username: "testUser" };

// Holds the latest context value seen by the consumer component
let latest: WebSocketContextType;

function Consumer() {
    latest = useWebSocketContext();
    return null;
}

function renderProvider() {
    const queryClient = new QueryClient();
    return render(
        <QueryClientProvider client={queryClient}>
            <WebSocketProvider>
                <Consumer />
            </WebSocketProvider>
        </QueryClientProvider>
    );
}

// The provider hands these callbacks to useInitClient, which is mocked.
// We grab them from the most recent call so the test can act as "the socket".
function getHandlers() {
    const calls = vi.mocked(useInitClient).mock.calls;
    const args = calls[calls.length - 1];
    return {
        handleConnect: args[5] as () => void,
        handleDisconnect: args[6] as () => void,
        handleWebSocketClose: args[7] as () => void,
    };
}

// Fake STOMP client: records calls, returns a fake subscription each time
function makeFakeClient(connected = true) {
    return {
        connected,
        subscribe: vi.fn(() => ({ id: "sub", unsubscribe: vi.fn() })),
        publish: vi.fn(),
        deactivate: vi.fn(),
    };
}

function attachClient(client: ReturnType<typeof makeFakeClient>) {
    (latest.clientRef as { current: Client | null }).current = client as unknown as Client;
}

// ----------------- TESTS -----------------

describe("WebSocketProvider", () => {
    beforeEach(() => {
        vi.clearAllMocks();
        vi.mocked(useGetCurrentUser).mockReturnValue({
            data: mockUser,
        } as unknown as ReturnType<typeof useGetCurrentUser>);
    });

    afterEach(() => {
        vi.useRealTimers();
    });

    test("throws if useWebSocketContext is used outside the provider", () => {
        const errorSpy = vi.spyOn(console, "error").mockImplementation(() => {});
        expect(() => render(<Consumer />)).toThrow(
            "useWebSocketContext must be used inside WebSocketProvider wrapper"
        );
        errorSpy.mockRestore();
    });

    test("provides the expected context values, starting disconnected", () => {
        renderProvider();
        expect(typeof latest.subscribe).toBe("function");
        expect(typeof latest.unsubscribe).toBe("function");
        expect(typeof latest.send).toBe("function");
        expect(latest.isConnected).toBe(false);
        expect(latest.nextReconnectAt).toBeNull();
        expect(latest.clientRef.current).toBeNull();
    });

    test("passes the current user to useInitClient", () => {
        renderProvider();
        expect(useInitClient).toHaveBeenCalled();
        const calls = vi.mocked(useInitClient).mock.calls;
        expect(calls[calls.length - 1][0]).toEqual(mockUser);
    });

    test("subscribe before connecting returns null and is queued", () => {
        renderProvider();
        const result = latest.subscribe("/topic/lobby/1", vi.fn());
        expect(result).toBeNull();
    });

    test("queued subscriptions are subscribed once the client connects", () => {
        renderProvider();
        const callback = vi.fn();
        latest.subscribe("/topic/lobby/1", callback);

        const client = makeFakeClient();
        attachClient(client);
        act(() => getHandlers().handleConnect());

        expect(latest.isConnected).toBe(true);
        expect(client.subscribe).toHaveBeenCalledWith("/topic/lobby/1", expect.any(Function));

        // The handler given to STOMP should parse the JSON body and pass it on
        const stompHandler = (client.subscribe.mock.calls[0] as unknown[])[1] as (m: { body: string }) => void;
        stompHandler({ body: JSON.stringify({ hello: "world" }) });
        expect(callback).toHaveBeenCalledWith({ hello: "world" });
    });

    test("connecting also sets up the system subscriptions", () => {
        renderProvider();
        attachClient(makeFakeClient());
        act(() => getHandlers().handleConnect());

        expect(subscribeUserUpdates).toHaveBeenCalledTimes(1);
        expect(subscribeUserErrors).toHaveBeenCalledTimes(1);
    });

    test("subscribe while connected subscribes straight away and returns the subscription", () => {
        renderProvider();
        const client = makeFakeClient();
        attachClient(client);

        const result = latest.subscribe("/topic/game/5", vi.fn());

        expect(result).not.toBeNull();
        expect(client.subscribe).toHaveBeenCalledTimes(1);
    });

    test("subscribing to the same topic twice reuses the first subscription", () => {
        renderProvider();
        const client = makeFakeClient();
        attachClient(client);

        const first = latest.subscribe("/topic/game/5", vi.fn());
        const second = latest.subscribe("/topic/game/5", vi.fn());

        expect(second).toBe(first);
        expect(client.subscribe).toHaveBeenCalledTimes(1);
    });

    test("unsubscribe cancels the subscription and allows re-subscribing", () => {
        renderProvider();
        const client = makeFakeClient();
        attachClient(client);

        const subscription = latest.subscribe("/topic/game/5", vi.fn());
        latest.unsubscribe("/topic/game/5");

        expect(subscription!.unsubscribe).toHaveBeenCalledTimes(1);

        latest.subscribe("/topic/game/5", vi.fn());
        expect(client.subscribe).toHaveBeenCalledTimes(2);
    });

    test("unsubscribe on an unknown topic does nothing", () => {
        renderProvider();
        expect(() => latest.unsubscribe("/topic/never-subscribed")).not.toThrow();
    });

    test("dynamic subscriptions are re-created after a reconnect", () => {
        renderProvider();
        const client = makeFakeClient();
        attachClient(client);
        const callback = vi.fn();

        latest.subscribe("/topic/game/5", callback);
        expect(client.subscribe).toHaveBeenCalledTimes(1);

        // Simulate the connection dropping and coming back
        act(() => getHandlers().handleDisconnect());
        act(() => getHandlers().handleConnect());

        expect(client.subscribe).toHaveBeenCalledTimes(2);
        expect(client.subscribe).toHaveBeenLastCalledWith("/topic/game/5", expect.any(Function));

        const stompHandler = (client.subscribe.mock.calls[1] as unknown[])[1] as (m: { body: string }) => void;
        stompHandler({ body: JSON.stringify({ n: 1 }) });
        expect(callback).toHaveBeenCalledWith({ n: 1 });
    });

    test("send publishes the body as JSON to the destination", () => {
        renderProvider();
        const client = makeFakeClient();
        attachClient(client);

        latest.send("/app/move", { cell: 4 });

        expect(client.publish).toHaveBeenCalledWith({
            destination: "/app/move",
            body: JSON.stringify({ cell: 4 }),
        });
    });

    test("send with no client does not throw", () => {
        renderProvider();
        expect(() => latest.send("/app/move", { cell: 4 })).not.toThrow();
    });

    test("disconnect sets isConnected false and schedules a reconnect time", () => {
        renderProvider();
        attachClient(makeFakeClient());
        act(() => getHandlers().handleConnect());
        expect(latest.isConnected).toBe(true);
        expect(latest.nextReconnectAt).toBeNull();

        const before = Date.now();
        act(() => getHandlers().handleDisconnect());

        expect(latest.isConnected).toBe(false);
        expect(latest.nextReconnectAt).toBeGreaterThan(before);
    });

    test("a websocket close is handled the same way as a disconnect", () => {
        renderProvider();
        attachClient(makeFakeClient());
        act(() => getHandlers().handleConnect());

        act(() => getHandlers().handleWebSocketClose());

        expect(latest.isConnected).toBe(false);
        expect(latest.nextReconnectAt).not.toBeNull();
    });

    test("reconnecting clears nextReconnectAt", () => {
        renderProvider();
        attachClient(makeFakeClient());
        act(() => getHandlers().handleDisconnect());
        expect(latest.nextReconnectAt).not.toBeNull();

        act(() => getHandlers().handleConnect());

        expect(latest.isConnected).toBe(true);
        expect(latest.nextReconnectAt).toBeNull();
    });

    test("reconnect delay doubles on each disconnect and is capped", () => {
        // Freeze the clock and make the random starting delay predictable (500ms)
        vi.useFakeTimers({ toFake: ["Date"] });
        vi.setSystemTime(0);
        const randomSpy = vi.spyOn(Math, "random").mockReturnValue(0);

        renderProvider();
        const expected = [500, 1000, 2000, 4000, 8000, MAX_RECONNECT_DELAY, MAX_RECONNECT_DELAY];

        for (const delay of expected) {
            act(() => getHandlers().handleDisconnect());
            expect(latest.nextReconnectAt).toBe(delay);
        }

        randomSpy.mockRestore();
    });

    test("connecting resets the reconnect delay", () => {
       vi.useFakeTimers({ toFake: ["Date"] });
       vi.setSystemTime(0);
       const randomSpy = vi.spyOn(Math, "random").mockReturnValue(0);

       renderProvider();
       attachClient(makeFakeClient());

       act(() => getHandlers().handleDisconnect()); // 500
       act(() => getHandlers().handleDisconnect()); // 1000
       act(() => getHandlers().handleConnect());    // reset
       act(() => getHandlers().handleDisconnect());

       expect(latest.nextReconnectAt).toBe(500);
       randomSpy.mockRestore();
    });
});