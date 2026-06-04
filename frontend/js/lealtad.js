let modalRegistroUI;
let puntosActuales = 0;

const token = localStorage.getItem('ventiflow_token'); 

document.addEventListener("DOMContentLoaded", () => {
    modalRegistroUI = new bootstrap.Modal(document.getElementById('registroModal'));
});

document.getElementById('btnBuscar').addEventListener('click', async () => {
    const telefono = document.getElementById('telefono').value;

    if (telefono.length !== 10 || isNaN(telefono)) {
        alert("Por favor, ingresa un número de 10 dígitos válido.");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/v1/clientes/${telefono}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (response.ok) {
            const cliente = await response.json();
            mostrarTarjeta(cliente);
        } else if (response.status === 404) {
            document.getElementById('telefonoRegistro').value = telefono; 
            document.getElementById('nombreRegistro').value = '';         
            document.getElementById('tarjetaCliente').style.display = 'none'; 

            modalRegistroUI.show(); 
        } else {
            console.error("Error en el servidor:", response.status);
        }
    } catch (error) {
        console.error("Error de conexión:", error);
    }
});

document.getElementById('btnGuardarCliente').addEventListener('click', async () => {
    const telefono = document.getElementById('telefonoRegistro').value;
    const nombreAlias = document.getElementById('nombreRegistro').value;

    if (!nombreAlias.trim()) {
        alert("Por favor ingresa un nombre válido.");
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/v1/clientes', {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` 
            },
            body: JSON.stringify({ telefono: telefono, nombreAlias: nombreAlias })
        });

        if (response.ok) {
            const nuevoCliente = await response.json();
            modalRegistroUI.hide(); 
            mostrarTarjeta(nuevoCliente); 
        } else {
            alert("Hubo un error al registrar el cliente en el servidor.");
        }
    } catch (error) {
        console.error("Error al registrar:", error);
    }
});

function mostrarTarjeta(cliente) {
    document.getElementById('nombreCliente').innerText = cliente.nombreAlias || "Cliente VentiFlow";

    const telFormateado = cliente.telefono.replace(/(\d{2})(\d{4})(\d{4})/, '$1 $2 $3');
    document.getElementById('telCliente').innerText = `+52 ${telFormateado}`;

    puntosActuales = cliente.puntosAcumulados || 0;
    document.getElementById('puntosCliente').innerText = puntosActuales;

    const tarjeta = document.getElementById('tarjetaCliente');
    tarjeta.style.display = 'flex';
}

document.getElementById('btnAplicarDescuento').addEventListener('click', () => {
    const dineroAFavor = puntosActuales * 1;

    document.getElementById('modalPuntos').innerText = `${puntosActuales} pts`;
    document.getElementById('modalDinero').innerText = `$${dineroAFavor.toFixed(2)} MXN`;

    const modalDesc = new bootstrap.Modal(document.getElementById('modalDescuento'));
    modalDesc.show();
});