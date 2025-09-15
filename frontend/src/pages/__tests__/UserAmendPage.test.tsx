import { screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';
import { UserAmendPage } from '../UserAmendPage';
import { processUserAmend } from '../../api/user/processUserAmend';
import { renderWithRouterAndContext } from '../../setupTests';

// ----------------- MOCKS -----------------

// Mock processUserAmend API
vi.mock('../../api/user/processUserAmend', () => ({
  processUserAmend: vi.fn(() => Promise.resolve()),
}));

// Mock Auth Context
const mockRefreshUserAuth = vi.fn(() => Promise.resolve());
vi.mock('../../auth/AuthContextProvider', () => ({
  getAuthContext: vi.fn(() => ({ refreshUserAuth: mockRefreshUserAuth })),
}));

// Mock react-router-dom navigation
const mockNavigate = vi.fn();
vi.mock('react-router-dom', () => ({
  useNavigate: () => mockNavigate,
}));

// ----------------- TESTS -----------------

describe('UserAmendPage', () => {
  beforeEach(() => {
    vi.clearAllMocks(); // Reset mocks before each test
  });

  test('renders UserForm', () => {
    renderWithRouterAndContext(<UserAmendPage />);
    expect(screen.getByRole('textbox')).toBeInTheDocument(); // assumes input is rendered in UserForm
    // name uses case insensitive regex to find button text
    expect(screen.getByRole('button', { name: /update account/i })).toBeInTheDocument();
  });

  test('submits username and navigates', async () => {
    renderWithRouterAndContext(<UserAmendPage />);

    const input = screen.getByRole('textbox');
    const button = screen.getByRole('button', { name: /update account/i });

    // Simulate typing a username
    await userEvent.type(input, 'testuser');

    // Simulate clicking the submit button
    await userEvent.click(button);

    // Check that API was called
    expect(processUserAmend).toHaveBeenCalledWith('testuser');

    // Check that refreshUserAuth was called
    expect(mockRefreshUserAuth).toHaveBeenCalled();

    // Check that navigation happened
    expect(mockNavigate).toHaveBeenCalledWith('/dashboard', { replace: true });
  });
});