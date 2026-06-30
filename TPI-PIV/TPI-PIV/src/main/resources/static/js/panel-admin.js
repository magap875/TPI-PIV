/******************************
 * CONFIG + LOGIN
 ******************************/
const API_EQUIPOS = `${API_URL}/api/equipos`;
const API_FECHAS = `${API_URL}/api/fechas`;
const API_PARTIDOS = `${API_URL}/api/partidos`;
const API_USUARIOS = `${API_URL}/api/usuarios`;
const API_PRONOSTICOS = `${API_URL}/api/pronosticos`;

/******************************
 * DOM - EQUIPOS
 ******************************/
const btnNuevoEquipo = document.getElementById("btn-nuevo-equipo");
const modalEquipoEl = document.getElementById("modal-equipo");

const modalEquipoTitulo = document.getElementById("modal-equipo-titulo");
const equipoId = document.getElementById("equipo-id");
const equipoNombre = document.getElementById("equipo-nombre");

const btnCerrarModalEquipo = document.getElementById("btn-cerrar-modal-equipo");
const btnCancelarEquipo = document.getElementById("btn-cancelar-equipo");
const btnGuardarEquipo = document.getElementById("btn-guardar-equipo");

const tablaEquiposBody = document.getElementById("tabla-equipos");
const buscarEquipo = document.getElementById("buscar-equipo");
/******************************
 * DOM - FECHAS
 ******************************/
const btnNuevaFecha = document.getElementById("btn-nueva-fecha");

const contenedorFechas = document.getElementById("contenedor-fechas");

const modalFechaEl = document.getElementById("modal-fecha");

const modalFechaTitulo = document.getElementById("modal-fecha-titulo");

const fechaId = document.getElementById("fecha-id");
const fechaNombre = document.getElementById("fecha-nombre");
const fechaEstado = document.getElementById("fecha-estado");

const btnGuardarFecha = document.getElementById("btn-guardar-fecha");
const btnCancelarFecha = document.getElementById("btn-cancelar-fecha");
const btnCerrarModalFecha = document.getElementById("btn-cerrar-modal-fecha");
/******************************
 * DOM - PARTIDOS
 ******************************/
const btnNuevoPartido = document.getElementById("btn-nuevo-partido");
const tabsFechasPartidos = document.getElementById("tabs-fechas-partidos");
const tablaPartidosBody = document.getElementById("tabla-partidos");

const modalPartidoEl = document.getElementById("modal-partido");
const modalPartidoTitulo = document.getElementById("modal-partido-titulo");

const partidoId = document.getElementById("partido-id");
const partidoFecha = document.getElementById("partido-fecha");
const partidoEquipoLocal = document.getElementById("partido-equipo-local");
const partidoEquipoVisitante = document.getElementById("partido-equipo-visitante");
const partidoHorario = document.getElementById("partido-horario");

const btnGuardarPartido = document.getElementById("btn-guardar-partido");
const btnCancelarPartido = document.getElementById("btn-cancelar-partido");
const btnCerrarModalPartido = document.getElementById("btn-cerrar-modal-partido");

// resultado
const modalResultadoEl = document.getElementById("modal-resultado");
const resultadoPartidoId = document.getElementById("resultado-partido-id");
const modalResultadoTitulo = document.getElementById("modal-resultado-titulo");
const labelLocal = document.getElementById("label-local");
const labelVisitante = document.getElementById("label-visitante");
const resultadoGolesLocal = document.getElementById("resultado-goles-local");
const resultadoGolesVisitante = document.getElementById("resultado-goles-visitante");

const btnGuardarResultado = document.getElementById("btn-guardar-resultado");
const btnCancelarResultado = document.getElementById("btn-cancelar-resultado");
const btnCerrarModalResultado = document.getElementById("btn-cerrar-modal-resultado");

// estado
const modalEstadoEl = document.getElementById("modal-estado");
const estadoPartidoId = document.getElementById("estado-partido-id");
const modalEstadoTitulo = document.getElementById("modal-estado-titulo");

const btnConfirmarEstado = document.getElementById("btn-confirmar-estado");
const btnCancelarEstado = document.getElementById("btn-cancelar-estado");
const btnCerrarModalEstado = document.getElementById("btn-cerrar-modal-estado");
/******************************
 * DOM - USUARIOS
 ******************************/
const tablaUsuariosBody = document.getElementById("tabla-usuarios");
const buscarUsuario = document.getElementById("buscar-usuario");
/******************************
 * DOM - PRONÓSTICOS
 ******************************/
const selectPartidoPronosticos = document.getElementById("select-partido-pronosticos");
const selectFechaPronosticos = document.getElementById("select-fecha-pronosticos");
const tituloPronosticosPartido = document.getElementById("titulo-pronosticos-partido");
const cantidadPronosticos = document.getElementById("cantidad-pronosticos");
const tablaPronosticosBody = document.getElementById("tabla-pronosticos");
/******************************
 * DOM - DASHBOARD
 ******************************/
const statEquipos = document.getElementById("stat-equipos");
const statFechas = document.getElementById("stat-fechas");
const statFechasDetalle = document.getElementById("stat-fechas-detalle");
const statPartidos = document.getElementById("stat-partidos");
const statPartidosDetalle = document.getElementById("stat-partidos-detalle");
const statUsuarios = document.getElementById("stat-usuarios");

const tablaDashboardPartidos = document.getElementById("tabla-dashboard-partidos");
const btnDashboardVerPartidos = document.getElementById("btn-dashboard-ver-partidos");
/******************************
 * ESTADO
 ******************************/
let equiposCache = [];
let equipoEditId = null;
let fechasCache = [];
let fechaEditId = null;
let partidosCache = [];
let fechaPartidosSeleccionadaId = null;
let partidoEditId = null;
let usuariosCache = [];
let pronosticosCache = [];
let partidoPronosticosSeleccionadoId = null;
let fechaPronosticosSeleccionadaId = null;
/******************************
 * HELPERS
 ******************************/
function getToken() {
  return localStorage.getItem("accessToken");
}

function showSection(name, btn = null) {
  document.querySelectorAll(".panel-section").forEach(section => {
    section.classList.add("hidden");
    section.classList.remove("active");
  });

  document.querySelectorAll(".side-link").forEach(link => {
    link.classList.remove("active");
  });

  const section = document.getElementById(`section-${name}`);

  if (section) {
    section.classList.remove("hidden");
    section.classList.add("active");
  }

  if (btn) {
    btn.classList.add("active");
  }

  const main = document.querySelector("main");
  if (main) {
    main.scrollTop = 0;
  }
}

function authHeaders() {
  return {
    "Content-Type": "application/json",
    "Authorization": `Bearer ${getToken()}`
  };
}

function abrirModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.remove("hidden");
}

function cerrarModal(id) {
  const modal = document.getElementById(id);
  if (modal) modal.classList.add("hidden");
}

function normalizarTexto(valor) {
  return String(valor ?? "").trim().toLowerCase();
}

async function leerErrorResponse(res) {
  try {
    const data = await res.json();

    if (data.message) return data.message;
    if (data.error) return data.error;

    return JSON.stringify(data);
  } catch {
    return await res.text().catch(() => "Error desconocido");
  }
}

function mostrarError(titulo, mensaje = "Ocurrió un error inesperado") {
  Swal.fire({
    icon: "error",
    title: titulo,
    text: mensaje,
    confirmButtonText: "Aceptar"
  });
}

function mostrarExito(titulo, mensaje = "") {
  Swal.fire({
    icon: "success",
    title: titulo,
    text: mensaje,
    timer: 1600,
    showConfirmButton: false
  });
}

/******************************
 * EQUIPOS - MODAL
 ******************************/
function limpiarModalEquipo() {
  equipoEditId = null;

  if (equipoId) equipoId.value = "";
  if (equipoNombre) equipoNombre.value = "";

  if (modalEquipoTitulo) {
    modalEquipoTitulo.textContent = "Nuevo equipo";
  }
}

function abrirModalNuevoEquipo() {
  limpiarModalEquipo();
  equipoEditId = null;
  abrirModal("modal-equipo");
}

function abrirModalEditarEquipo(id) {
  const equipo = equiposCache.find(e => Number(e.id) === Number(id));

  if (!equipo) {
    mostrarError("Equipo no encontrado");
    return;
  }

  equipoEditId = equipo.id;

  if (equipoId) equipoId.value = equipo.id;
  if (equipoNombre) equipoNombre.value = equipo.nombre ?? "";

  if (modalEquipoTitulo) {
    modalEquipoTitulo.textContent = "Editar equipo";
  }

  abrirModal("modal-equipo");
}

function cerrarModalEquipo() {
  cerrarModal("modal-equipo");
  limpiarModalEquipo();
}

/******************************
 * EQUIPOS - DATA
 ******************************/
async function cargarEquipos() {
  try {
    const res = await fetch(API_EQUIPOS, {
      method: "GET",
      headers: authHeaders()
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    equiposCache = await res.json();
    renderEquipos(equiposCache);

  } catch (e) {
    console.error(e);
    mostrarError("Error cargando equipos", e.message);
  }
}

async function guardarEquipo() {
  const nombre = equipoNombre?.value?.trim();

  if (!nombre) {
    mostrarError("Campo obligatorio", "El nombre del equipo es obligatorio");
    return;
  }

  const body = {
    nombre
  };

  const editando = equipoEditId !== null && equipoEditId !== undefined;

  const url = editando ? `${API_EQUIPOS}/${equipoEditId}` : API_EQUIPOS;
  const method = editando ? "PATCH" : "POST";

  try {
    const res = await fetch(url, {
      method,
      headers: authHeaders(),
      body: JSON.stringify(body)
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    cerrarModalEquipo();

    mostrarExito(
      editando ? "Equipo actualizado" : "Equipo creado",
      editando ? "El equipo se actualizó correctamente" : "El equipo se creó correctamente"
    );

    await cargarEquipos();

  } catch (e) {
    console.error(e);
    mostrarError("Error guardando equipo", e.message);
  }
}

async function eliminarEquipo(id) {
  const result = await Swal.fire({
    icon: "warning",
    title: "¿Eliminar equipo?",
    text: "Esta acción no se puede deshacer.",
    showCancelButton: true,
    confirmButtonText: "Sí, eliminar",
    cancelButtonText: "Cancelar"
  });

  if (!result.isConfirmed) return;

  try {
    const res = await fetch(`${API_EQUIPOS}/${id}`, {
      method: "DELETE",
      headers: authHeaders()
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    mostrarExito("Equipo eliminado", "El equipo fue eliminado correctamente");

    await cargarEquipos();

  } catch (e) {
    console.error(e);
    mostrarError("Error eliminando equipo", e.message);
  }
}

/******************************
 * EQUIPOS - RENDER
 ******************************/
function renderEquipos(equipos) {
  if (!tablaEquiposBody) return;

  tablaEquiposBody.innerHTML = "";

  if (!equipos || equipos.length === 0) {
    tablaEquiposBody.innerHTML = `
      <tr class="tbl-row">
        <td colspan="4" class="text-center text-gray-500 py-4">
          No hay equipos cargados
        </td>
      </tr>
    `;
    return;
  }

  equipos.forEach((equipo, index) => {
    tablaEquiposBody.innerHTML += `
      <tr class="tbl-row">
        <td class="text-gray-600">${index + 1}</td>
        <td class="font-semibold">${equipo.nombre ?? "-"}</td>
        <td class="flex gap-2">
          <button class="btn-edit" onclick="abrirModalEditarEquipo(${equipo.id})">
            Editar
          </button>
          <button class="btn-danger" onclick="eliminarEquipo(${equipo.id})">
            Eliminar
          </button>
        </td>
      </tr>
    `;
  });
}

function filtrarEquipos() {
  const texto = normalizarTexto(buscarEquipo?.value);

  if (!texto) {
    renderEquipos(equiposCache);
    return;
  }

  const filtrados = equiposCache.filter(equipo =>
    normalizarTexto(equipo.nombre).includes(texto)
  );

  renderEquipos(filtrados);
}

/******************************
 * FECHAS - MODAL
 ******************************/
function limpiarModalFecha() {
  fechaEditId = null;

  if (fechaId) fechaId.value = "";
  if (fechaNombre) fechaNombre.value = "";

  if (modalFechaTitulo) {
    modalFechaTitulo.textContent = "Nueva fecha";
  }
}

function abrirModalNuevaFecha() {
  limpiarModalFecha();
  abrirModal("modal-fecha");
}

function abrirModalEditarFecha(id) {
  const fecha = fechasCache.find(f => Number(f.id) === Number(id));

  if (!fecha) {
    mostrarError("Fecha no encontrada");
    return;
  }

  fechaEditId = fecha.id;

  if (fechaId) fechaId.value = fecha.id;
  if (fechaNombre) fechaNombre.value = fecha.nombre ?? "";

  if (modalFechaTitulo) {
    modalFechaTitulo.textContent = "Editar fecha";
  }

  abrirModal("modal-fecha");
}

function cerrarModalFecha() {
  cerrarModal("modal-fecha");
  limpiarModalFecha();
}

/******************************
 * FECHAS - DATA
 ******************************/
async function cargarFechas() {
  try {
    const res = await fetch(API_FECHAS, {
      method: "GET",
      headers: authHeaders()
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    fechasCache = await res.json();
    renderFechas(fechasCache);

  } catch (e) {
    console.error(e);
    mostrarError("Error cargando fechas", e.message);
  }
}

async function guardarFecha() {
  const nombre = fechaNombre?.value?.trim();

  if (!nombre) {
    mostrarError("Campo obligatorio", "El nombre de la fecha es obligatorio");
    return;
  }

  const body = { nombre };

  const editando = fechaEditId !== null && fechaEditId !== undefined;
  const url = editando ? `${API_FECHAS}/${fechaEditId}` : API_FECHAS;
  const method = editando ? "PUT" : "POST";

  try {
    const res = await fetch(url, {
      method,
      headers: authHeaders(),
      body: JSON.stringify(body)
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    cerrarModalFecha();

    mostrarExito(
      editando ? "Fecha actualizada" : "Fecha creada",
      editando ? "La fecha se actualizó correctamente" : "La fecha se creó correctamente"
    );

    await cargarFechas();

  } catch (e) {
    console.error(e);
    mostrarError("Error guardando fecha", e.message);
  }
}

async function eliminarFecha(id) {
  const result = await Swal.fire({
    icon: "warning",
    title: "¿Eliminar fecha?",
    text: "Solo se puede eliminar si no tiene partidos asociados.",
    showCancelButton: true,
    confirmButtonText: "Sí, eliminar",
    cancelButtonText: "Cancelar"
  });

  if (!result.isConfirmed) return;

  try {
    const res = await fetch(`${API_FECHAS}/${id}`, {
      method: "DELETE",
      headers: authHeaders()
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    mostrarExito("Fecha eliminada", "La fecha fue eliminada correctamente");

    await cargarFechas();

  } catch (e) {
    console.error(e);
    mostrarError("Error eliminando fecha", e.message);
  }
}

/******************************
 * FECHAS - RENDER
 ******************************/
function renderFechas(fechas) {
  if (!contenedorFechas) return;

  contenedorFechas.innerHTML = "";

  if (!fechas || fechas.length === 0) {
    contenedorFechas.innerHTML = `
      <div class="card p-5 text-gray-500">
        No hay fechas cargadas
      </div>
    `;
    return;
  }

  fechas.forEach(fecha => {
    contenedorFechas.innerHTML += crearCardFecha(fecha);
  });
}

function crearCardFecha(fecha) {
  const estado = fecha.estado ?? "PROGRAMADA";
  const badge = obtenerBadgeFecha(estado);
  const cerrada = estado === "FINALIZADA";
  const enJuego = estado === "EN_JUEGO";

  const borde = enJuego ? "border-color:#05AC2E44;" : "";
  const barra = cerrada ? "background:#333;" : "";

  return `
    <div class="card overflow-hidden" style="${borde}">
      <div class="p-5">
        <div class="flex items-center justify-between mb-3">
          <h3 class="title-font text-xl font-bold uppercase">
            ${fecha.nombre ?? "-"}
          </h3>
          ${badge}
        </div>

        <p class="text-xs text-gray-600 mb-4">
          Mundial 2026
        </p>

        <div class="flex flex-col gap-2">
          <button 
            class="btn-edit" 
            style="height:34px;font-size:11px;"
            onclick="abrirModalEditarFecha(${fecha.id})"
            ${cerrada ? "disabled" : ""}
          >
            Editar
          </button>

          <button 
            class="btn-danger" 
            style="height:34px;font-size:11px;"
            onclick="eliminarFecha(${fecha.id})"
            ${cerrada ? "disabled" : ""}
          >
            Eliminar
          </button>
        </div>
      </div>

      <div class="green-bar" style="${barra}"></div>
    </div>
  `;
}

function obtenerBadgeFecha(estado) {
  if (estado === "FINALIZADA") {
    return `<span class="badge badge-finalizada">Finalizada</span>`;
  }

  if (estado === "EN_JUEGO") {
    return `
      <span class="badge badge-en-juego">
        <span class="dot-live" style="width:5px;height:5px;"></span>
        En juego
      </span>
    `;
  }

  return `<span class="badge badge-por-jugarse">Programada</span>`;
}
/******************************
 * PARTIDOS - MODAL CREAR/EDITAR
 ******************************/
function cargarSelectsPartido() {
  if (partidoFecha) {
    partidoFecha.innerHTML = `<option value="">Seleccioná una fecha</option>`;

    fechasCache.forEach(fecha => {
      partidoFecha.innerHTML += `
        <option value="${fecha.id}">
          ${fecha.nombre}
        </option>
      `;
    });
  }

  if (partidoEquipoLocal) {
    partidoEquipoLocal.innerHTML = `<option value="">Equipo local</option>`;

    equiposCache.forEach(equipo => {
      partidoEquipoLocal.innerHTML += `
        <option value="${equipo.id}">
          ${equipo.nombre}
        </option>
      `;
    });
  }

  if (partidoEquipoVisitante) {
    partidoEquipoVisitante.innerHTML = `<option value="">Equipo visitante</option>`;

    equiposCache.forEach(equipo => {
      partidoEquipoVisitante.innerHTML += `
        <option value="${equipo.id}">
          ${equipo.nombre}
        </option>
      `;
    });
  }
}

function limpiarModalPartido() {
  partidoEditId = null;

  if (partidoId) partidoId.value = "";
  if (partidoFecha) partidoFecha.value = "";
  if (partidoEquipoLocal) partidoEquipoLocal.value = "";
  if (partidoEquipoVisitante) partidoEquipoVisitante.value = "";
  if (partidoHorario) partidoHorario.value = "";

  if (modalPartidoTitulo) {
    modalPartidoTitulo.textContent = "Nuevo partido";
  }
}

function abrirModalNuevoPartido() {
  limpiarModalPartido();
  cargarSelectsPartido();

  
   if (partidoFecha) {
        partidoFecha.disabled = false;
        partidoFecha.value = "";
    }

  abrirModal("modal-partido");
}

function abrirModalEditarPartido(id) {

  const partido = partidosCache.find(p => Number(p.id) === Number(id));


  if (partidoFecha) {
    partidoFecha.value = partido.fechaId;
    partidoFecha.disabled = true;
}

  if (!partido) {
    mostrarError("Partido no encontrado");
    return;
  }

  partidoEditId = partido.id;

  cargarSelectsPartido();

  if (partidoId) partidoId.value = partido.id;
  if (partidoFecha) partidoFecha.value = partido.fechaId;
  if (partidoEquipoLocal) partidoEquipoLocal.value = partido.equipoLocalId;
  if (partidoEquipoVisitante) partidoEquipoVisitante.value = partido.equipoVisitanteId;
  if (partidoHorario) partidoHorario.value = convertirFechaParaInput(partido.fechaHorarioInicio);

  if (modalPartidoTitulo) {
    modalPartidoTitulo.textContent = "Editar partido";
  }

  abrirModal("modal-partido");
}

function cerrarModalPartido() {
  cerrarModal("modal-partido");
  limpiarModalPartido();
}

function convertirFechaParaInput(fechaIso) {
  if (!fechaIso) return "";

  const fecha = new Date(fechaIso);

  if (Number.isNaN(fecha.getTime())) {
    return "";
  }

  const year = fecha.getFullYear();
  const month = String(fecha.getMonth() + 1).padStart(2, "0");
  const day = String(fecha.getDate()).padStart(2, "0");
  const hours = String(fecha.getHours()).padStart(2, "0");
  const minutes = String(fecha.getMinutes()).padStart(2, "0");

  return `${year}-${month}-${day}T${hours}:${minutes}`;
}
/******************************
 * PARTIDOS - DATA
 ******************************/
async function cargarPartidos() {
  try {
    const res = await fetch(API_PARTIDOS, {
      method: "GET",
      headers: authHeaders()
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    const data = await res.json();

    console.log("Respuesta partidos:", data);

    partidosCache = Array.isArray(data) ? data : data.data;

    if (!Array.isArray(partidosCache)) {
      partidosCache = [];
    }

    renderTabsFechasPartidos();
    renderPartidos();

  } catch (e) {
    console.error(e);
    mostrarError("Error cargando partidos", e.message);
  }
}

async function guardarPartido() {
  const fechaId = partidoFecha?.value;
  const equipoLocalId = partidoEquipoLocal?.value;
  const equipoVisitanteId = partidoEquipoVisitante?.value;
  const fechaHorarioInicio = partidoHorario?.value;

  if (!fechaId || !equipoLocalId || !equipoVisitanteId || !fechaHorarioInicio) {
    mostrarError("Campos obligatorios", "Completá fecha, equipos y horario");
    return;
  }

  if (equipoLocalId === equipoVisitanteId) {
    mostrarError("Equipos inválidos", "El equipo local y visitante no pueden ser iguales");
    return;
  }

  const body = {
    fechaId: Number(fechaId),
    equipoLocalId: Number(equipoLocalId),
    equipoVisitanteId: Number(equipoVisitanteId),
    fechaHorarioInicio
  };
  

  const editando = partidoEditId !== null && partidoEditId !== undefined;

  if (!editando) {
    body.fechaId = Number(fechaId);
}

  const url = editando ? `${API_PARTIDOS}/${partidoEditId}` : API_PARTIDOS;
  const method = editando ? "PUT" : "POST";



  try {
    const res = await fetch(url, {
      method,
      headers: authHeaders(),
      body: JSON.stringify(body)
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    cerrarModalPartido();

    mostrarExito(
      editando ? "Partido actualizado" : "Partido creado",
      editando ? "El partido se actualizó correctamente" : "El partido se creó correctamente"
    );

    await cargarPartidos();

  } catch (e) {
    console.error(e);
    mostrarError("Error guardando partido", e.message);
  }
}

/******************************
 * PARTIDOS - RENDER
 ******************************/
function renderTabsFechasPartidos() {
  if (!tabsFechasPartidos) return;

  tabsFechasPartidos.innerHTML = "";

  if (!fechasCache || fechasCache.length === 0) {
    tabsFechasPartidos.innerHTML = `
      <span class="text-gray-500 text-sm">
        No hay fechas cargadas
      </span>
    `;
    return;
  }

  if (!fechaPartidosSeleccionadaId) {
    fechaPartidosSeleccionadaId = fechasCache[0].id;
  }

  fechasCache.forEach(fecha => {
    const active = Number(fecha.id) === Number(fechaPartidosSeleccionadaId);

    tabsFechasPartidos.innerHTML += `
      <button 
        class="tab ${active ? "active" : ""}" 
        onclick="seleccionarFechaPartidos(${fecha.id})"
      >
        ${fecha.nombre}
      </button>
    `;
  });
}

function seleccionarFechaPartidos(fechaId) {
  fechaPartidosSeleccionadaId = fechaId;

  renderTabsFechasPartidos();
  renderPartidos();
}

function renderPartidos() {
  if (!tablaPartidosBody) return;

  tablaPartidosBody.innerHTML = "";

  let partidos = partidosCache;

  if (fechaPartidosSeleccionadaId) {
    partidos = partidosCache.filter(p =>
      Number(p.fechaId) === Number(fechaPartidosSeleccionadaId)
    );
  }

  partidos.sort((a, b) => {
    return new Date(a.fechaHorarioInicio) - new Date(b.fechaHorarioInicio);
  });

  if (!partidos || partidos.length === 0) {
    tablaPartidosBody.innerHTML = `
      <tr class="tbl-row">
        <td colspan="6" class="text-center text-gray-500 py-4">
          No hay partidos cargados para esta fecha
        </td>
      </tr>
    `;
    return;
  }

  partidos.forEach(partido => {
    tablaPartidosBody.innerHTML += crearFilaPartido(partido);
  });
}

function crearFilaPartido(partido) {
  const estado = partido.estado ?? "POR_JUGARSE";
  const resultado = obtenerResultadoPartido(partido);
  const acciones = obtenerAccionesPartido(partido);

  return `
    <tr class="tbl-row">
      <td class="title-font font-bold">
        ${partido.equipoLocal ?? "-"}
      </td>

      <td class="title-font font-bold">
        ${partido.equipoVisitante ?? "-"}
      </td>

      <td class="text-gray-500 text-xs">
        ${formatearFechaHora(partido.fechaHorarioInicio)}
      </td>

      <td>
        ${obtenerBadgePartido(estado)}
      </td>

      <td class="title-font font-bold">
        ${resultado}
      </td>

      <td>
        <div class="flex gap-2">
          ${acciones}
        </div>
      </td>
    </tr>
  `;
}

function obtenerResultadoPartido(partido) {
  if (
    partido.golesLocal === null ||
    partido.golesLocal === undefined ||
    partido.golesVisitante === null ||
    partido.golesVisitante === undefined
  ) {
    return `<span class="text-gray-600">—</span>`;
  }

  return `${partido.golesLocal} — ${partido.golesVisitante}`;
}

function obtenerAccionesPartido(partido) {
  if (partido.estado === "FINALIZADO") {
    return `<span class="text-gray-700 text-xs">Cerrado</span>`;
  }

  return `
    <button 
      class="btn-edit" 
      onclick="abrirModalResultado(${partido.id})"
    >
      Resultado
    </button>

    <button 
      class="btn-ghost" 
      style="height:30px;font-size:11px;" 
      onclick="abrirModalEstado(${partido.id})"
      ${partido.estado !== "POR_JUGARSE" ? "disabled" : ""}
    >
      Estado
    </button>

    <button 
      class="btn-edit" 
      style="height:30px;font-size:11px;" 
      onclick="abrirModalEditarPartido(${partido.id})"
      ${partido.estado !== "POR_JUGARSE" ? "disabled" : ""}
    >
      Editar
    </button>
  `;
}

function obtenerBadgePartido(estado) {
  if (estado === "FINALIZADO") {
    return `<span class="badge badge-finalizada">Finalizado</span>`;
  }

  if (estado === "EN_JUEGO") {
    return `
      <span class="badge badge-en-juego">
        <span class="dot-live" style="width:5px;height:5px;"></span>
        En juego
      </span>
    `;
  }

  return `<span class="badge badge-por-jugarse">Por jugarse</span>`;
}

function formatearFechaHora(fechaIso) {
  if (!fechaIso) return "-";

  const fecha = new Date(fechaIso);

  if (Number.isNaN(fecha.getTime())) {
    return fechaIso;
  }

  return fecha.toLocaleString("es-AR", {
    day: "2-digit",
    month: "2-digit",
    year: "2-digit",
    hour: "2-digit",
    minute: "2-digit"
  });
}
/******************************
 * PARTIDOS - RESULTADO
 ******************************/
function abrirModalResultado(id) {
  const partido = partidosCache.find(p => Number(p.id) === Number(id));

  if (!partido) {
    mostrarError("Partido no encontrado");
    return;
  }

  if (resultadoPartidoId) resultadoPartidoId.value = partido.id;

  if (modalResultadoTitulo) {
    modalResultadoTitulo.textContent = `${partido.equipoLocal} vs ${partido.equipoVisitante}`;
  }

  if (labelLocal) labelLocal.textContent = partido.equipoLocal ?? "Local";
  if (labelVisitante) labelVisitante.textContent = partido.equipoVisitante ?? "Visitante";

  if (resultadoGolesLocal) resultadoGolesLocal.value = partido.golesLocal ?? 0;
  if (resultadoGolesVisitante) resultadoGolesVisitante.value = partido.golesVisitante ?? 0;

  abrirModal("modal-resultado");
}

function cerrarModalResultado() {
  cerrarModal("modal-resultado");

  if (resultadoPartidoId) resultadoPartidoId.value = "";
  if (resultadoGolesLocal) resultadoGolesLocal.value = 0;
  if (resultadoGolesVisitante) resultadoGolesVisitante.value = 0;
}

async function guardarResultado() {
  const id = resultadoPartidoId?.value;
  const golesLocal = Number(resultadoGolesLocal?.value);
  const golesVisitante = Number(resultadoGolesVisitante?.value);

  if (!id) {
    mostrarError("Partido inválido", "No se encontró el partido");
    return;
  }

  if (golesLocal < 0 || golesVisitante < 0) {
    mostrarError("Resultado inválido", "Los goles no pueden ser negativos");
    return;
  }

  const body = {
    golesLocal,
    golesVisitante
  };

  try {
    const res = await fetch(`${API_PARTIDOS}/${id}/resultado`, {
      method: "PATCH",
      headers: authHeaders(),
      body: JSON.stringify(body)
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }


    cerrarModalResultado();

    mostrarExito("Resultado cargado", "Se calcularon los puntos automáticamente");

    renderDashboardPartidos();
    await cargarFechas();
    await cargarPartidos();

  } catch (e) {
    console.error(e);
    mostrarError("Error cargando resultado", e.message);
  }
}
/******************************
 * PARTIDOS - ESTADO
 ******************************/
function abrirModalEstado(id) {
  const partido = partidosCache.find(p => Number(p.id) === Number(id));

  if (!partido) {
    mostrarError("Partido no encontrado");
    return;
  }

  if (estadoPartidoId) estadoPartidoId.value = partido.id;

  if (modalEstadoTitulo) {
    modalEstadoTitulo.textContent = `${partido.equipoLocal} vs ${partido.equipoVisitante}`;
  }

  abrirModal("modal-estado");
}

function cerrarModalEstado() {
  cerrarModal("modal-estado");

  if (estadoPartidoId) estadoPartidoId.value = "";
}

async function confirmarEstadoEnJuego() {
  const id = estadoPartidoId?.value;

  if (!id) {
    mostrarError("Partido inválido", "No se encontró el partido");
    return;
  }

  try {
    const res = await fetch(`${API_PARTIDOS}/${id}/en-juego`, {
      method: "PATCH",
      headers: authHeaders()
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    cerrarModalEstado();

    mostrarExito("Estado actualizado", "El partido pasó a EN JUEGO");

    await cargarFechas();
    await cargarPartidos();

  } catch (e) {
    console.error(e);
    mostrarError("Error cambiando estado", e.message);
  }
}
/******************************
 * USUARIOS - DATA
 ******************************/
async function cargarUsuarios() {
  try {
    const res = await fetch(API_USUARIOS, {
      method: "GET",
      headers: authHeaders()
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    const data = await res.json();

    usuariosCache = Array.isArray(data) ? data : data.data;

    if (!Array.isArray(usuariosCache)) {
      usuariosCache = [];
    }

    renderUsuarios(usuariosCache);

  } catch (e) {
    console.error(e);
    mostrarError("Error cargando usuarios", e.message);
  }
}

/******************************
 * USUARIOS - RENDER
 ******************************/
function renderUsuarios(usuarios) {
  if (!tablaUsuariosBody) return;

  tablaUsuariosBody.innerHTML = "";

  if (!usuarios || usuarios.length === 0) {
    tablaUsuariosBody.innerHTML = `
      <tr class="tbl-row">
        <td colspan="8" class="text-center text-gray-500 py-4">
          No hay usuarios cargados
        </td>
      </tr>
    `;
    return;
  }

  usuarios.forEach((usuario, index) => {
    tablaUsuariosBody.innerHTML += `
      <tr class="tbl-row">
        <td class="text-gray-600">${index + 1}</td>

        <td class="font-semibold">
          ${usuario.nombre ?? "-"}
        </td>

        <td class="text-gray-500 text-xs">
          ${usuario.email ?? "-"}
        </td>

        <td>
          ${obtenerBadgeRol(usuario.rol)}
        </td>

        <td class="font-bold ${Number(usuario.puntosTotales ?? 0) > 0 ? "text-[#05AC2E]" : ""}">
          ${usuario.puntosTotales ?? 0}
        </td>

        <td class="text-gray-400">
          ${usuario.cantidadPronosticos ?? "—"}
        </td>

        <td class="text-gray-400">
          ${usuario.cantidadResultadosExactos ?? 0}
        </td>

        <td>
          <button 
            class="btn-edit"
            style="height:30px;font-size:11px;"
            onclick="cambiarRolUsuario(${usuario.id})"
            >
           ${usuario.rol === "ADMIN" ? "Hacer USER" : "Hacer ADMIN"}
          </button>
        </td>
      </tr>
    `;
  });
}

function obtenerBadgeRol(rol) {
  if (rol === "ADMIN") {
    return `<span class="badge badge-admin">Admin</span>`;
  }

  return `<span class="badge badge-user">Usuario</span>`;
}

function filtrarUsuarios() {
  const texto = normalizarTexto(buscarUsuario?.value);

  if (!texto) {
    renderUsuarios(usuariosCache);
    return;
  }

  const filtrados = usuariosCache.filter(usuario =>
    normalizarTexto(usuario.nombre).includes(texto) ||
    normalizarTexto(usuario.email).includes(texto) ||
    normalizarTexto(usuario.rol).includes(texto)
  );

  renderUsuarios(filtrados);
}

async function cambiarRolUsuario(id) {
  const usuario = usuariosCache.find(u => Number(u.id) === Number(id));

  if (!usuario) {
    mostrarError("Usuario no encontrado");
    return;
  }

  const nuevoRol = usuario.rol === "ADMIN" ? "USER" : "ADMIN";

  const result = await Swal.fire({
    icon: "warning",
    title: "¿Cambiar rol?",
    text: `El usuario ${usuario.nombre} pasará a ser ${nuevoRol}.`,
    showCancelButton: true,
    confirmButtonText: "Sí, cambiar",
    cancelButtonText: "Cancelar"
  });

  if (!result.isConfirmed) return;

  try {
    const res = await fetch(`${API_USUARIOS}/${id}/cambiar-rol`, {
      method: "PATCH",
      headers: authHeaders()
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    mostrarExito("Rol actualizado", `El usuario ahora tiene rol ${nuevoRol}`);

    await cargarUsuarios();

  } catch (e) {
    console.error(e);
    mostrarError("Error cambiando rol", e.message);
  }
}

/******************************
 * PRONÓSTICOS - SELECT
 ******************************/
function cargarSelectFechasPronosticos() {
  if (!selectFechaPronosticos) return;

  selectFechaPronosticos.innerHTML = `<option value="">Todas las fechas</option>`;

  fechasCache.forEach(fecha => {
    selectFechaPronosticos.innerHTML += `
      <option value="${fecha.id}">
        ${fecha.nombre}
      </option>
    `;
  });
}

function cargarSelectPartidosPronosticos() {
  if (!selectPartidoPronosticos) return;

  selectPartidoPronosticos.innerHTML = `<option value="">Seleccioná un partido</option>`;

  let partidos = partidosCache;

  if (fechaPronosticosSeleccionadaId) {
    partidos = partidosCache.filter(p =>
      Number(p.fechaId) === Number(fechaPronosticosSeleccionadaId)
    );
  }

  partidos.forEach(partido => {
    selectPartidoPronosticos.innerHTML += `
      <option value="${partido.id}">
        ${partido.equipoLocal} vs ${partido.equipoVisitante}
      </option>
    `;
  });
}

function seleccionarFechaPronosticos() {
  fechaPronosticosSeleccionadaId = selectFechaPronosticos?.value || null;

  partidoPronosticosSeleccionadoId = null;
  pronosticosCache = [];

  if (selectPartidoPronosticos) {
    selectPartidoPronosticos.value = "";
  }

  cargarSelectPartidosPronosticos();
  renderPronosticos();
}

async function seleccionarPartidoPronosticos() {
  const partidoId = selectPartidoPronosticos?.value;

  if (!partidoId) {
    partidoPronosticosSeleccionadoId = null;
    pronosticosCache = [];
    renderPronosticos();
    return;
  }

  partidoPronosticosSeleccionadoId = partidoId;

  await cargarPronosticosPorPartido(partidoId);
}

/******************************
 * PRONÓSTICOS - DATA
 ******************************/
async function cargarPronosticosPorPartido(partidoId) {
  try {
    const res = await fetch(`${API_PRONOSTICOS}/partido/${partidoId}`, {
      method: "GET",
      headers: authHeaders()
    });

    if (!res.ok) {
      const msg = await leerErrorResponse(res);
      throw new Error(msg);
    }

    const data = await res.json();

    pronosticosCache = Array.isArray(data) ? data : data.data;
    console.log("Pronósticos:", pronosticosCache);
    if (!Array.isArray(pronosticosCache)) {
      pronosticosCache = [];
    }

    renderPronosticos();

  } catch (e) {
    console.error(e);
    mostrarError("Error cargando pronósticos", e.message);
  }
}

/******************************
 * PRONÓSTICOS - RENDER
 ******************************/
function renderPronosticos() {
  if (!tablaPronosticosBody) return;

  const partido = partidosCache.find(
    p => Number(p.id) === Number(partidoPronosticosSeleccionadoId)
  );

  if (tituloPronosticosPartido) {
    tituloPronosticosPartido.textContent = partido
      ? `${partido.equipoLocal} vs ${partido.equipoVisitante}`
      : "Seleccioná un partido";
  }

  if (cantidadPronosticos) {
    cantidadPronosticos.textContent = `${pronosticosCache.length} pronósticos`;
  }

  tablaPronosticosBody.innerHTML = "";

  if (!partidoPronosticosSeleccionadoId) {
    tablaPronosticosBody.innerHTML = `
      <tr class="tbl-row">
        <td colspan="5" class="text-center text-gray-500 py-4">
          Seleccioná un partido para ver los pronósticos
        </td>
      </tr>
    `;
    return;
  }

  if (!pronosticosCache || pronosticosCache.length === 0) {
    tablaPronosticosBody.innerHTML = `
      <tr class="tbl-row">
        <td colspan="5" class="text-center text-gray-500 py-4">
          No hay pronósticos cargados para este partido
        </td>
      </tr>
    `;
    return;
  }

  pronosticosCache.forEach(pronostico => {
    tablaPronosticosBody.innerHTML += crearFilaPronostico(pronostico, partido);
  });
}

function crearFilaPronostico(pronostico, partido) {
  const prediccion = `${pronostico.golesLocalPronosticados} — ${pronostico.golesVisitantePronosticados}`;

  const resultadoReal = obtenerResultadoPartido(partido);

  const puntos = pronostico.puntosObtenidos ?? 0;

  return `
    <tr class="tbl-row">
      <td class="font-semibold">
        ${pronostico.nombreUsuario ?? "-"}
      </td>

      <td class="title-font font-bold">
        ${prediccion}
      </td>

      <td class="title-font font-bold text-[#05AC2E]">
        ${resultadoReal}
      </td>

      <td class="${puntos > 0 ? "text-[#05AC2E]" : "text-gray-600"} font-bold">
        ${puntos > 0 ? "+" + puntos : puntos}
      </td>

      <td>
        ${obtenerTipoPronostico(pronostico, partido)}
      </td>
    </tr>
  `;
}

function obtenerTipoPronostico(pronostico, partido) {
  if (!partido || partido.estado !== "FINALIZADO") {
    return `<span class="badge badge-por-jugarse">Pendiente</span>`;
  }

  const puntos = pronostico.puntosObtenidos ?? 0;

  if (puntos === 3) {
    return `<span class="badge badge-en-juego">Exacto</span>`;
  }

  if (puntos === 1) {
    return `<span class="badge badge-por-jugarse">Ganador</span>`;
  }

  return `<span class="badge badge-finalizada">Errado</span>`;
}

/******************************
 * DASHBOARD
 ******************************/
function renderDashboard() {
  renderDashboardStats();
  renderDashboardPartidos();
}

function renderDashboardStats() {
  if (statEquipos) {
    statEquipos.textContent = equiposCache.length;
  }

  if (statFechas) {
    statFechas.textContent = fechasCache.length;
  }

  if (statFechasDetalle) {
    const fechasEnJuego = fechasCache.filter(f => f.estado === "EN_JUEGO").length;
    statFechasDetalle.textContent = `${fechasEnJuego} en juego`;
  }

  if (statPartidos) {
    statPartidos.textContent = partidosCache.length;
  }

  if (statPartidosDetalle) {
    const porJugarse = partidosCache.filter(p => p.estado === "POR_JUGARSE").length;
    statPartidosDetalle.textContent = `${porJugarse} por jugarse`;
  }

  if (statUsuarios) {
    statUsuarios.textContent = usuariosCache.length;
  }
}

function renderDashboardPartidos() {
  if (!tablaDashboardPartidos) return;

  tablaDashboardPartidos.innerHTML = "";

  const partidosDashboard = partidosCache
    .filter(p => p.estado === "EN_JUEGO")
    .sort((a, b) => {
      if (a.estado === "EN_JUEGO" && b.estado !== "EN_JUEGO") return -1;
      if (a.estado !== "EN_JUEGO" && b.estado === "EN_JUEGO") return 1;

      return new Date(a.fechaHorarioInicio) - new Date(b.fechaHorarioInicio);
    })
    .slice(0, 5);

  if (partidosDashboard.length === 0) {
    tablaDashboardPartidos.innerHTML = `
      <tr class="tbl-row">
        <td colspan="5" class="text-center text-gray-500 py-4">
          No hay partidos en juego.
        </td>
      </tr>
    `;
    return;
  }

  partidosDashboard.forEach(partido => {
    tablaDashboardPartidos.innerHTML += crearFilaDashboardPartido(partido);
  });
}

function crearFilaDashboardPartido(partido) {
  return `
    <tr class="tbl-row">
      <td class="title-font font-bold">
        ${partido.equipoLocal ?? "-"}
        <span class="text-gray-600 font-normal text-xs">vs</span>
        ${partido.equipoVisitante ?? "-"}
      </td>

      <td class="text-gray-500">
        ${partido.fechaNombre ?? "-"}
      </td>

      <td>
        ${obtenerBadgePartido(partido.estado)}
      </td>


      <td class="flex gap-2 items-center">
        ${obtenerAccionesDashboardPartido(partido)}
      </td>
    </tr>
  `;
}

function obtenerAccionesDashboardPartido(partido) {
  if (partido.estado === "FINALIZADO") {
    return `<span class="text-gray-700 text-xs">Cerrado</span>`;
  }

  return `
    <button class="btn-edit" onclick="abrirModalResultado(${partido.id})">
      Resultado
    </button>

    <button 
      class="btn-ghost" 
      style="height:30px;font-size:11px;" 
      onclick="abrirModalEstado(${partido.id})"
      ${partido.estado !== "POR_JUGARSE" ? "disabled" : ""}
    >
      Estado
    </button>
  `;
}

/******************************
 * INIT
 ******************************/
document.addEventListener("DOMContentLoaded", async () => {
  showSection("dashboard");
  await cargarEquipos();
  await cargarFechas();
  await cargarPartidos();
  await cargarUsuarios();
  renderDashboard();

  cargarSelectFechasPronosticos();
  cargarSelectPartidosPronosticos();
  renderPronosticos();
  selectFechaPronosticos?.addEventListener("change", seleccionarFechaPronosticos);
  selectPartidoPronosticos?.addEventListener("change", seleccionarPartidoPronosticos);

  //listeners equipos
  btnNuevoEquipo?.addEventListener("click", abrirModalNuevoEquipo);
  btnGuardarEquipo?.addEventListener("click", guardarEquipo);
  btnCancelarEquipo?.addEventListener("click", cerrarModalEquipo);
  btnCerrarModalEquipo?.addEventListener("click", cerrarModalEquipo);
  buscarEquipo?.addEventListener("input", filtrarEquipos);

  modalEquipoEl?.addEventListener("click", (e) => {
    if (e.target === modalEquipoEl) {
      cerrarModalEquipo();
    }
  });

  //listeners fechas
  btnNuevaFecha?.addEventListener("click", abrirModalNuevaFecha);
  btnGuardarFecha?.addEventListener("click", guardarFecha);
  btnCancelarFecha?.addEventListener("click", cerrarModalFecha);
  btnCerrarModalFecha?.addEventListener("click", cerrarModalFecha);

  modalFechaEl?.addEventListener("click", (e) => {
    if (e.target === modalFechaEl) {
      cerrarModalFecha();
    }
  });

  //listeners partidos
  btnNuevoPartido?.addEventListener("click", abrirModalNuevoPartido);
  btnGuardarPartido?.addEventListener("click", guardarPartido);
  btnCancelarPartido?.addEventListener("click", cerrarModalPartido);
  btnCerrarModalPartido?.addEventListener("click", cerrarModalPartido);

  modalPartidoEl?.addEventListener("click", (e) => {
    if (e.target === modalPartidoEl) cerrarModalPartido();
  });

  //listeners resultado
  btnGuardarResultado?.addEventListener("click", guardarResultado);
  btnCancelarResultado?.addEventListener("click", cerrarModalResultado);
  btnCerrarModalResultado?.addEventListener("click", cerrarModalResultado);

  modalResultadoEl?.addEventListener("click", (e) => {
    if (e.target === modalResultadoEl) cerrarModalResultado();
  });


  //listeners estado
  btnConfirmarEstado?.addEventListener("click", confirmarEstadoEnJuego);
  btnCancelarEstado?.addEventListener("click", cerrarModalEstado);
  btnCerrarModalEstado?.addEventListener("click", cerrarModalEstado);

  modalEstadoEl?.addEventListener("click", (e) => {
    if (e.target === modalEstadoEl) cerrarModalEstado();
  });

  //listeners usuarios
  buscarUsuario?.addEventListener("input", filtrarUsuarios);

  //listeners dashboard
  btnDashboardVerPartidos?.addEventListener("click", () => {
    showSection("partidos", document.querySelector('[href="#partidos"]'));
  });
});