const token = localStorage.getItem('ventiflow_token');
let chartInstancia = null;

document.addEventListener("DOMContentLoaded", () => {
    cargarDashboard('hoy');

    document.getElementById('filtroFecha').addEventListener('change', (e) => {
        cargarDashboard(e.target.value);
    });
});

async function cargarDashboard(rango) {
    const hoy = new Date();
    let fechaInicio = new Date();

    if (rango === 'semana') fechaInicio.setDate(hoy.getDate() - 7);
    if (rango === 'mes') fechaInicio.setDate(hoy.getDate() - 30);

    const formatearFechaLocal = (fecha) => {
        const año = fecha.getFullYear();
        const mes = String(fecha.getMonth() + 1).padStart(2, '0');
        const dia = String(fecha.getDate()).padStart(2, '0');
        return `${año}-${mes}-${dia}`;
    };

    const strInicio = formatearFechaLocal(fechaInicio);
    const strFin = formatearFechaLocal(hoy);

    try {
        const res = await fetch(`http://localhost:8080/api/v1/metricas/resumen?fechaInicio=${strInicio}&fechaFin=${strFin}`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (res.ok) {
            const data = await res.json();
            actualizarUI(data);
        }
    } catch (error) {
        console.error("Error al cargar métricas:", error);
    }
}

function actualizarUI(data) {
    document.getElementById('kpiVentas').innerText = `$${data.ventasTotales.toLocaleString('en-US', {minimumFractionDigits: 2})}`;
    document.getElementById('kpiTickets').innerText = data.ticketsGenerados;

    const minutos = Math.floor(data.tiempoPromedioSegundos / 60);
    const segundos = Math.round(data.tiempoPromedioSegundos % 60);
    document.getElementById('kpiTiempo').innerText = `${minutos}m ${segundos}s`;

    document.getElementById('kpiProducto').innerText = data.productoEstrella || "N/A";

    const tbody = document.getElementById('listaUltimosPedidos');
    tbody.innerHTML = '';
    data.ultimosPedidos.forEach(pedido => {
        const horaFormateada = new Date(pedido.fechaHora).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
        tbody.innerHTML += `
            <tr>
                <td class="text-dark">#${pedido.turno}</td>
                <td class="text-secondary text-center">${horaFormateada}</td>
                <td class="text-success text-end"><b>$${pedido.total.toFixed(2)}</b></td>
            </tr>
        `;
    });

    dibujarGrafico(data.graficoHoras);
}

function dibujarGrafico(datosGrafico) {
    const ctx = document.getElementById('graficoVentas').getContext('2d');

    if (chartInstancia) chartInstancia.destroy();

    chartInstancia = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: datosGrafico.map(item => item.hora),
            datasets: [{
                label: 'Ventas por Hora',
                data: datosGrafico.map(item => item.total),
                backgroundColor: '#2ccb74',
                borderRadius: 4
            }]
        },
        options: {
            responsive: true,
            scales: {
                y: { beginAtZero: true, display: false },
                x: { grid: { display: false } }
            },
            plugins: { legend: { display: false } }
        }
    });
}