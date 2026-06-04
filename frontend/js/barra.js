let stompClient = null;
const token = localStorage.getItem('ventiflow_token');

document.addEventListener("DOMContentLoaded", () => {
    cargarPedidosPendientesHoy();
    conectarWebSocket();
});

async function cargarPedidosPendientesHoy() {
    try {
        const res = await fetch('http://localhost:8080/api/v1/pedidos/activos?area=Barra', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
            const pedidos = await res.json();
            pedidos.forEach(p => inyectarTicketEnUI(p, p.estadoGeneral));
        }
    } catch (error) {
        console.error("Error cargando el estado inicial", error);
    }
}

function conectarWebSocket() {
    const socket = new SockJS('http://localhost:8080/ws-ventiflow');
    stompClient = Stomp.over(socket);
    stompClient.debug = null;

    stompClient.connect({}, function (frame) {
        document.getElementById('conexionStatus').innerText = "🟢 Conectado";
        document.getElementById('conexionStatus').className = "status-conectado";

        stompClient.subscribe('/topic/kds/barra', function (mensaje) {
            const nuevoPedido = JSON.parse(mensaje.body);
            inyectarTicketEnUI(nuevoPedido, nuevoPedido.estadoGeneral || 'PENDIENTE'); 
        });

    }, function(error) {
        document.getElementById('conexionStatus').innerText = "🔴 Desconectado (Reintentando...)";
        document.getElementById('conexionStatus').className = "status-desconectado";
        setTimeout(conectarWebSocket, 5000);
    });
}

function inyectarTicketEnUI(pedido, estado) {
    let listaHTML = '';
    
    if (pedido.detalles) {
        pedido.detalles.forEach(prod => {
            listaHTML += `<li class="mb-1 fw-medium">• ${prod.cantidad}x ${prod.nombreProducto}</li>`;
        });
    }

    let botonesHTML = '';
    let targetCol = '';
    
    let colorTextoClase = 'text-dark'; 

    if (estado === 'PENDIENTE') {
        targetCol = 'colPendientes';
        botonesHTML = `<button class="btn btn-accion btn-preparar w-100 py-2" onclick="avanzarEstado(${pedido.idPedido}, 'PREPARANDO')">PREPARAR</button>`;
    } else if (estado === 'PREPARANDO') {
        targetCol = 'colPreparando'; 
        botonesHTML = `<button class="btn btn-accion btn-listo w-100 py-2" onclick="avanzarEstado(${pedido.idPedido}, 'LISTO')">MARCAR LISTO</button>`;
    } else if (estado === 'LISTO') {
        targetCol = 'colEntregar';
        botonesHTML = `<span class="text-success small float-end">Terminado</span>`;
        colorTextoClase = 'text-white'; 
    }

    const ticketHTML = `
        <div class="ticket-card p-3 shadow-sm" id="ticket-${pedido.idPedido}">
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h4 class="fw-bold mb-0 ${colorTextoClase}">#${pedido.turnoAsignado}</h4>
                <span class="text-danger small fw-bold" id="timer-${pedido.idPedido}">00:00</span>
            </div>
            <ul class="list-unstyled mb-3 px-2 ${colorTextoClase}">
                ${listaHTML}
            </ul>
            ${botonesHTML}
        </div>
    `;

    document.getElementById(targetCol).insertAdjacentHTML('beforeend', ticketHTML);

    if (pedido.fechaHoraCreacion) {
        tiemposTickets[pedido.idPedido] = new Date(pedido.fechaHoraCreacion);
    }
}

async function avanzarEstado(idPedido, nuevoEstado) {
    try {
        const res = await fetch(`http://localhost:8080/api/v1/pedidos/${idPedido}/estado`, {
            method: 'PATCH',
            headers: { 
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}` 
            },
            body: JSON.stringify({ nuevoEstado: nuevoEstado })
        });

        if (res.ok) {
            window.location.reload();
        } else {
            if (res.status === 403) {
                alert("⛔ Acceso Denegado: Tu rol actual no tiene permiso.");
            } else {
                alert(`Error del servidor (Código ${res.status}).`);
            }
        }
    } catch (e) {
        alert("Fallo de conexión con el servidor.");
    }
}

const tiemposTickets = {}; 

setInterval(() => {
    const ahora = new Date();
    
    for (const [id, horaCreacion] of Object.entries(tiemposTickets)) {
        const spanTimer = document.getElementById(`timer-${id}`);
        
        if (spanTimer) {
            const diffMs = ahora - horaCreacion;
            const diffSegundos = Math.floor(diffMs / 1000);
            
            const minutos = Math.floor(diffSegundos / 60);
            const segundos = diffSegundos % 60;
            
            const minStr = minutos.toString().padStart(2, '0');
            const segStr = segundos.toString().padStart(2, '0');
            
            spanTimer.innerText = `${minStr}:${segStr}`;
            
            if (minutos >= 10) {
                spanTimer.classList.add('text-danger', 'fs-5');
            }
        }
    }
}, 1000);