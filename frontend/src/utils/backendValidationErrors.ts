// Creates an Error object with a message comprised of all validation errors found in backend, separated by newline
export function backendValidationErrors(errors: string[]): Error {
    let errorMessage: string = errors.join("\n");
    return new Error(errorMessage);
}