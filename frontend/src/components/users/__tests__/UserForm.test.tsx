import { describe, test, expect, vi } from "vitest";
import { render, screen, waitFor, fireEvent } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import { UserForm } from "../UserForm";

describe("UserForm", () => {
  const VALID_EMAIL = "a@b.com";

  const setup = (onSubmit = vi.fn()) => {
    render(<UserForm onSubmit={onSubmit} submitLabel="Create" />);
    const input = screen.getByPlaceholderText("Username") as HTMLInputElement;
    const email = screen.getByPlaceholderText("your@email.com") as HTMLInputElement;
    const button = screen.getByRole("button", { name: /create/i });
    const form = button.closest("form") as HTMLFormElement;
    return { input, email, button, form, onSubmit };
  };

  test("renders inputs and submit button", () => {
    const { input, email, button } = setup();
    expect(input).toBeInTheDocument();
    expect(email).toBeInTheDocument();
    expect(button).toBeInTheDocument();
  });

  test("requires username", async () => {
    const { input, email, form } = setup();
    await userEvent.type(input, "   ");
    await userEvent.type(email, VALID_EMAIL);
    fireEvent.submit(form);

    expect(await screen.findByText("Username is required")).toBeInTheDocument();
  });

  test("shows error if username is too short", async () => {
    const { input, email, form } = setup();
    await userEvent.type(input, "ab");
    await userEvent.type(email, VALID_EMAIL);
    fireEvent.submit(form);

    expect(
      await screen.findByText("Username must be between 3 and 10 characters long")
    ).toBeInTheDocument();
  });

  test("rejects whitespace inside the username", async () => {
    const { input, email, form } = setup();
    await userEvent.type(input, "ab cd");
    await userEvent.type(email, VALID_EMAIL);
    fireEvent.submit(form);

    expect(await screen.findByText("Username must not contain whitespace")).toBeInTheDocument();
  });

  test("requires recovery email", async () => {
    const { input, form } = setup();
    await userEvent.type(input, "validUser");
    fireEvent.submit(form);

    expect(await screen.findByText("Recovery email is required")).toBeInTheDocument();
  });

  test("rejects an invalid recovery email", async () => {
    const { input, email, form } = setup();
    await userEvent.type(input, "validUser");
    await userEvent.type(email, "notanemail");
    fireEvent.submit(form);

    expect(await screen.findByText("Must be a valid email address")).toBeInTheDocument();
  });

  test("username input capped at 10 chars", async () => {
    const { input } = setup();
    await userEvent.type(input, "a".repeat(11));

    expect(input).toHaveValue("a".repeat(10));
  });

  test("submits valid username and email", async () => {
    const onSubmit = vi.fn().mockResolvedValue(undefined);
    const { input, email, button } = setup(onSubmit);

    await userEvent.type(input, "validUser");
    await userEvent.type(email, VALID_EMAIL);
    await userEvent.click(button);

    await waitFor(() => expect(onSubmit).toHaveBeenCalledWith("validUser", VALID_EMAIL));
    expect(screen.queryByText(/must be/)).not.toBeInTheDocument();
  });

  test("trims leading and trailing spaces from the username before submitting", async () => {
    const onSubmit = vi.fn().mockResolvedValue(undefined);
    const { input, email, button } = setup(onSubmit);

    // 10 characters including the two spaces, so it fits the maxLength
    await userEvent.type(input, " validUse ");
    await userEvent.type(email, VALID_EMAIL);
    await userEvent.click(button);

    await waitFor(() => expect(onSubmit).toHaveBeenCalledWith("validUse", VALID_EMAIL));
  });

  test("handles 400 backend error", async () => {
    const onSubmit = vi.fn().mockRejectedValue({ status: 400, message: "Invalid username" });
    const { input, email, button } = setup(onSubmit);

    await userEvent.type(input, "BadName");
    await userEvent.type(email, VALID_EMAIL);
    await userEvent.click(button);

    expect(await screen.findByText("Invalid username")).toBeInTheDocument();
  });

  test("handles 409 backend error", async () => {
    const onSubmit = vi.fn().mockRejectedValue({ status: 409 });
    const { input, email, button } = setup(onSubmit);

    await userEvent.type(input, "TakenUser");
    await userEvent.type(email, VALID_EMAIL);
    await userEvent.click(button);

    expect(await screen.findByText("That username is already taken.")).toBeInTheDocument();
  });

  test("handles 422 backend error", async () => {
    const onSubmit = vi.fn().mockRejectedValue({ status: 422 });
    const { input, email, button } = setup(onSubmit);

    await userEvent.type(input, "FormError");
    await userEvent.type(email, VALID_EMAIL);
    await userEvent.click(button);

    expect(await screen.findByText("Fix existing form errors.")).toBeInTheDocument();
  });

  test("handles unexpected backend error", async () => {
    const onSubmit = vi.fn().mockRejectedValue({ status: 500 });
    const { input, email, button } = setup(onSubmit);

    await userEvent.type(input, "Unexpected");
    await userEvent.type(email, VALID_EMAIL);
    await userEvent.click(button);

    expect(
      await screen.findByText("Something went wrong whilst processing request.")
    ).toBeInTheDocument();
  });

  test("clears error when resubmitting valid username", async () => {
    const onSubmit = vi.fn().mockResolvedValue(undefined);
    const { input, email, button, form } = setup(onSubmit);

    // First submit: invalid (blank) username
    await userEvent.type(input, " ");
    await userEvent.type(email, VALID_EMAIL);
    fireEvent.submit(form);
    expect(await screen.findByText("Username is required")).toBeInTheDocument();

    // Fix the username and submit again
    await userEvent.clear(input);
    await userEvent.type(input, "validUser");
    await userEvent.click(button);

    await waitFor(() => expect(onSubmit).toHaveBeenCalledWith("validUser", VALID_EMAIL));
    expect(screen.queryByText("Username is required")).not.toBeInTheDocument();
  });
});