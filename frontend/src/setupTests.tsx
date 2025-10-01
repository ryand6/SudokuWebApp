// Add Testing Library matchers
import '@testing-library/jest-dom';

// Import Vitest mocking utilities
import { vi } from 'vitest';

// Global test setup - polyfill window.matchMedia for libraries like Tailwind or responsive logic
Object.defineProperty(window, 'matchMedia', {
    writable: true,
    value: (query: string) => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: () => {},
        removeListener: () => {},
        addEventListener: () => {},
        removeEventListener: () => {},
        dispatchEvent: () => false,
    }),
});

// Optional helper: render components with React Router context
import { render } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { WebSocketProvider } from './context/WebSocketProvider';

/**
 * Renders a React element wrapped in BrowserRouter.
 * Useful for testing components that use react-router hooks.
 */
export function renderWithRouterAndContext(queryClient: QueryClient, ui?: React.ReactElement) {
    return render(
        <QueryClientProvider client={queryClient} >
            <WebSocketProvider>
                <BrowserRouter>
                    {ui}
                </BrowserRouter>
            </WebSocketProvider>
        </QueryClientProvider>
    );
}

// global test setup of localStorage mocks
Object.defineProperty(window, 'localStorage', {
    value: {
        getItem: vi.fn(),
        setItem: vi.fn(),
        removeItem: vi.fn(),
        clear: vi.fn(),
    },
});