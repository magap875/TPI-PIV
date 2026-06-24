// URL base de la API.
const API_URL =
    window.location.hostname === "127.0.0.1" ||
    window.location.hostname === "localhost"
        ? "http://localhost:8080"
        : window.location.origin;

// ===== MANEJO DE TOKENS =====

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

// Llama a /api/auth/refresh con el refreshToken guardado.
// Si el backend responde OK, guarda el par nuevo (accessToken + refreshToken,
// porque el backend rota ambos) y devuelve el accessToken nuevo.
// Si falla, limpia los tokens y devuelve null.
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

// ===== FETCH CON MANEJO AUTOMÁTICO DE TOKEN VENCIDO =====

// Envoltorio de fetch que:
// 1. Agrega el header Authorization automáticamente (si hay token).
// 2. Si la respuesta es 401 (token vencido), intenta refrescar el token
//    una sola vez y reintenta la request original con el token nuevo.
// 3. Si el refresh también falla, cierra la sesión (sesión realmente vencida).
//
// Uso: en vez de fetch(`${API_URL}/api/...`, opciones)
//      usar fetchConToken(`${API_URL}/api/...`, opciones)
//
// No hace falta poner el header Authorization a mano: fetchConToken lo agrega solo.
async function fetchConToken(url, opciones = {}, reintentando = false) {
    const token = getAccessToken();

    const headers = {
        ...(opciones.headers || {}),
        ...(token ? { Authorization: `Bearer ${token}` } : {})
    };

    const res = await fetch(url, { ...opciones, headers });

    // Si no es 401, o ya reintentamos una vez, devolvemos la respuesta tal cual
    // (sea éxito o cualquier otro error que no sea de autenticación).
    if (res.status !== 401 || reintentando) {
        return res;
    }

    // Token vencido: intentamos refrescar una sola vez.
    const nuevoAccessToken = await refrescarToken();

    if (!nuevoAccessToken) {
        // El refresh token también venció o es inválido: sesión muerta de verdad.
        cerrarSesion();
        return res;
    }

    // Reintentamos la request original, ahora con el token nuevo.
    return fetchConToken(url, opciones, true);
}

// Cierra la sesión y recarga la página (ya la tenías en home.js;
// la dejamos también acá para que cualquier página con api.js pueda usarla).
function cerrarSesion() {
    limpiarTokens();
    location.reload();
}