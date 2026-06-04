document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const usuario = document.getElementById('usuario').value;
    const clave = document.getElementById('clave').value;
    const errorMsg = document.getElementById('errorMsg');

    try {
        const response = await fetch('http://localhost:8080/api/v1/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ usuario: usuario, clave: clave })
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('ventiflow_token', data.token);
            localStorage.setItem('ventiflow_rol', data.rol);
            localStorage.setItem('ventiflow_alias', data.alias);

            if(data.rol === 'Administrador') {
                window.location.href = '../index.html';
            } else {
                window.location.href = '../index.html';
            }
        } else {
            errorMsg.style.display = 'block';
        }
    } catch (error) {
        console.error("Error al conectar con el servidor:", error);
        alert("No se pudo conectar con el servidor backend.");
    }
});