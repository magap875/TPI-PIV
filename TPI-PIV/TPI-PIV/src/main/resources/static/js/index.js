const navbar = document.getElementById("navbar-actions");

let todasLasFechas = [];
let fechaSeleccionada = null;
let estadoSeleccionado = "POR_JUGARSE";
let todosLosPartidos = [];
let usuarioActualId = null;

document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("accessToken");

    if (!token) {
        ocultarSeccionesPrivadas();
    }

    await cargarTodosLosPartidos();

    cargarNavbar();
    cargarFechas();
    initFiltroEstados();
    cargarPreviewGrupo();
    cargarPreviewApuestas();
    cargarRankingGlobal();
});

function mostrarSpinner(contenedorId) {
    const contenedor = document.getElementById(contenedorId);
    if (!contenedor) return;

    contenedor.innerHTML = `
        <div class="flex items-center justify-center py-8 col-span-full">
            <div class="w-6 h-6 border-2 border-[#2a2a2a] border-t-[#05AC2E] rounded-full animate-spin"></div>
        </div>
    `;
}

function ocultarSeccionesPrivadas() {
    const previewGrupo = document.getElementById("preview-grupo");
    const previewApuestas = document.getElementById("preview-apuestas");

    if (previewGrupo) {
        previewGrupo.innerHTML = `
            <p class="text-sm text-gray-500">
                Iniciá sesión para ver tus grupos.
            </p>
        `;
    }

    if (previewApuestas) {
        previewApuestas.innerHTML = `
            <p class="text-sm text-gray-500">
                Iniciá sesión para ver tus apuestas.
            </p>
        `;
    }

    document.querySelectorAll(".solo-autenticado").forEach(el => {
        el.classList.add("hidden");
    });
}

// navbar
async function cargarNavbar() {
    const token = getAccessToken();

    if (!token) {
        mostrarNavbarPublico();
        return;
    }

    try {
        const res = await fetchConToken(
            `${API_URL}/api/usuarios/me`
        );

        if (!res.ok) throw new Error();

        const result = await res.json();
        const usuario = result.data;

        usuarioActualId = usuario.id ?? null;

        mostrarNavbarAutenticado(usuario);

    } catch (e) {
        console.error(e);
        cerrarSesion();
    }
}

function mostrarNavbarPublico() {
    navbar.innerHTML = `
        <a href="./html/login.html" class="text-gray-400">Iniciá sesión</a>
        <a href="./html/register.html" class="bg-[#05AC2E] px-4 py-2 rounded text-white">Registrarme</a>
    `;
}

function mostrarNavbarAutenticado(usuario) {
    navbar.innerHTML = `
        ${usuario.rol === "ADMIN" ? `
            <a href="./html/panel-admin.html" class="text-[#05AC2E] font-semibold">
                Dashboard
            </a>
        ` : ""}

        <a href="./html/mi-cuenta.html" class="text-white">
            Mi perfil
        </a>

        <button id="logout-btn" class="text-red-400">
            Cerrar sesión
        </button>
    `;

    document
        .getElementById("logout-btn")
        .addEventListener("click", cerrarSesion);
}


// carga y render de fechas
async function cargarFechas() {
    mostrarSpinner("lista-fechas");

    try {
        const res = await fetch(`${API_URL}/api/fechas`);
        const result = await res.json();

        todasLasFechas = result.data ?? result ?? [];
        renderFechas(todasLasFechas);

    } catch (e) {
        console.error(e);
    }
}

function renderFechas(fechas = []) {
    const lista = document.getElementById("lista-fechas");
    lista.innerHTML = "";

    lista.innerHTML += `
        <li class="fecha-item cursor-pointer px-2 py-1 rounded hover:bg-[#1a1a1a] text-gray-300" data-id=""> Todas las fechas</li>`;

    fechas.forEach(fecha => {
        lista.innerHTML += `
            <li class="fecha-item cursor-pointer px-2 py-1 rounded hover:bg-[#1a1a1a]"
                data-id="${fecha.id}"> ${fecha.nombre}
            </li>
        `;
    });

    document.querySelectorAll(".fecha-item").forEach(item => {
        item.addEventListener("click", () => {

            document.querySelectorAll(".fecha-item")
                .forEach(i => i.classList.remove("bg-[#1a1a1a]", "text-white"));
            item.classList.add("bg-[#1a1a1a]", "text-white");
            const id = item.dataset.id;
            fechaSeleccionada = id ? Number(id) : null;
            cargarPartidos();
        });
    });
}

// filtro por estados
function initFiltroEstados() {
    const botones = document.querySelectorAll(".estado-tab");

    botones.forEach(btn => {
        btn.addEventListener("click", () => {

            botones.forEach(b => {
                b.classList.remove("bg-[#1a1a1a]", "text-white");
                b.classList.add("text-gray-400");
            });

            btn.classList.add("bg-[#1a1a1a]", "text-white");
            btn.classList.remove("text-gray-400");
            estadoSeleccionado = btn.dataset.estado;
            cargarPartidos();
        });
    });

    document.querySelector('[data-estado="POR_JUGARSE"]')?.click();
}

// carga de partidos
async function cargarPartidos() {
    mostrarSpinner("contenedor-partidos");

    try {
        let url = `${API_URL}/api/partidos/estado/${estadoSeleccionado}`;

        const res = await fetch(url);
        const result = await res.json();
        let partidos = result.data ?? [];

        if (fechaSeleccionada) {
            partidos = partidos.filter(p =>
                String(p.fechaId) === String(fechaSeleccionada)
            );
        }
        renderVista(partidos);

    } catch (e) {
        console.error(e);
    }
}

// vista
function renderVista(partidos) {
    renderPartidos(partidos);
    updateHeader(partidos);
}

// actualizar header
function updateHeader(partidos) {
    const badge = document.getElementById("fecha-badge");
    const contador = document.getElementById("partidos-count");

    badge.textContent = fechaSeleccionada
        ? (todasLasFechas.find(f => String(f.id) === String(fechaSeleccionada))?.nombre ?? "Fecha")
        : "Todas las fechas";

    const cant = partidos.length;
    contador.textContent =
        `${cant} ${cant === 1 ? "partido disponible" : "partidos disponibles"}`;
}

// render de los partidos
async function renderPartidos(partidos=[]) {
    const contenedor = document.getElementById("contenedor-partidos");
    contenedor.innerHTML = "";

    if (!partidos.length) {
        contenedor.innerHTML = `<p class="text-gray-500 text-sm">No hay partidos para este filtro.</p>`;
        return;
    }

    const token = localStorage.getItem("accessToken");

    for (const p of partidos) {
        const esPorJugarse = p.estado === "POR_JUGARSE";
        const esEnJuego = p.estado === "EN_JUEGO";
        const esFinalizado = p.estado === "FINALIZADO";

        let miPronostico = null;

        if (token) {
            miPronostico = await obtenerMiPronostico(p.id);
        }

        const golesLocal = miPronostico?.golesLocalPronosticados ?? 0;
        const golesVisitante = miPronostico?.golesVisitantePronosticados ?? 0;
        const textoBoton = miPronostico ? "Editar apuesta" : "Realizar apuesta";

        let mensajePronostico = "";
        if (miPronostico) {
            if (miPronostico.puntosObtenidos === 3) {
                mensajePronostico = "¡Resultado exacto! (+3 puntos)";
            } else if (miPronostico.puntosObtenidos === 1) {
                mensajePronostico = "Tendencia acertada (+1 punto)";
            } else {
                mensajePronostico = "Pronóstico incorrecto (0 puntos)";
            }
        }

        contenedor.innerHTML += `
            <div class="match-card bg-gradient-to-b from-[#1a1a1a] to-[#141414] border border-[#262626] rounded-[6px] overflow-hidden w-full shadow-lg shadow-black/30">
                <!-- HEADER PARTIDO -->
                <div class="flex flex-col gap-1.5 px-4 pt-5 pb-4 text-center border-b border-[#222] bg-black/20">
                    <div class="flex items-center justify-center gap-3">
                        <span class="text-lg md:text-xl font-bold uppercase text-white tracking-tight">${p.equipoLocal}</span>
                        <span class="text-gray-500 font-medium text-xs">vs</span>
                        <span class="text-lg md:text-xl font-bold uppercase text-white tracking-tight">${p.equipoVisitante}</span>
                    </div>
                    <span class="text-[10px] text-gray-500 uppercase tracking-widest font-medium mt-1">
                        ${p.fechaNombre ?? ""}
                    </span>
                    <span class="text-[10px] text-gray-500">
                        Inicio: ${p.fechaHorarioInicio ? new Date(p.fechaHorarioInicio).toLocaleString() : "-"}
                    </span>
                    <span class="text-[10px] px-2.5 py-1 rounded-full bg-[#0d0d0d] text-gray-400 w-fit mx-auto mt-1 border border-[#262626] uppercase tracking-wide font-semibold">
                        ${p.estado === "POR_JUGARSE" ? "Por jugarse" : p.estado === "EN_JUEGO" ? "En juego" : p.estado === "FINALIZADO" ? "Finalizado" : p.estado}
                    </span>
                </div>

                <!-- BODY -->
                <div class="px-4 py-4 flex flex-col gap-4">
                    <p class="text-[11px] text-gray-500 text-center uppercase tracking-wide font-medium">
                        ${esFinalizado ? "Resultado final del partido" : esEnJuego ? "Partido en juego" : "Tu pronóstico de goles"}
                    </p>

                    <!-- POR JUGARSE -->
                    ${esPorJugarse ? `
                        <div class="flex flex-col gap-3">
                            <!-- LOCAL -->
                            <div class="flex justify-between items-center bg-[#0d0d0d] border border-[#222] rounded-[4px] px-3 py-2.5">
                                <span class="text-sm text-white font-medium truncate pr-2">${p.equipoLocal}</span>
                                <div class="flex items-center gap-3 shrink-0">
                                    <button class="w-7 h-7 flex items-center justify-center rounded-full bg-[#1f1f1f] hover:bg-[#05AC2E] text-white text-base font-bold transition" onclick="changeGoal(this,-1)">−</button>
                                    <div class="goal-display text-white font-bold text-lg w-6 text-center">${golesLocal}</div>
                                    <button class="w-7 h-7 flex items-center justify-center rounded-full bg-[#1f1f1f] hover:bg-[#05AC2E] text-white text-base font-bold transition" onclick="changeGoal(this,1)">+</button>
                                </div>
                            </div>
                            <!-- VISITANTE -->
                            <div class="flex justify-between items-center bg-[#0d0d0d] border border-[#222] rounded-[4px] px-3 py-2.5">
                                <span class="text-sm text-white font-medium truncate pr-2">${p.equipoVisitante}</span>
                                <div class="flex items-center gap-3 shrink-0">
                                    <button class="w-7 h-7 flex items-center justify-center rounded-full bg-[#1f1f1f] hover:bg-[#05AC2E] text-white text-base font-bold transition" onclick="changeGoal(this,-1)">−</button>
                                    <div class="goal-display text-white font-bold text-lg w-6 text-center">${golesVisitante}</div>
                                    <button class="w-7 h-7 flex items-center justify-center rounded-full bg-[#1f1f1f] hover:bg-[#05AC2E] text-white text-base font-bold transition" onclick="changeGoal(this,1)">+</button>
                                </div>
                            </div>
                            <button
                                class="bg-[#05AC2E] hover:bg-white hover:text-black text-white text-xs font-bold uppercase tracking-wide px-5 py-2.5 rounded-[4px] transition shadow-md shadow-[#05AC2E]/20 mt-1"
                                onclick="enviarApuesta(this, ${p.id})"
                            > ${textoBoton}
                            </button>
                        </div>
                    ` : ""}

                    <!-- EN JUEGO -->
                    ${esEnJuego ? `
                        <div class="flex items-center justify-center gap-2 text-center text-yellow-400 text-sm font-medium bg-yellow-400/5 border border-yellow-400/20 rounded-[4px] py-3">
                            <span class="relative flex h-2 w-2">
                                <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-yellow-400 opacity-75"></span>
                                <span class="relative inline-flex rounded-full h-2 w-2 bg-yellow-400"></span>
                            </span>
                            No se permiten apuestas en este momento
                        </div>
                    ` : ""}

                    <!-- FINALIZADO -->
                    ${esFinalizado ? `
                        <div class="flex flex-col gap-2 text-center bg-[#0d0d0d] border border-[#222] rounded-[4px] py-4">
                            <div class="text-2xl font-extrabold text-white tracking-wide">
                                ${p.golesLocal ?? 0} <span class="text-gray-600">-</span> ${p.golesVisitante ?? 0}
                            </div>
                            ${p.resultadoTendencia ? `
                                <div class="text-[11px] text-gray-500 uppercase tracking-wide">
                                    Tendencia: <span class="text-gray-300 font-semibold">${p.resultadoTendencia}</span>
                                </div>
                            ` : ""}
                            ${miPronostico ? `
                            <div class="border-t border-[#222] pt-4 mt-3 flex flex-col gap-2 text-center">
                                <p class="text-[11px] text-gray-500 uppercase tracking-wide">Tu pronóstico</p>
                                <div class="text-xl font-bold text-white">
                                    ${miPronostico.golesLocalPronosticados}
                                    <span class="text-gray-600">-</span>
                                    ${miPronostico.golesVisitantePronosticados}
                                </div>
                                <p class="text-xs text-[#05AC2E] font-semibold">
                                    ${mensajePronostico}
                                </p>
                            </div>
                        ` : ""}
                        </div>
                    ` : ""}
                </div>
            </div>
            `;
    };
}

// obtener pronosticos del partido
async function obtenerMiPronostico(partidoId) {
    const token = getAccessToken();

    if (!token) return null;

    try {
        const res = await fetchConToken(
            `${API_URL}/api/pronosticos/mi-pronostico/${partidoId}`
        );

        if (!res.ok) return null;

        return await res.json();

    } catch (e) {
        console.error(e);
        return null;
    }
}

// stepper
function changeGoal(btn, delta) {
    const row = btn.closest("div");
    const display = row.querySelector(".goal-display");

    let val = Number(display.textContent || 0);
    val = Math.max(0, val + delta);

    display.textContent = val;
}

// apuesta
function enviarApuesta(btn, partidoId) {
    const token = getAccessToken();

    if (!token) {
        Swal.fire({
            icon: "warning",
            title: "Tenés que iniciar sesión para poder apostar.",
            confirmButtonText: "Iniciar sesión"
        }).then((result) => {
            if (result.isConfirmed) {
                window.location.href = "../static/html/login.html";
            }
        });

        return;
    }

    const card = btn.closest(".match-card");
    const goles = card.querySelectorAll(".goal-display");

    const body = {
        golesLocalPronosticados: Number(goles[0]?.textContent || 0),
        golesVisitantePronosticados: Number(goles[1]?.textContent || 0)
    };

    fetchConToken(`${API_URL}/api/pronosticos/${partidoId}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(body)
    })
    .then(async res => {
        const data = await res.json().catch(() => null);

        if (!res.ok) {
            throw new Error(data?.message || "Error");
        }

        Swal.fire({
            icon: "success",
            title: "Apuesta registrada correctamente."
        });

        cargarPreviewApuestas();
        cargarPartidos();
    })
    .catch(err => {
        Swal.fire({
            icon: "error",
            title: "Error",
            text: err.message
        });
    });
}

// grupos
async function cargarPreviewGrupo() {
    const token = getAccessToken();

    if (!token) {
        document.getElementById("preview-grupo").innerHTML = `
            <p class="text-sm text-gray-500">
                Iniciá sesión para ver tus grupos.
            </p>
        `;
        return;
    }

    mostrarSpinner("preview-grupo");

    try {
        const res = await fetchConToken(
            `${API_URL}/api/grupos/mis-grupos`
        );

        const misGrupos = await res.json();

        if (!misGrupos.length) {
            document.getElementById("preview-grupo").innerHTML = `
                <p class="text-sm text-gray-500">
                    No pertenecés a ningún grupo.
                </p>
            `;
            return;
        }

        const grupo = misGrupos[0];

        const rankingRes = await fetchConToken(
            `${API_URL}/api/grupos/${grupo.grupoId}/ranking`
        );

        const ranking = await rankingRes.json();

        renderPreviewGrupo(grupo, ranking);

    } catch (e) {
        console.error(e);
    }
}

// render grupo
function renderPreviewGrupo(grupo, ranking) {
    const cont = document.getElementById("preview-grupo");

    cont.innerHTML = `
        <p class="text-[10px] font-bold uppercase tracking-[2px] text-gray-500 mb-1">
            ${grupo.grupoNombre}
        </p>

        <ul class="space-y-2">
            ${ranking.slice(0, 4).map((u, i) => `
                <li class="flex justify-between text-sm">
                        <span>
                            ${i === 0 ? "🥇" :
                            i === 1 ? "🥈" :
                            i === 2 ? "🥉" : ""}
                            ${u.nombre}
                        </span>
                    <span class="text-[#05AC2E] text-xs font-bold">
                        ${u.puntosTotales} pts
                    </span>
                </li>
            `).join("")}
        </ul>
    `;
}

// apuestas sidebar
async function cargarPreviewApuestas() {
    const token = getAccessToken();

    if (!token) {
        document.getElementById("preview-apuestas").innerHTML = `
            <p class="text-sm text-gray-500">
                Iniciá sesión para ver tus apuestas.
            </p>
        `;
        return;
    }

    mostrarSpinner("preview-apuestas");

    try {
        const res = await fetchConToken(
            `${API_URL}/api/pronosticos/mis-pronosticos`
        );

        if (!res.ok) return;

        const pronosticos = await res.json();

        renderPreviewApuestas(pronosticos);

    } catch (e) {
        console.error(e);
    }
}

// sidebar apuestas
function renderPreviewApuestas(pronosticos) {
    const cont = document.getElementById("preview-apuestas");

    if (!pronosticos.length) {
        cont.innerHTML = `
            <p class="text-sm text-gray-500">
                Todavía no realizaste apuestas.
            </p>
        `;
        return;
    }

    cont.innerHTML = `
    <ul class="space-y-2 text-sm text-gray-400">
        ${pronosticos.slice(0, 3).map(p => {

            const partido = todosLosPartidos.find(
                partido => partido.id === p.partidoId
            );

            const nombrePartido = partido
                ? `${partido.equipoLocal} vs ${partido.equipoVisitante}`
                : "Partido no encontrado";

            return `
                <li class="flex items-center justify-between">
                    <span class="truncate">
                        ${nombrePartido}
                    </span>

                    <span class="text-[#05AC2E] text-xs font-semibold">
                        ${p.golesLocalPronosticados}-${p.golesVisitantePronosticados}
                    </span>
                </li>
            `;
        }).join("")}
    </ul>
    `;
}

// todos los partidos
async function cargarTodosLosPartidos() {
    try {
        const res = await fetch(`${API_URL}/api/partidos`);
        const result = await res.json();

        todosLosPartidos = result.data ?? result ?? [];

    } catch (e) {
        console.error(e);
    }
}


async function cargarRankingGlobal() {
    mostrarSpinner("ranking-global-lista");

    try {
        const res = await fetch(`${API_URL}/api/rankings/global`);

        if (!res.ok) throw new Error();

        const ranking = await res.json();

        renderRankingGlobal(ranking);

    } catch (e) {
        console.error(e);
    }
}

function renderRankingGlobal(ranking = []) {
    const lista = document.getElementById("ranking-global-lista");

    if (!lista) return;

    if (!ranking.length) {
        lista.innerHTML = `
            <li class="text-gray-500 text-center text-xs py-2">
                Todavía no hay ranking para mostrar.
            </li>
        `;
        return;
    }

    const top3 = ranking.slice(0, 3);

    const medalla = (index) => {
        if (index === 0) return "🥇";
        if (index === 1) return "🥈";
        if (index === 2) return "🥉";
        return "";
    };

    const filaHTML = (usuario, index, claseExtra = "") => {
        const esUsuarioActual = usuario.id === usuarioActualId;

        return `
            <li class="flex items-center justify-between ${claseExtra} ${esUsuarioActual ? "text-white font-semibold" : ""}">
                <span class="${esUsuarioActual ? "text-[#05AC2E]" : "text-gray-600"}">
                    ${medalla(index) || `#${index + 1}`}
                </span>
                <span>${usuario.nombre}</span>
                <span class="${esUsuarioActual ? "text-[#05AC2E]" : ""} font-bold text-xs">
                    ${usuario.puntosTotales ?? 0} pts
                </span>
            </li>
        `;
    };

    let html = top3.map((u, i) => filaHTML(u, i)).join("");

    if (usuarioActualId) {
        const posicionPropia = ranking.findIndex(u => u.id === usuarioActualId);
        const estaEnTop3 = posicionPropia >= 0 && posicionPropia < 3;

        if (!estaEnTop3 && posicionPropia >= 0) {
            html += filaHTML(
                ranking[posicionPropia],
                posicionPropia,
                "border-t border-[#222] pt-2 mt-2"
            );
        }
    }

    lista.innerHTML = html;
}

