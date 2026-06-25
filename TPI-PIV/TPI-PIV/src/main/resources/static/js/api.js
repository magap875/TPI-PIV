// URL base de la API.
const API_URL =
    window.location.hostname === "127.0.0.1" ||
    window.location.hostname === "localhost"
        ? "http://localhost:8080"
        : window.location.origin;

// manejo de tokens
function getAccessToken() {
    return localStorage.getItem("accessToken");
}

function getRefreshToken() {
    return localStorage.getItem("refreshToken");
}

function guardarTokens(accessToken, refreshToken) {
    localStorage.setItem("accessToken", accessToken);
    localStorage.setItem("refreshToken", refreshToken);
}

function limpiarTokens() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
}

async function refrescarToken() {
    const refreshToken = getRefreshToken();

    if (!refreshToken) {
        limpiarTokens();
        return null;
    }

    try {
        const res = await fetch(`${API_URL}/api/auth/refresh`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ refreshToken })
        });

        if (!res.ok) {
            limpiarTokens();
            return null;
        }

        const response = await res.json();
        const data = response.data ?? response;

        guardarTokens(data.accessToken, data.refreshToken);

        return data.accessToken;

    } catch (e) {
        console.error("Error al refrescar token:", e);
        limpiarTokens();
        return null;
    }
}

async function fetchConToken(url, opciones = {}, reintentando = false) {
    const token = getAccessToken();

    const headers = {
        ...(opciones.headers || {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {})
    };

    const res = await fetch(url, { ...opciones, headers });
    console.log("STATUS:", res.status);

    if (res.status !== 401 || reintentando) {
        return res;
    }

    const nuevoAccessToken = await refrescarToken();

    if (!nuevoAccessToken) {
        cerrarSesion();
        return res;
    }

    return fetchConToken(url, opciones, true);
}

function cerrarSesion() {
    limpiarTokens();
    location.reload();
}