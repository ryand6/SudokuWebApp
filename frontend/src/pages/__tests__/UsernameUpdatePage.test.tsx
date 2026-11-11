import { describe, test, expect, vi, beforeEach } from "vitest";
import { screen, waitFor, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { UsernameUpdatePage } from "../UsernameUpdatePage";
import { processUsernameUpdate } from "../../api/rest/users/mutate/processUsernameUpdate";
import { renderWithRouterAndContext } from "../../setupTests";
import { QueryClient } from "@tanstack/react-query";

const queryClient = new QueryClient();

// Mock the API call (path must match the real file, or the mock does nothing)
vi.mock("../../api/rest/users/mutate/processUsernameUpdate", () => ({
    processUsernameUpdate: vi.fn(() => Promise.resolve()),
}));

// The page loads the current user; return fixed data so no real request is made
vi.mock("../../api/rest/users/query/useGetCurrentUser", () => ({
    useGetCurrentUser: () => ({ data: { username: "oldname" } }),
}));

// Replace only useNavigate, keep everything else in react-router-dom real
const mockNavigate = vi.fn();
vi.mock("react-router-dom", async (importOriginal) => {
    const actual = await importOriginal<typeof import("react-router-dom")>();
    return { ...actual, useNavigate: () => mockNavigate };
});

function setup() {
    renderWithRouterAndContext(queryClient, <UsernameUpdatePage />);
    const input = screen.getByRole("textbox");
    const button = screen.getByRole("button", { name: /update username/i });
    const form = button.closest("form") as HTMLFormElement;
    return { input, button, form };
}

describe("UsernameUpdatePage", () => {
    beforeEach(() => {
        vi.clearAllMocks();
    });

    test("renders the form and the current username", () => {
        setup();
        expect(screen.getByText("oldname")).toBeInTheDocument();
        expect(screen.getByRole("textbox")).toBeInTheDocument();
        expect(screen.getByRole("button", { name: /update username/i })).toBeInTheDocument();
    });

    test("submits a valid username and navigates to the dashboard", async () => {
        const { input, button } = setup();

        await userEvent.type(input, "newname");
        await userEvent.click(button);

        await waitFor(() => expect(processUsernameUpdate).toHaveBeenCalledWith("newname"));
        await waitFor(() =>
            expect(mockNavigate).toHaveBeenCalledWith("/dashboard", { replace: true })
        );
    });

    test("trims leading and trailing spaces before submitting", async () => {
        const { input, button } = setup();

        // 9 characters including the spaces, so it fits the maxLength of 10
        await userEvent.type(input, " newname ");
        await userEvent.click(button);

        await waitFor(() => expect(processUsernameUpdate).toHaveBeenCalledWith("newname"));
    });

    // Tests below use fireEvent.submit(form) to skip the browser's built-in checks
    // (required / minLength), so our own validation messages are the ones tested.

    test("requires a username", async () => {
        const { input, form } = setup();
        await userEvent.type(input, "   ");
        fireEvent.submit(form);

        expect(await screen.findByText("Username is required")).toBeInTheDocument();
        expect(processUsernameUpdate).not.toHaveBeenCalled();
    });

    test("shows error if username is too short", async () => {
        const { input, form } = setup();
        await userEvent.type(input, "ab");
        fireEvent.submit(form);

        expect(
            await screen.findByText("Username must be between 3 and 10 characters long")
        ).toBeInTheDocument();
        expect(processUsernameUpdate).not.toHaveBeenCalled();
    });

    test("rejects whitespace inside the username", async () => {
        const { input, form } = setup();
        await userEvent.type(input, "ab cd");
        fireEvent.submit(form);

        expect(await screen.findByText("Username must not contain whitespace")).toBeInTheDocument();
        expect(processUsernameUpdate).not.toHaveBeenCalled();
    });

    test("username input capped at 10 chars", async () => {
        const { input } = setup();
        await userEvent.type(input, "a".repeat(11));

        expect(input).toHaveValue("a".repeat(10));
    });

    test("handles 409 (username taken)", async () => {
        vi.mocked(processUsernameUpdate).mockRejectedValueOnce({ status: 409 });
        const { input, button } = setup();

        await userEvent.type(input, "takenname");
        await userEvent.click(button);

        expect(await screen.findByText("That username is already taken.")).toBeInTheDocument();
        expect(mockNavigate).not.toHaveBeenCalled();
    });

    test("handles 400 backend validation error", async () => {
        vi.mocked(processUsernameUpdate).mockRejectedValueOnce({
            status: 400,
            message: "Invalid username",
        });
        const { input, button } = setup();

        await userEvent.type(input, "badname");
        await userEvent.click(button);

        expect(await screen.findByText("Invalid username")).toBeInTheDocument();
    });

    test("handles unexpected backend error", async () => {
        vi.mocked(processUsernameUpdate).mockRejectedValueOnce({ status: 500 });
        const { input, button } = setup();

        await userEvent.type(input, "newname");
        await userEvent.click(button);

        expect(
            await screen.findByText("Something went wrong whilst processing request.")
        ).toBeInTheDocument();
    });
});