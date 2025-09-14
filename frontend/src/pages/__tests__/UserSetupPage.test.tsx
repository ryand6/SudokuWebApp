import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { vi } from 'vitest';
import { UserSetupPage } from '../UserSetupPage';
import { getAuthContext } from '../../auth/AuthContextProvider';
import { useNavigate } from 'react-router-dom';
import { processUserSetup } from '../../api/user/processUserSetup';
import { renderWithRouterAndContext } from '../../setupTests';

// ----------------- MOCKS -----------------

// Mock processUserSetup API
vi.mock('../../api/user/processUserSetup', () => ({
  processUserSetup: vi.fn(() => Promise.resolve()),
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

describe('UserSetupPage', () => {
  beforeEach(() => {
    vi.clearAllMocks(); // Reset mocks before each test
  });

  test('renders UserForm', () => {
    renderWithRouterAndContext(<UserSetupPage />);
    expect(screen.getByRole('textbox')).toBeInTheDocument(); // assumes input is rendered in UserForm
    // name uses case insensitive regex to find button text
    expect(screen.getByRole('button', { name: /create account/i })).toBeInTheDocument();
  });

  test('submits username and navigates', async () => {
    renderWithRouterAndContext(<UserSetupPage />);

    const input = screen.getByRole('textbox');
    const button = screen.getByRole('button', { name: /create account/i });

    // Simulate typing a username
    await userEvent.type(input, 'testuser');

    // Simulate clicking the submit button
    await userEvent.click(button);

    // Check that API was called
    expect(processUserSetup).toHaveBeenCalledWith('testuser');

    // Check that refreshUserAuth was called
    expect(mockRefreshUserAuth).toHaveBeenCalled();

    // Check that navigation happened
    expect(mockNavigate).toHaveBeenCalledWith('/', { replace: true });
  });
});