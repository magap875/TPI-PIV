let todosLosPartidos = [];
let misPronosticos = [];
let filtroActual = "todas";
let usuarioActualId = null;
let usuarioActual = null;
let rankingGlobal = [];

function pluralizar(cantidad, singular, plural) {
    return cantidad === 1 ? singular : plural;
}

document.addEventListener("DOMContentLoaded", async () => {
    mostrarSpinnerHeaderPerfil();

    await cargarRankingGlobal();
    await cargarUsuarioActual();

    await cargarPartidos();
    await cargarMisPronosticos();
    cargarMisGrupos();
});

function mostrarSpinner(contenedorId) {
    const contenedor = document.getElementById(contenedorId);
    if (!contenedor) return;

    contenedor.innerHTML = `
        <div class="col-span-full flex items-center justify-center py-8">
            <div class="w-6 h-6 border-2 border-[#2a2a2a] border-t-[#05AC2E] rounded-full animate-spin"></div>
        </div>
    `;
}


function mostrarSpinnerHeaderPerfil() {
    const nombreEl = document.getElementById("nombre-perfil");
    const puntosEl = document.getElementById("puntos-perfil");
    const apuestasEl = document.getElementById("apuestas-perfil");

    if (nombreEl) nombreEl.textContent = "...";
    if (puntosEl) puntosEl.textContent = "—";
    if (apuestasEl) apuestasEl.textContent = "—";
}

async function cargarUsuarioActual() {
    const token = localStorage.getItem("accessToken");

    if (!token) return;

    try {
        const res = await fetchConToken(`${API_URL}/api/usuarios/me`);

        if (!res.ok) throw new Error("Error al obtener usuario");

        const response = await res.json();
        const usuario = response.data ?? response;

        usuarioActual = usuario;

        actualizarNavbar(usuario);
        actualizarHeaderPerfil(usuario);
        actualizarFormularioDatos(usuario);

    } catch (err) {
        console.error(err);
    }
}

function actualizarNavbar(usuario) {
    const nombre = usuario.nombre ?? "Usuario";

    const nombreEl = document.getElementById("nombre-usuario");
    if (nombreEl) nombreEl.textContent = nombre;

    const avatarEl = document.getElementById("avatar");
    if (avatarEl) {
        const iniciales = nombre
            .split(" ")
            .map(n => n[0])
            .slice(0, 2)
            .join("")
            .toUpperCase();

        avatarEl.textContent = iniciales;
    }
}

function actualizarHeaderPerfil(usuario) {
    usuarioActualId = usuario.id ?? null;

    const nombre = usuario.nombre ?? "Usuario";

    const nombreEl = document.getElementById("nombre-perfil");
    if (nombreEl) nombreEl.textContent = nombre;

    const avatarEl = document.getElementById("avatar-perfil");
    if (avatarEl) {
        const iniciales = nombre
            .split(" ")
            .map(n => n[0])
            .slice(0, 2)
            .join("")
            .toUpperCase();

        avatarEl.textContent = iniciales;
    }

    const puntosEl = document.getElementById("puntos-perfil");
    if (puntosEl) puntosEl.textContent = usuario.puntosTotales ?? 0;

    const posicion = rankingGlobal.findIndex(u => u.id === usuario.id);

    const rankingEl = document.getElementById("ranking-perfil");
    if (rankingEl) {
        const posicion = rankingGlobal.findIndex(u => u.id === usuario.id);
        rankingEl.textContent =
            posicion !== -1 ? `#${posicion + 1}` : "-";
    }
}

function actualizarApuestasPerfil() {
    const cantidad = misPronosticos.length;

    const apuestasEl = document.getElementById("apuestas-perfil");
    if (apuestasEl) apuestasEl.textContent = cantidad;

    const labelEl = document.getElementById("label-apuestas-perfil");
    if (labelEl) labelEl.textContent = pluralizar(cantidad, "Apuesta", "Apuestas");
}

async function cargarPartidos() {
    try {
        const res = await fetch(`${API_URL}/api/partidos`);
        const result = await res.json();

        todosLosPartidos = result.data ?? [];

    } catch (e) {
        console.error(e);
    }
}

async function cargarMisPronosticos() {
    const token = localStorage.getItem("accessToken");

    if (!token) return;

    mostrarSpinner("mis-apuestas-container");

    try {
        const res = await fetchConToken(`${API_URL}/api/pronosticos/mis-pronosticos`);

        if (!res.ok) return;

        misPronosticos = await res.json();

        renderMisPronosticos();
        actualizarApuestasPerfil();

    } catch (e) {
        console.error(e);
    }
}

function setTab(btn, filtro) {

    document.querySelectorAll(".tab-btn")
        .forEach(b => b.classList.remove("active"));

    btn.classList.add("active");
    filtroActual = filtro;
    renderMisPronosticos();
}

function renderMisPronosticos() {
    const container = document.getElementById("mis-apuestas-container");

    let pronosticos = [...misPronosticos];

    if (filtroActual === "acertadas") {
        pronosticos = pronosticos.filter(p => p.puntosObtenidos > 0);
    }

    if (filtroActual === "pendientes") {
        pronosticos = pronosticos.filter(p => {
            const partido = todosLosPartidos.find(
                x => x.id === p.partidoId
            );

            return partido?.estado === "POR_JUGARSE";
        });
    }

    if (!pronosticos.length) {
        container.innerHTML = `
            <div class="card p-6 text-center text-gray-500">
                No hay apuestas para mostrar.
            </div>
        `;
        return;
    }

    container.innerHTML = `
        <div class="card overflow-hidden">

            ${pronosticos.map((p, index) => {

                const partido = todosLosPartidos.find(
                    x => x.id === p.partidoId
                );

                if (!partido) return "";

                const pendiente =
                    partido.estado === "POR_JUGARSE";

                const exacto =
                    p.puntosObtenidos === 3;

                const tendencia =
                    p.puntosObtenidos === 1;

                let badge = "";
                let puntos = "";

                if (pendiente) {
                    badge = `
                        <span class="badge-result badge-pending">
                            Por jugar
                        </span>
                    `;

                    puntos = "";
                } else if (exacto) {
                    badge = `
                        <span class="badge-result badge-win">
                            ✔ Exacto
                        </span>
                    `;

                    puntos = `
                        <p class="text-[#05AC2E] font-bold text-sm mt-1">
                            +3 pts
                        </p>
                    `;
                } else if (tendencia) {
                    badge = `
                        <span class="badge-result badge-win">
                            ✔ Ganador
                        </span>
                    `;

                    puntos = `
                        <p class="text-[#05AC2E] font-bold text-sm mt-1">
                            +1 pt
                        </p>
                    `;
                } else {
                    badge = `
                        <span class="badge-result">
                            ✖ Falló
                        </span>
                    `;

                    puntos = `
                        <p class="text-red-400 font-bold text-sm mt-1">
                            0 pts
                        </p>
                    `;
                }

                return `
                    <div class="flex items-center gap-4 px-5 py-3.5
                        ${index !== pronosticos.length - 1 ? "border-b border-[#1e1e1e]" : ""}
                    ">

                        <div class="flex flex-col gap-0.5 min-w-[220px]">
                            <div class="flex items-center gap-2">
                                <span class="title-font font-bold uppercase text-sm">
                                    ${partido.equipoLocal}
                                </span>

                                <span class="text-gray-600 text-xs px-1">
                                    vs
                                </span>

                                <span class="title-font font-bold uppercase text-sm">
                                    ${partido.equipoVisitante}
                                </span>
                            </div>

                            <div class="flex items-center gap-2">
                                <span class="text-[10px] text-gray-500 uppercase tracking-wide">
                                    ${partido.fechaNombre ?? ""}
                                </span>

                                <span class="text-[9px] px-1.5 py-0.5 rounded-full bg-[#0d0d0d] text-gray-500 border border-[#262626] uppercase tracking-wide font-semibold">
                                    ${partido.estado === "POR_JUGARSE" ? "Por jugarse" : partido.estado === "EN_JUEGO" ? "En juego" : partido.estado === "FINALIZADO" ? "Finalizado" : partido.estado}
                                </span>
                            </div>
                        </div>

                        <div class="flex-1 text-center">
                            <span class="text-xs text-gray-500">
                                Tu predicción
                            </span>

                            ${pendiente ? `
                                <!-- editable: fila por equipo, igual patrón que renderPartidos -->
                                <div
                                    class="apuesta-editor flex flex-col gap-1.5 mt-1"
                                    data-goles-local="${p.golesLocalPronosticados}"
                                    data-goles-visitante="${p.golesVisitantePronosticados}"
                                >
                                    <div class="flex justify-between items-center bg-[#0d0d0d] border border-[#222] rounded-[4px] px-3 py-1">
                                        <span class="text-xs text-white font-medium truncate pr-2">${partido.equipoLocal}</span>
                                        <div class="flex items-center gap-2 shrink-0">
                                            <button type="button" class="step-btn" onclick="changeGoalMisApuesta(this, 'local', -1)">−</button>
                                            <span class="goal-display-local title-font text-base font-bold text-white w-4 text-center">${p.golesLocalPronosticados}</span>
                                            <button type="button" class="step-btn" onclick="changeGoalMisApuesta(this, 'local', 1)">+</button>
                                        </div>
                                    </div>

                                    <div class="flex justify-between items-center bg-[#0d0d0d] border border-[#222] rounded-[4px] px-3 py-1">
                                        <span class="text-xs text-white font-medium truncate pr-2">${partido.equipoVisitante}</span>
                                        <div class="flex items-center gap-2 shrink-0">
                                            <button type="button" class="step-btn" onclick="changeGoalMisApuesta(this, 'visitante', -1)">−</button>
                                            <span class="goal-display-visitante title-font text-base font-bold text-white w-4 text-center">${p.golesVisitantePronosticados}</span>
                                            <button type="button" class="step-btn" onclick="changeGoalMisApuesta(this, 'visitante', 1)">+</button>
                                        </div>
                                    </div>

                                    <button
                                        class="bg-[#05AC2E] w-full text-white text-xs font-bold px-3 py-1 rounded mt-0.5 self-center"
                                        onclick="upsertMisApuesta(this, ${p.partidoId})"
                                    >
                                        Actualizar apuesta
                                    </button>
                                </div>
                            ` : `
                                <p class="title-font text-xl font-bold text-white">
                                    ${p.golesLocalPronosticados}
                                    —
                                    ${p.golesVisitantePronosticados}
                                </p>
                            `}
                        </div>

                        <div class="flex-1 text-center hidden sm:block">
                            <span class="text-xs text-gray-500">
                                Resultado
                            </span>

                            <p class="title-font text-xl font-bold text-white">
                                ${
                                    pendiente
                                        ? "— —"
                                        : `${partido.golesLocal} — ${partido.golesVisitante}`
                                }
                            </p>
                        </div>

                        <div class="text-right">
                            ${badge}
                            ${puntos}
                        </div>

                    </div>
                `;
            }).join("")}

            <div class="green-bar"></div>

        </div>
    `;
}

function changeGoalMisApuesta(btn, equipo, delta) {
    const editor = btn.closest(".apuesta-editor");

    const dataAttr = equipo === "local" ? "data-goles-local" : "data-goles-visitante";

    let valorActual = Number(editor.getAttribute(dataAttr)) || 0;
    valorActual = Math.max(0, valorActual + delta);

    editor.setAttribute(dataAttr, valorActual);

    const display = editor.querySelector(`.goal-display-${equipo}`);
    display.textContent = valorActual;
}

async function upsertMisApuesta(btn, partidoId) {
    const editor = btn.closest(".apuesta-editor");

    const local = Number(editor.getAttribute("data-goles-local")) || 0;
    const visitante = Number(editor.getAttribute("data-goles-visitante")) || 0;

    const body = {
        golesLocalPronosticados: local,
        golesVisitantePronosticados: visitante
    };

    try {
        const res = await fetchConToken(`${API_URL}/api/pronosticos/${partidoId}`, {
            method: "POST", // upsert
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });

        if (!res.ok) throw new Error();

        Swal.fire({
            icon: "success",
            title: "Apuesta actualizada"
        });

        cargarMisPronosticos();

        if (typeof cargarPreviewApuestas === "function") {
            cargarPreviewApuestas();
        }

    } catch (e) {
        Swal.fire({
            icon: "error",
            title: "Error al actualizar apuesta"
        });
    }
}

async function cargarMisGrupos() {
    const token = localStorage.getItem("accessToken");

    if (!token) return;

    mostrarSpinner("contenedor-grupos");

    try {
        const res = await fetchConToken(`${API_URL}/api/grupos/mis-grupos`);

        if (!res.ok) throw new Error("Error al obtener los grupos");

        const misGrupos = await res.json();

        // por cada grupo, traemos su ranking en paralelo
        const gruposConRanking = await Promise.all(
            misGrupos.map(async grupo => {
                const ranking = await obtenerRankingGrupo(grupo.grupoId);
                return { grupo, ranking };
            })
        );

        renderMisGrupos(gruposConRanking);

    } catch (e) {
        console.error(e);
    }
}

async function obtenerRankingGrupo(grupoId) {
    try {
        const res = await fetchConToken(`${API_URL}/api/grupos/${grupoId}/ranking`);

        if (!res.ok) return [];

        return await res.json();

    } catch (e) {
        console.error(e);
        return [];
    }
}

function renderMisGrupos(gruposConRanking) {
    const contenedor = document.getElementById("contenedor-grupos");

    if (!contenedor) return;

    if (!gruposConRanking.length) {
        contenedor.innerHTML = `
            <div class="card p-6 text-center text-gray-500 col-span-full">
                No pertenecés a ningún grupo todavía.
            </div>
        `;
        return;
    }

    contenedor.innerHTML = gruposConRanking.map(({ grupo, ranking }) => {

        const cantidadMiembros = ranking.length;
        const codigoGrupo = grupo.codigoInvitacion;

        const filasRanking = ranking.map((usuario, index) => {

            const esUsuarioActual = usuario.id === usuarioActualId;
            const filaId = `${grupo.grupoId}-${usuario.id}`;

            const iniciales = (usuario.nombre ?? "??")
                .split(" ")
                .map(n => n[0])
                .slice(0, 2)
                .join("")
                .toUpperCase();

            return `
                <li>
                    <div
                        class="rank-row flex items-center gap-3 px-2 py-1.5 rounded-[3px] cursor-pointer
                            ${esUsuarioActual ? "bg-[#05AC2E0A] border border-[#05AC2E22]" : ""}
                        "
                        onclick="toggleApuestasMiembro('${filaId}', ${usuario.id})"
                    >
                        <span class="text-xs w-5 text-center font-bold
                            ${esUsuarioActual ? "text-[#05AC2E]" : "text-gray-600"}
                        ">
                            ${index + 1}
                        </span>

                        <div class="avatar" style="width:28px;height:28px;font-size:11px;
                            ${esUsuarioActual ? "" : "background:#1a1a1a; border-color:#333; color:#666;"}
                        ">
                            ${iniciales}
                        </div>

                        <span class="text-sm flex-1
                            ${esUsuarioActual ? "text-white font-semibold" : "text-gray-400"}
                        ">
                            ${usuario.nombre}
                        </span>

                        <span class="font-bold text-sm
                            ${esUsuarioActual ? "text-white" : "text-gray-500"}
                        ">
                            ${usuario.puntosTotales ?? 0} pts
                        </span>

                        <svg id="chevron-${filaId}" class="w-3.5 h-3.5 text-gray-600 transition-transform" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                        </svg>
                    </div>

                    <div id="apuestas-miembro-${filaId}" class="hidden px-2 pb-2 pt-1">
                        <!-- se llena dinámicamente al abrir -->
                    </div>
                </li>
            `;
        }).join("");

        return `
            <div class="card overflow-hidden h-full flex flex-col">
                <div class="px-5 pt-4 pb-3 flex-1">
                    <div class="flex items-center justify-between mb-4">
                        <div>
                            <p class="text-[10px] font-bold uppercase tracking-[2px] text-gray-500 mb-0.5">Grupo privado</p>
                            <h3 class="title-font text-lg font-bold uppercase">${grupo.grupoNombre}</h3>
                            <span class="inline-flex items-center gap-1.5 text-[10px] font-bold text-gray-500 bg-[#05AC2E14] border border-[#05AC2E33] rounded-[3px] px-2 py-1 tracking-wide">
                            ${grupo.codigoInvitacion}
                        </span>
                        </div>
                        <span class="section-badge">${cantidadMiembros} ${pluralizar(cantidadMiembros, "miembro", "miembros")}</span>
                    </div>
                    <div class="flex items-center gap-2 mb-4">
                        <span class="text-[10px] text-gray-600">
                            Fecha de ingreso: ${new Date(grupo.fechaIngreso).toLocaleDateString("es-AR")}
                        </span>
                    </div>
                    <ul class="space-y-2">
                        ${filasRanking}
                    </ul>
                </div>
                <div class="px-5 pb-4 pt-2 flex justify-end">
                    <button
                        class="text-xs font-semibold text-red-400 border border-red-900 px-4 py-1.5 rounded-[3px] hover:bg-red-900/20 transition"
                        onclick="salirDelGrupo(${grupo.grupoId})"
                    >
                        Salir del grupo
                    </button>
                </div>
                <div class="green-bar"></div>
            </div>
        `;
    }).join("");
}

const cachePronosticosPorUsuario = {};

async function toggleApuestasMiembro(filaId, usuarioId) {
    const panel = document.getElementById(`apuestas-miembro-${filaId}`);
    const chevron = document.getElementById(`chevron-${filaId}`);

    const estaAbierto = !panel.classList.contains("hidden");

    if (estaAbierto) {
        panel.classList.add("hidden");
        chevron.style.transform = "";
        return;
    }

    chevron.style.transform = "rotate(180deg)";
    panel.classList.remove("hidden");
    panel.innerHTML = `
        <p class="text-xs text-gray-600 text-center py-2">Cargando apuestas...</p>
    `;

    try {
        let pronosticos = cachePronosticosPorUsuario[usuarioId];

        if (!pronosticos) {
            const res = await fetchConToken(`${API_URL}/api/pronosticos/usuario/${usuarioId}`);

            if (!res.ok) throw new Error();

            pronosticos = await res.json();
            cachePronosticosPorUsuario[usuarioId] = pronosticos;
        }

        panel.innerHTML = renderApuestasMiembro(pronosticos);

    } catch (e) {
        console.error(e);
        panel.innerHTML = `
            <p class="text-xs text-red-400 text-center py-2">
                No se pudieron cargar las apuestas.
            </p>
        `;
    }
}

function renderApuestasMiembro(pronosticos) {
    const jugados = pronosticos.filter(p => {
        const partido = todosLosPartidos.find(x => x.id === p.partidoId);
        return partido && partido.estado === "FINALIZADO";
    });

    if (!jugados.length) {
        return `
            <p class="text-xs text-gray-600 text-center py-2">
                Todavía no tiene apuestas con resultado.
            </p>
        `;
    }

    const filas = jugados.map(p => {

        const partido = todosLosPartidos.find(x => x.id === p.partidoId);

        const exacto = p.puntosObtenidos === 3;
        const tendencia = p.puntosObtenidos === 1;

        let badge = `<span class="badge-result">✖ Falló</span>`;
        if (exacto) badge = `<span class="badge-result badge-win">✔ Exac. (+3)</span>`;
        else if (tendencia) badge = `<span class="badge-result badge-win">✔ Tenden. (+1)</span>`;

        const horaPartido = partido.fechaHorarioInicio
            ? new Date(partido.fechaHorarioInicio).toLocaleString("es-AR", {
                  day: "2-digit",
                  month: "short",
                  hour: "2-digit",
                  minute: "2-digit"
              })
            : "";

        return `
            <div class="flex flex-col gap-2 px-3 py-2.5 bg-[#0d0d0d] border border-[#1e1e1e] rounded-[3px]">
                <div class="flex flex-col gap-0.5">
                    <span class="title-font font-bold font-sm uppercase text-white">
                        ${partido.equipoLocal} vs ${partido.equipoVisitante}
                    </span>
                    <span class="text-[10px] text-gray-600">
                        ${partido.fechaNombre ?? ""}${partido.fechaNombre && horaPartido ? " · " : ""}${horaPartido}
                    </span>
                </div>

                <div class="flex items-center justify-between gap-2">
                    <span class="text-xs text-gray-500">
                        Predijo ${p.golesLocalPronosticados}-${p.golesVisitantePronosticados}
                        <span class="text-gray-600">(${partido.golesLocal}-${partido.golesVisitante})</span>
                    </span>
                    ${badge}
                </div>
            </div>
        `;
    }).join("");

    return `
        <div class="flex flex-col gap-1.5 mt-1">
            ${filas}
        </div>
    `;
}

async function salirDelGrupo(grupoId) {
    const confirmacion = await Swal.fire({
        icon: "warning",
        title: "¿Salir del grupo?",
        text: "Vas a dejar de ver el ranking y los miembros de este grupo.",
        showCancelButton: true,
        confirmButtonText: "Sí, salir",
        cancelButtonText: "Cancelar"
    });

    if (!confirmacion.isConfirmed) return;

    try {
        const res = await fetchConToken(`${API_URL}/api/grupos/${grupoId}/salir`, {
            method: "DELETE"
        });

        if (!res.ok) throw new Error();

        Swal.fire({
            icon: "success",
            title: "Saliste del grupo"
        });

        cargarMisGrupos();

    } catch (e) {
        Swal.fire({
            icon: "error",
            title: "Error al salir del grupo"
        });
    }
}

async function abrirModalCrearGrupo() {
    const { value: nombre } = await Swal.fire({
        icon: "question",
        title: "Crear grupo",
        input: "text",
        inputLabel: "Nombre del grupo",
        inputPlaceholder: "Ej: Los Cracks",
        inputAttributes: {
            maxlength: 100
        },
        showCancelButton: true,
        confirmButtonText: "Crear",
        cancelButtonText: "Cancelar",
        inputValidator: (valor) => {
            if (!valor || valor.trim().length < 3) {
                return "El nombre debe tener al menos 3 caracteres";
            }
        }
    });

    if (!nombre) return;

    await crearGrupo(nombre.trim());
}

async function crearGrupo(nombre) {
    try {
        const res = await fetchConToken(`${API_URL}/api/grupos`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ nombre })
        });

        const data = await res.json().catch(() => null);

        if (!res.ok) {
            throw new Error(data?.message || "Error al crear el grupo");
        }

        const grupoCreado = data?.data ?? data;

        await Swal.fire({
            icon: "success",
            title: "¡Grupo creado!",
            html: `
                Compartí este código con tus amigos para que se unan:
                <p class="title-font text-2xl font-bold mt-3" style="color:#05AC2E;">
                    ${grupoCreado.codigoInvitacion}
                </p>
            `,
            confirmButtonText: "Listo"
        });

        cargarMisGrupos();

    } catch (err) {
        Swal.fire({
            icon: "error",
            title: "Error al crear el grupo",
            text: err.message
        });
    }
}

async function abrirModalUnirseGrupo() {
    const { value: codigo } = await Swal.fire({
        icon: "question",
        title: "Unirme a un grupo",
        input: "text",
        inputLabel: "Código de invitación",
        inputPlaceholder: "Ej: ABC123",
        showCancelButton: true,
        confirmButtonText: "Unirme",
        cancelButtonText: "Cancelar",
        inputValidator: (valor) => {
            if (!valor || valor.trim().length === 0) {
                return "El código de invitación es obligatorio";
            }
        }
    });

    if (!codigo) return;

    await unirseAGrupo(codigo.trim());
}

async function unirseAGrupo(codigoInvitacion) {
    try {
        const res = await fetchConToken(`${API_URL}/api/grupos/unirse`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ codigoInvitacion })
        });

        const data = await res.json().catch(() => null);

        if (!res.ok) {
            throw new Error(data?.message || "Código de invitación inválido");
        }

        Swal.fire({
            icon: "success",
            title: "Te uniste al grupo correctamente"
        });

        cargarMisGrupos();

    } catch (err) {
        Swal.fire({
            icon: "error",
            title: "No se pudo unir al grupo",
            text: err.message
        });
    }
}

// mis datos

let editandoDatos = false;

function actualizarFormularioDatos(usuario) {
    const inputNombre = document.getElementById("input-nombre");
    const inputEmail = document.getElementById("input-email");

    if (inputNombre) inputNombre.value = usuario.nombre ?? "";
    if (inputEmail) inputEmail.value = usuario.email ?? "";
}

function toggleEdit() {
    if (editandoDatos) {
        guardarDatosPerfil();
        return;
    }

    editandoDatos = true;

    const inputNombre = document.getElementById("input-nombre");
    const inputPass = document.getElementById("input-pass");
    const btn = document.getElementById("btn-editar");
    const btnCancelar = document.getElementById("btn-cancelar");
    const hint = document.getElementById("hint-editar");

    inputNombre.disabled = false;
    inputNombre.style.borderColor = "#05AC2E44";

    inputPass.disabled = false;
    inputPass.style.borderColor = "#05AC2E44";

    btn.textContent = "Guardar cambios";
    btn.classList.remove("bg-[#05AC2E]");
    btn.classList.add("bg-white", "text-[#111]");

    btnCancelar.classList.remove("hidden");
    hint.classList.remove("hidden");
}

function cancelEdit() {
    editandoDatos = false;

    if (usuarioActual) {
        actualizarFormularioDatos(usuarioActual);
    }

    document.getElementById("input-pass").value = "";

    salirDeModoEdicion();
}

function salirDeModoEdicion() {
    const inputNombre = document.getElementById("input-nombre");
    const inputPass = document.getElementById("input-pass");
    const btn = document.getElementById("btn-editar");
    const btnCancelar = document.getElementById("btn-cancelar");
    const hint = document.getElementById("hint-editar");

    inputNombre.disabled = true;
    inputNombre.style.borderColor = "";

    inputPass.disabled = true;
    inputPass.style.borderColor = "";

    btn.textContent = "Editar datos";
    btn.classList.add("bg-[#05AC2E]");
    btn.classList.remove("bg-white", "text-[#111]");

    btnCancelar.classList.add("hidden");
    hint.classList.add("hidden");

    editandoDatos = false;
}

async function guardarDatosPerfil() {
    const inputNombre = document.getElementById("input-nombre");
    const inputPass = document.getElementById("input-pass");

    const nuevoNombre = inputNombre.value.trim();
    const nuevaPassword = inputPass.value.trim();

    const nombreCambio = nuevoNombre && nuevoNombre !== usuarioActual?.nombre;
    const passwordCambio = nuevaPassword.length > 0;

    if (!nombreCambio && !passwordCambio) {
        Swal.fire({
            icon: "info",
            title: "No hay cambios para guardar"
        });
        salirDeModoEdicion();
        return;
    }

    const body = {};
    if (nombreCambio) body.nombre = nuevoNombre;
    if (passwordCambio) body.contraseña = nuevaPassword;

    try {
        const res = await fetchConToken(`${API_URL}/api/usuarios/me`, {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(body)
        });

        const data = await res.json().catch(() => null);

        if (!res.ok) {
            throw new Error(data?.message || "Error al actualizar el perfil");
        }

        const usuarioActualizado = data?.data ?? data;
        usuarioActual = usuarioActualizado;

        actualizarNavbar(usuarioActualizado);
        actualizarHeaderPerfil(usuarioActualizado);
        actualizarFormularioDatos(usuarioActualizado);

        inputPass.value = "";

        Swal.fire({
            icon: "success",
            title: "Perfil actualizado correctamente"
        });

        salirDeModoEdicion();

    } catch (err) {
        Swal.fire({
            icon: "error",
            title: "Error al actualizar el perfil",
            text: err.message
        });
    }
}

async function cargarRankingGlobal() {
    try {
        const res = await fetch(`${API_URL}/api/rankings/global`);
        const result = await res.json();

        const ranking = result.data ?? result;

        renderRankingGlobal(ranking);
        rankingGlobal = ranking;
        
        if (usuarioActual) {
            actualizarHeaderPerfil(usuarioActual);
        }

    } catch (e) {
        console.error("Error cargando ranking global", e);
    }
}

function renderRankingGlobal(ranking) {
    const container = document.getElementById("ranking-global-container");
    if (!container) return;

    if (!ranking.length) {
        container.innerHTML = `
            <div class="px-5 py-4 text-center text-gray-500">
                No hay datos de ranking.
            </div>
        `;
        return;
    }

    container.innerHTML = ranking.map((u, index) => {

        const posicion = index + 1;

        let medalla = "";
        if (posicion === 1) medalla = "🥇";
        else if (posicion === 2) medalla = "🥈";
        else if (posicion === 3) medalla = "🥉";
        else medalla = `#${posicion}`;

        const esUsuario = u.id === usuarioActualId;

        return `
            <div class="grid grid-cols-12 items-center px-5 py-3 border-b border-[#1e1e1e] rank-row
                ${esUsuario ? "bg-[#05AC2E0A] border-[#05AC2E22]" : ""}
            ">

                <span class="col-span-1 text-sm font-bold
                    ${posicion === 1 ? "text-yellow-500" :
                      posicion === 2 ? "text-gray-400" :
                      posicion === 3 ? "text-[#cd7f32]" :
                      "text-gray-600"}
                ">
                    ${medalla}
                </span>

                <div class="col-span-7 flex items-center gap-3">
                    <div class="avatar"
                        style="width:30px;height:30px;font-size:12px;
                        ${esUsuario ? "" : "background:#1a1a1a; border-color:#333; color:#aaa;"}
                        ">
                        ${u.nombre.split(" ").map(n => n[0]).slice(0,2).join("").toUpperCase()}
                    </div>

                    <span class="text-sm ${esUsuario ? "text-white font-semibold" : "text-gray-400"}">
                        ${u.nombre}
                        ${esUsuario ? `<span class="section-badge ml-2">Tú</span>` : ""}
                    </span>
                </div>

                <span class="col-span-2 text-center text-sm text-gray-400">
                    ${u.cantidadResultadosExactos}
                </span>

                <span class="col-span-2 text-right font-bold text-sm
                    ${esUsuario ? "text-[#05AC2E]" : "text-gray-400"}
                ">
                    ${u.puntosTotales} pts
                </span>
            </div>
        `;
    }).join("");
}