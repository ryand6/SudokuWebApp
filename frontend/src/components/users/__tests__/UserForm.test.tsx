import { describe, expect, vi } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from '@testing-library/user-event';
import { UserForm } from "../users/UserForm";

describe("UserForm", () => {
  const setup = (onSubmit = vi.fn()) => {
    render(<UserForm onSubmit={onSubmit} submitLabel="Create" />);
    const input = screen.getByPlaceholderText("Username") as HTMLInputElement;
    const button = screen.getByRole("button", { name: /create/i });
    return { input, button, onSubmit };
  };

  test("renders input and submit button", () => {
    const { input, button } = setup();
    expect(input).toBeInTheDocument();
    expect(button).toBeInTheDocument();
  });

  test("requires username", async () => {
    const { input, button } = setup();
    await userEvent.type(input, "   ");
    await userEvent.click(button);

    expect(await screen.findByText("Username is required")).toBeInTheDocument();
  });

  test("shows error if username is too short", async () => {
    const { input, button } = setup();
    await userEvent.type(input, "ab")
    await userEvent.click(button);

    expect(await screen.findByText("Username must be between 3 and 20 characters long")).toBeInTheDocument();
  });

  test("username input capped at 20 chars", async () => {
    const { input, button } = setup();
    await userEvent.type(input, "a".repeat(21));
    await userEvent.click(button);

    expect(input).toHaveValue("a".repeat(20));
  });

  test("submits valid username", async () => {
    const onSubmit = vi.fn().mockResolvedValue(undefined);
    const { input, button } = setup(onSubmit);

    await userEvent.type(input, "validUser")
    await userEvent.click(button);

    await waitFor(() => expect(onSubmit).toHaveBeenCalledWith("validUser"));
    expect(screen.queryByText(/must be/)).not.toBeInTheDocument();
  });

  test("handles 400 backend error", async () => {
    const onSubmit = vi.fn().mockRejectedValue({ status: 400, message: "Invalid username" });
    const { input, button } = setup(onSubmit);

    await userEvent.type(input, "InvalidUser");
    await userEvent.click(button);
    screen.debug(); 
    expect(await screen.findByText("Invalid username")).toBeInTheDocument();
  });

  test("handles 409 backend error", async () => {
    const onSubmit = vi.fn().mockRejectedValue({ status: 409 });
    const { input, button } = setup(onSubmit);

    await userEvent.type(input, "TakenUser");
    await userEvent.click(button);

    expect(await screen.findByText("That username is already taken.")).toBeInTheDocument();
  });

  test("handles 422 backend error", async () => {
    const onSubmit = vi.fn().mockRejectedValue({ status: 422 });
    const { input, button } = setup(onSubmit);

    await userEvent.type(input, "FormError");
    await userEvent.click(button);

    expect(await screen.findByText("Fix existing form errors.")).toBeInTheDocument();
  });

  test("handles unexpected backend error", async () => {
    const onSubmit = vi.fn().mockRejectedValue({ status: 500 });
    const { input, button } = setup(onSubmit);

    await userEvent.type(input, "UnexpectedError");
    await userEvent.click(button);

    expect(await screen.findByText("Something went wrong whilst processing request.")).toBeInTheDocument();
  });

  test("clears error when resubmitting valid username", async () => {
    const { input, button, onSubmit } = setup(vi.fn().mockResolvedValue(undefined));

    // First submit invalid username
    await userEvent.type(input, " ");
    await userEvent.click(button);
    expect(await screen.findByText("Username is required")).toBeInTheDocument();

    // Fix username and resubmit
    await userEvent.clear(input);
    await userEvent.type(input, "validUser");
    await userEvent.click(button);

    await waitFor(() => expect(onSubmit).toHaveBeenCalledWith("validUser"));
    expect(screen.queryByText("Username is required")).not.toBeInTheDocument();
  });
});