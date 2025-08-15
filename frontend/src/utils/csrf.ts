// Gets csrf token value from cookie and returns it - for use in request headers for state changing requests
export function getCsrfToken(): string | null {
    const csrf: RegExpMatchArray | null = document.cookie.match(new RegExp('XSRF-TOKEN=([^;]+)'));
    return csrf ? csrf[0] : null;
}