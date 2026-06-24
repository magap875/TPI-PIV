/******************************
 * CONFIG + LOGIN
 ******************************/
const API_EQUIPOS = `${API_URL}/api/equipos`;

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
 * ESTADO
 ******************************/
let equiposCache = [];
let equipoEditId = null;

/******************************
 * HELPERS
 ******************************/
function getToken() {
  return localStorage.getItem("accessToken");
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

  const id = equipoId?.value;
  const editando = Boolean(id);

  const url = editando ? `${API_EQUIPOS}/${id}` : API_EQUIPOS;
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
        <td class="text-gray-500">—</td>
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
 * INIT
 ******************************/
document.addEventListener("DOMContentLoaded", async () => {
  await cargarEquipos();

  btnNuevoEquipo?.addEventListener("click", abrirModalNuevoEquipo);
  btnGuardarEquipo?.addEventListener("click", guardarEquipo);
  btnCancelarEquipo?.addEventListener("click", cerrarModalEquipo);
  btnCerrarModalEquipo?.addEventListener("click", cerrarModalEquipo);

  modalEquipoEl?.addEventListener("click", (e) => {
    if (e.target === modalEquipoEl) {
      cerrarModalEquipo();
    }
  });

  buscarEquipo?.addEventListener("input", filtrarEquipos);
});