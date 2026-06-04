document.addEventListener("DOMContentLoaded", () => {
    const token = localStorage.getItem('ventiflow_token');
    
    const rutaActual = window.location.pathname;
    const rutaLogin = rutaActual.includes('/pages/') ? 'login.html' : 'pages/login.html';

    if (!token) {
        window.location.href = rutaLogin;
        return; 
    }

    const aliasUsuario = localStorage.getItem('ventiflow_alias') || 'Usuario';
    const rolUsuario = localStorage.getItem('ventiflow_rol') || 'STAFF';
    
    const spanNombre = document.getElementById('nombreUsuarioNav');
    if (spanNombre) {
        spanNombre.innerText = `${aliasUsuario} (${rolUsuario})`;
    }

    const btnCerrarSesion = document.querySelector('.logout-icon');
    if (btnCerrarSesion) {
        btnCerrarSesion.onclick = (e) => {
            e.preventDefault(); 
            localStorage.removeItem('ventiflow_token');
            localStorage.removeItem('ventiflow_rol');
            localStorage.removeItem('ventiflow_alias'); 
            
            window.location.href = rutaLogin; 
        };
    }
});