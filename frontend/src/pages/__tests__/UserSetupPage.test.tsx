import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';
import { UserSetupPage } from '../UserSetupPage';
import { processUserSetup } from '../../api/rest/users/processUserSetup';
import { renderWithRouterAndContext } from '../../setupTests';
import { QueryClient } from '@tanstack/react-query';

const queryClient = new QueryClient();

// ----------------- MOCKS -----------------

// Mock processUserSetup API
vi.mock('../../api/user/processUserSetup', () => ({
    processUserSetup: vi.fn(() => Promise.resolve()),
}));

// Mock react-router-dom navigation
const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
    useNavigate: () => mockNavigate,
}));

// ----------------- TESTS -----------------

describe('UserSetupPage', () => {
    beforeEach(() => {
        vi.clearAllMocks(); // Reset mocks before each test
    });

    test('renders UserForm', () => {
        renderWithRouterAndContext(queryClient, <UserSetupPage />);
        expect(screen.getByRole('textbox')).toBeInTheDocument(); // assumes input is rendered in UserForm
        // name uses case insensitive regex to find button text
        expect(screen.getByRole('button', { name: /create account/i })).toBeInTheDocument();
    });

    test('submits username and navigates', async () => {
        renderWithRouterAndContext(queryClient, <UserSetupPage />);

        const input = screen.getByRole('textbox');
        const button = screen.getByRole('button', { name: /create account/i });

        // Simulate typing a username
        await userEvent.type(input, 'testuser');

        // Simulate clicking the submit button
        await userEvent.click(button);

        // Check that API was called
        expect(processUserSetup).toHaveBeenCalledWith('testuser');

        // Check that navigation happened
        expect(mockNavigate).toHaveBeenCalledWith('/', { replace: true });
    });
});