import { describe, test, expect, vi, beforeEach } from "vitest";
import { screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { UserSetupPage } from "../UserSetupPage";
import { processUserSetup } from "../../api/rest/users/mutate/processUserSetup";
import { renderWithRouterAndContext } from "../../setupTests";
import { QueryClient } from "@tanstack/react-query";

const queryClient = new QueryClient();

// Mock the API call (path must match the real file, or the mock does nothing)
vi.mock("../../api/rest/users/mutate/processUserSetup", () => ({
    processUserSetup: vi.fn(() => Promise.resolve()),
}));

// Replace only useNavigate, keep everything else in react-router-dom real
const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importOriginal) => {
    const actual = await importOriginal<typeof import("react-router-dom")>();
    return { ...actual, useNavigate: () => mockNavigate };
});


describe("UserSetupPage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    test("renders the form", () => {
        renderWithRouterAndContext(queryClient, <UserSetupPage />);
        expect(screen.getByLabelText(/choose a username/i)).toBeInTheDocument();
        expect(screen.getByLabelText(/recovery email/i)).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /create account/i })).toBeInTheDocument();
    });

    test("submits details, shows the success step, and skip navigates home", async () => {
        renderWithRouterAndContext(queryClient, <UserSetupPage />);

        await userEvent.type(screen.getByLabelText(/choose a username/i), "testuser");
        await userEvent.type(screen.getByLabelText(/recovery email/i), "a@b.com");
        await userEvent.click(screen.getByRole("button", { name: /create account/i }));

        await waitFor(() =>
            expect(processUserSetup).toHaveBeenCalledWith("testuser", "a@b.com")
        );

        // The page now shows a second step instead of navigating straight away
        expect(await screen.findByText("Account Created")).toBeInTheDocument();
        expect(mockNavigate).not.toHaveBeenCalled();

        await userEvent.click(screen.getByRole("button", { name: /skip for now/i }));
        expect(mockNavigate).toHaveBeenCalledWith("/", { replace: true });
    });

    test("'Link another provider' navigates to the linking page", async () => {
        renderWithRouterAndContext(queryClient, <UserSetupPage />);

        await userEvent.type(screen.getByLabelText(/choose a username/i), "testuser");
        await userEvent.type(screen.getByLabelText(/recovery email/i), "a@b.com");
        await userEvent.click(screen.getByRole("button", { name: /create account/i }));

        await userEvent.click(await screen.findByRole("button", { name: /link another provider/i }));
        expect(mockNavigate).toHaveBeenCalledWith("/link-additional-providers", {
            replace: true,
            state: { fromSetup: true },
        });
    });
});