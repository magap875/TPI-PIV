const form = document.getElementById("registerForm");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const nombre = document.getElementById("nombre").value;
    const email = document.getElementById("email").value;
    const contrasena = document.getElementById("contrasena").value;

    try {
        const response = await fetch(`${API_URL}/api/auth/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ nombre, email, contraseña: contrasena })
        });

        const data = await response.json();

        if (!response.ok) {
            Swal.fire({
                icon: "error",
                title: "Error",
                text: data.message,
                confirmButtonText: "Entendido"
            });
            return;
        }

        Swal.fire({
            icon: "success",
            title: "Éxito",
            text:  "Tu cuenta fue creada correctamente.",
            confirmButtonText: "Iniciar sesión"
        }).then(() => {
            window.location.href = "/html/login.html";
        });

    } catch (error) {
        Swal.fire({
            icon:  "error",
            title: "Ups...",
            text:  "Ocurrió un error inesperado."
        });
    }
});