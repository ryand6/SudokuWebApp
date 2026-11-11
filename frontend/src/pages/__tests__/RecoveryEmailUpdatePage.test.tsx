import { describe, test, expect, vi, beforeEach } from "vitest";
import { screen, waitFor, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { RecoveryEmailUpdatePage } from "../RecoveryEmailUpdatePage";
import { processRecoveryEmailUpdate } from "../../api/rest/users/mutate/processRecoveryEmailUpdate";
import { renderWithRouterAndContext } from "../../setupTests";
import { QueryClient } from "@tanstack/react-query";


const queryClient = new QueryClient();

// ----------------- MOCKS -----------------

// Mock the API call (path must match the real file, or the mock does nothing)
vi.mock("../../api/rest/users/mutate/processRecoveryEmailUpdate", () => ({
    processRecoveryEmailUpdate: vi.fn(() => Promise.resolve()),
}));

// Replace only useNavigate, keep everything else in react-router-dom real
const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importOriginal) => {
    const actual = await importOriginal<typeof import("react-router-dom")>();
    return { ...actual, useNavigate: () => mockNavigate };
});

// ----------------- HELPERS -----------------

function setup() {
    renderWithRouterAndContext(queryClient, <RecoveryEmailUpdatePage />);
    const input = screen.getByRole("textbox");
    const button = screen.getByRole("button", { name: /update recovery email/i });
    const form = button.closest("form") as HTMLFormElement;
    return { input, button, form };
}

// ----------------- TESTS -----------------

describe("RecoveryEmailUpdatePage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    test("renders the heading, email input and button", () => {
        setup();
        expect(screen.getByRole("heading", { name: /update recovery email/i })).toBeInTheDocument();
        expect(screen.getByLabelText(/recovery email/i)).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /update recovery email/i })).toBeInTheDocument();
    });

    test("submits a valid email and navigates to the dashboard", async () => {
        const { input, button } = setup();

        await userEvent.type(input, "a@b.com");
        await userEvent.click(button);

        await waitFor(() => expect(processRecoveryEmailUpdate).toHaveBeenCalledWith("a@b.com"));
        await waitFor(() =>
            expect(mockNavigate).toHaveBeenCalledWith("/dashboard", { replace: true })
        );
    });

    // Tests below use fireEvent.submit(form) to skip the browser's built-in checks
    // (required / type="email"), so our own validation messages are the ones tested.

    test("requires an email", async () => {
        const { form } = setup();
        fireEvent.submit(form);

        expect(await screen.findByText("Recovery email is required")).toBeInTheDocument();
        expect(processRecoveryEmailUpdate).not.toHaveBeenCalled();
    });

    test("rejects an invalid email", async () => {
        const { input, form } = setup();
        await userEvent.type(input, "notanemail");
        fireEvent.submit(form);

        expect(await screen.findByText("Must be a valid email address")).toBeInTheDocument();
        expect(processRecoveryEmailUpdate).not.toHaveBeenCalled();
    });

    test("clears the error after fixing the email and resubmitting", async () => {
        const { input, button, form } = setup();
        await userEvent.type(input, "notanemail");
        fireEvent.submit(form);
        expect(await screen.findByText("Must be a valid email address")).toBeInTheDocument();

        await userEvent.clear(input);
        await userEvent.type(input, "a@b.com");
        await userEvent.click(button);

        await waitFor(() => expect(processRecoveryEmailUpdate).toHaveBeenCalledWith("a@b.com"));
        expect(screen.queryByText("Must be a valid email address")).not.toBeInTheDocument();
    });

    test("handles 400 backend validation error", async () => {
        vi.mocked(processRecoveryEmailUpdate).mockRejectedValueOnce({
            status: 400,
            message: "Must be a valid email address",
        });
        const { input, button } = setup();

        await userEvent.type(input, "a@b.com");
        await userEvent.click(button);

        expect(await screen.findByText("Must be a valid email address")).toBeInTheDocument();
        expect(mockNavigate).not.toHaveBeenCalled();
    });

    test("handles 422 backend error", async () => {
        vi.mocked(processRecoveryEmailUpdate).mockRejectedValueOnce({ status: 422 });
        const { input, button } = setup();

        await userEvent.type(input, "a@b.com");
        await userEvent.click(button);

        expect(await screen.findByText("Fix existing form errors.")).toBeInTheDocument();
    });

    test("handles unexpected backend error", async () => {
        vi.mocked(processRecoveryEmailUpdate).mockRejectedValueOnce({ status: 500 });
        const { input, button } = setup();

        await userEvent.type(input, "a@b.com");
        await userEvent.click(button);

        expect(
            await screen.findByText("Something went wrong whilst processing request.")
        ).toBeInTheDocument();
        expect(mockNavigate).not.toHaveBeenCalled();
    });
});