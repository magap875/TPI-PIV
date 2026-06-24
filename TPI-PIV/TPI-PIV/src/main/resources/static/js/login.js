const form = document.getElementById("loginForm");

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const email = document.getElementById("email").value;
    const contrasena = document.getElementById("contrasena").value;

    try {
        const response = await fetch(`${API_URL}/api/auth/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, contraseña: contrasena })
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
        localStorage.setItem("accessToken", data.data.accessToken);
        localStorage.setItem("refreshToken", data.data.refreshToken);
       
        Swal.fire({
            icon: "success",
            title: "¡Bienvenido a SevenBets!",
            text: "Iniciaste sesión correctamente.",
            confirmButtonText: "Continuar"
        }).then(() => {
            window.location.href = "../index.html";
        });

    } catch (error) {
        Swal.fire({
            icon: "error",
            title: "Ups...",
            text: "Ocurrió un error inesperado."
        });
    }
});
