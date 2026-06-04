let stompClient = null;
const campanaAudio = new Audio('../pages/ding.mp3');

document.addEventListener("DOMContentLoaded", () => {
    const btnIniciar = document.getElementById('btn-iniciar');
    const overlay = document.getElementById('overlay-interaccion');

    btnIniciar.addEventListener('click', () => {
        campanaAudio.play().then(() => {
            campanaAudio.pause();
            campanaAudio.currentTime = 0;
        }).catch(err => console.warn("Aviso de audio al iniciar:", err));
        overlay.style.display = 'none';

        cargarPantallaPublicaHoy();
        conectarWebSocketCDS();
    });
});

async function cargarPantallaPublicaHoy() {
    try {
        const res = await fetch('http://localhost:8080/api/v1/pedidos/pantalla-publica');
        if (res.ok) {
            const pedidos = await res.json();
            pedidos.forEach(p => moverTurnoAColumna(p.turnoAsignado, p.estadoGeneral));
        }
    } catch (e) {
        console.warn("No se pudo cargar el historial del CDS", e);
    }
}

function conectarWebSocketCDS() {
    const socket = new SockJS('http://localhost:8080/ws-ventiflow');
    stompClient = Stomp.over(socket);
    stompClient.debug = null; 

    stompClient.connect({}, function (frame) {
        stompClient.subscribe('/topic/cds/publico', function (mensaje) {
            const evento = JSON.parse(mensaje.body);
            
            moverTurnoAColumna(evento.turnoAsignado, evento.estadoGeneral);

            if(evento.estadoGeneral === 'LISTO') {
                reproducirCampana();
            }
        });
    });
}

function moverTurnoAColumna(turno, estado) {
    const elementoExistente = document.getElementById(`turno-cds-${turno}`);
    if (elementoExistente) {
        elementoExistente.remove();
    }

    const div = document.createElement('div');
    div.id = `turno-cds-${turno}`;


    if (estado === 'PENDIENTE') {
        div.className = 'tarjeta-turno tarjeta-pendiente';
        div.innerText = turno;
        document.getElementById('contenedorPendientes').appendChild(div);

    } else if (estado === 'PREPARANDO') {
        div.className = 'tarjeta-turno tarjeta-preparando';
        div.innerText = turno;
        document.getElementById('contenedorPreparando').appendChild(div);

    } else if (estado === 'LISTO') {
        div.className = 'tarjeta-turno tarjeta-listo';
        div.innerHTML = `
            <span>${turno}</span>
            <span class="badge-listo">¡Pase a recoger!</span>
        `;
        
        document.getElementById('contenedorListos').insertAdjacentElement('afterbegin', div);
    }
}

function reproducirCampana() {
    try {
        campanaAudio.currentTime = 0;
        let playPromise = campanaAudio.play();
        
        if (playPromise !== undefined) {
            playPromise.catch(e => {
                console.log("No se pudo reproducir el sonido (bloqueado por navegador):", e);
            });
        }
    } catch(e) {
        console.log("Error general al intentar reproducir audio:", e);
    }
}