const token = localStorage.getItem('ventiflow_token');
const headers = {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
};

let catalogoGlobal = [];
let carrito = [];

let idClienteVinculado = null; 

document.getElementById('btnVincularCliente').addEventListener('click', async () => {
    const telefono = document.getElementById('telefonoPos').value;
    const msgError = document.getElementById('msgClientePos');
    const badgeNombre = document.getElementById('nombreClientePos');

    if (telefono.length !== 10 || isNaN(telefono)) {
        msgError.innerText = "Ingresa 10 dígitos válidos";
        msgError.classList.remove('d-none');
        return;
    }

    try {
        const res = await fetch(`http://localhost:8080/api/v1/clientes/${telefono}`, { headers });
        
        if (res.ok) {
            const cliente = await res.json();
            idClienteVinculado = cliente.idCliente; 
            
            badgeNombre.innerText = cliente.nombreAlias;
            badgeNombre.className = "badge bg-success";
            msgError.classList.add('d-none');
        } else if (res.status === 404) {
            msgError.innerText = "No registrado. Regístralo en la pestaña 'Clientes'.";
            msgError.classList.remove('d-none');
            
            idClienteVinculado = null;
            badgeNombre.innerText = "Público General";
            badgeNombre.className = "badge bg-secondary";
        }
    } catch (error) {
        console.error("Error al buscar cliente:", error);
    }
});

document.addEventListener("DOMContentLoaded", () => {
    cargarRecomendacion();
    cargarCatalogo();
});

async function cargarRecomendacion() {
    try {
        const res = await fetch('http://localhost:8080/api/v1/recomendaciones/dinamica', { headers });
        if (res.ok) {
            const data = await res.json();
            document.getElementById('textoRecomendacion').innerText = data.mensajeSugerido;
            document.getElementById('bannerRecomendacion').classList.remove('d-none');
        }
    } catch (error) {
        console.warn("No se pudo cargar la recomendación dinámica.", error);
    }
}

async function cargarCatalogo() {
    try {
        const res = await fetch('http://localhost:8080/api/v1/productos', { headers });
        if (res.ok) {
            catalogoGlobal = await res.json();
            renderizarProductos(catalogoGlobal);
        }
    } catch (error) {
        console.error("Error cargando productos:", error);
        document.getElementById('gridProductos').innerHTML = `<p class="text-danger">Error al conectar con la base de datos.</p>`;
    }
}

function renderizarProductos(productos) {
    const grid = document.getElementById('gridProductos');
    grid.innerHTML = '';

    productos.forEach(prod => {
        const icono = prod.areaPreparacion === 'Barra' ? '☕' : '🥪';

        const card = document.createElement('div');
        card.className = 'col';
        card.innerHTML = `
            <div class="card product-card h-100 shadow-sm text-center p-3" onclick="agregarAlCarrito(${prod.idProducto})">
                <div class="product-icon mb-3">${icono}</div>
                <h6 class="fw-bold mb-1 text-dark">${prod.nombreProducto}</h6>
                <p class="text-success fw-bold mb-0">$${prod.precio.toFixed(2)}</p>
            </div>
        `;
        grid.appendChild(card);
    });
}

function agregarAlCarrito(idProducto) {
    const producto = catalogoGlobal.find(p => p.idProducto === idProducto);
    if (!producto) return;

    const itemExistente = carrito.find(item => item.idProducto === idProducto);
    if (itemExistente) {
        itemExistente.cantidad++;
        itemExistente.subtotal = itemExistente.cantidad * producto.precio;
    } else {
        carrito.push({
            idProducto: producto.idProducto,
            nombre: producto.nombreProducto,
            area: producto.areaPreparacion,
            precio: producto.precio,
            cantidad: 1,
            subtotal: producto.precio
        });
    }
    actualizarCarritoUI();
}

function actualizarCarritoUI() {
    const lista = document.getElementById('listaCarrito');
    lista.innerHTML = '';
    let sumaTotal = 0;

    carrito.forEach((item, index) => {
        sumaTotal += item.subtotal;
        lista.innerHTML += `
            <div class="d-flex justify-content-between mb-3 align-items-center">
                <div>
                    <span class="fw-bold text-dark">${item.cantidad}x ${item.nombre}</span><br>
                    <small class="text-muted">Para ${item.area}</small>
                </div>
                <div class="fw-bold text-dark">$${item.subtotal.toFixed(2)}</div>
            </div>
        `;
    });

    const subtotal = sumaTotal / 1.16;
    const iva = sumaTotal - subtotal;

    document.getElementById('resumenSubtotal').innerText = `$${subtotal.toFixed(2)}`;
    document.getElementById('resumenIva').innerText = `$${iva.toFixed(2)}`;
    document.getElementById('resumenTotal').innerText = `$${sumaTotal.toFixed(2)}`;
}

document.getElementById('btnLimpiar').addEventListener('click', () => {
    carrito = [];
    actualizarCarritoUI();
});

document.getElementById('btnCobrar').addEventListener('click', async () => {
    if (carrito.length === 0) {
        alert("El carrito está vacío. Agrega productos antes de cobrar.");
        return;
    }

    const totalPagado = carrito.reduce((sum, item) => sum + item.subtotal, 0);

    const detallesPedido = carrito.map(item => ({
        idProducto: item.idProducto,
        cantidad: item.cantidad,
        subtotal: item.subtotal
    }));

    const payload = {
        idUsuario: 2,
        idCliente: idClienteVinculado,
        totalPagado: parseFloat(totalPagado.toFixed(2)),
        detalles: detallesPedido
    };

    try {
        const res = await fetch('http://localhost:8080/api/v1/pedidos', {
            method: 'POST',
            headers: headers,
            body: JSON.stringify(payload)
        });

        if (res.status === 201) {
            const data = await res.json();

            document.getElementById('turnoAsignadoModal').innerText = data.turnoAsignado;

            const modalExito = new bootstrap.Modal(document.getElementById('modalExitoPedido'));
            modalExito.show();

            carrito = [];
            actualizarCarritoUI();

        } else {
            alert("Hubo un error al procesar el cobro en el servidor.");
        }
    } catch (error) {
        console.error("Error al enviar el pedido:", error);
        // Lógica para Timeout o pérdida de conexión [cite: 747]
        alert("Fallo de conexión. El pedido no pudo ser enviado.");
    }
});

document.getElementById('btnSiguienteOrden').addEventListener('click', () => {
    window.location.reload();
});