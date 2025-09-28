export async function getCsrfToken(): Promise<{ parameterName: string, token: string, headerName: string }> {
    const response = await fetch("/csrf", { 
        credentials: "include" 
    });
    const data = await response.json();
    console.log("CSRF DATA" + data);
    return data;
}