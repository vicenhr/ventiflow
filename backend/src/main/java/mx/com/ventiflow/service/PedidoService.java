package mx.com.ventiflow.service;

import mx.com.ventiflow.domain.cliente.Cliente;
import mx.com.ventiflow.domain.cliente.ClienteRepository;
import mx.com.ventiflow.domain.detalle.DatosDetalleKds;
import mx.com.ventiflow.domain.detalle.DatosDetallePedido;
import mx.com.ventiflow.domain.detalle.DetallePedido;
import mx.com.ventiflow.domain.pedido.*;
import mx.com.ventiflow.domain.detalle.DetallePedidoRepository;
import mx.com.ventiflow.domain.producto.Producto;
import mx.com.ventiflow.domain.producto.ProductoRepository;
import mx.com.ventiflow.domain.turno.Turno;
import mx.com.ventiflow.domain.turno.TurnoRepository;
import mx.com.ventiflow.domain.usuario.Usuario;
import mx.com.ventiflow.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {
    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private DetallePedidoRepository detallePedidoRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private TurnoRepository turnoRepository;
    @Autowired
    private TurnoService turnoService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Transactional
    public DatosRespuestaPedido crearPedido (DatosRegistroPedido datos) {
        // 1. Buscar Usuario y Cliente
        Usuario usuario = usuarioRepository.findById(datos.idUsuario())
                .orElseThrow(() -> new RuntimeException("El usuario cajero no existe"));
        Cliente cliente = datos.idCliente() != null ? clienteRepository.findById(datos.idCliente()).orElse(null) : null;

        // 2. Crear cabecera del Pedido
        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setCliente(cliente);
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(BigDecimal.ZERO); // 0 para Postgres
        pedido = pedidoRepository.save(pedido);

        BigDecimal totalCalculado = BigDecimal.ZERO;

        // Listas para separar el pedido según el área
        List<DatosDetalleKds> detallesBarra = new ArrayList<>();
        List<DatosDetalleKds> detallesCocina = new ArrayList<>();

        // 3. Procesar los detalles
        for (DatosDetallePedido detalleDTO : datos.detalles()) {
            Producto producto = productoRepository.findById(detalleDTO.idProducto())
                    .orElseThrow(() -> new RuntimeException("El producto no existe"));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedido);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.cantidad());

            // Calculo del subtotal de forma segura en el servidor
            BigDecimal subtotal = producto.getPrecio().multiply(new BigDecimal(detalleDTO.cantidad()));
            detalle.setSubtotal(subtotal);

            detallePedidoRepository.save(detalle);
            totalCalculado = totalCalculado.add(subtotal);

            String area = producto.getCategoria().getAreaPreparacion();
            DatosDetalleKds kdsDetalle = new DatosDetalleKds(producto.getIdProducto(), producto.getNombre(), detalleDTO.cantidad());

            if ("Barra".equalsIgnoreCase(area)) {
                detallesBarra.add(kdsDetalle);
            } else if ("Cocina".equalsIgnoreCase(area)) {
                detallesCocina.add(kdsDetalle);
            }
        }

        // 4. Actualizar total final
        pedido.setTotal(totalCalculado);
        pedidoRepository.save(pedido);

        // --- CÁLCULO DE LEALTAD ---
        if (pedido.getCliente() != null) {
            // Dividimos el total entre 10 y tomamos solo la parte entera (RoundingMode.DOWN)
            int puntosGanados = totalCalculado.divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue();
            // Sumamos los puntos al cliente
            cliente.setPuntos(cliente.getPuntos() + puntosGanados);
            clienteRepository.save(cliente);
        }
        // ------------------------------------

        // 5. Generar el Turno / Código de Pantalla
        Turno turno = turnoService.asignarTurno(pedido);

        // 6. EMITIR WEBSOCKETS POR ÁREA
        LocalDateTime tiempoServidor = pedido.getFechaCreacion() != null ?
                pedido.getFechaCreacion() : LocalDateTime.now();

        if (!detallesBarra.isEmpty()) {
            DatosKdsPedido pedidoBarra = new DatosKdsPedido(
                    pedido.getIdPedido(), turno.getCodigo(), pedido.getEstado(), tiempoServidor, detallesBarra
            );
            simpMessagingTemplate.convertAndSend("/topic/kds/barra", pedidoBarra);
        }

        if (!detallesCocina.isEmpty()) {
            DatosKdsPedido pedidoCocina = new DatosKdsPedido(
                    pedido.getIdPedido(), turno.getCodigo(), pedido.getEstado(), tiempoServidor, detallesCocina
            );
            simpMessagingTemplate.convertAndSend("/topic/kds/cocina", pedidoCocina);
        }

        // 6. Retornar JSON
        return new DatosRespuestaPedido(
                pedido.getIdPedido(),
                turno.getCodigo(), // Aquí obtienes el "A-101"
                pedido.getEstado(),
                "Pedido registrado exitosamente"
        );
    }

    @Transactional
    public void actualizarEstado(Long idPedido, String nuevoEstado) {
        // 1. Actualizar tabla Pedidos
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        // --- VALIDACIÓN DE NEGOCIO ---
        String estadoActual = pedido.getEstado();

        if ("CANCELADO".equals(estadoActual)) {
            throw new IllegalStateException("No se puede modificar un pedido que ya fue cancelado.");
        }
        if ("PENDIENTE".equals(estadoActual) && "LISTO".equals(nuevoEstado)) { // O 'ENTREGADO' según lo llamen
            throw new IllegalStateException("Transición inválida: El pedido debe pasar por PREPARANDO primero.");
        }
        // ------------------------------------

        pedido.setEstado(nuevoEstado);
        pedidoRepository.save(pedido);

        // 2. Actualizar tabla Turnos
        Turno turno = turnoRepository.findByPedido(pedido)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado para este pedido"));
        turno.setEstado(nuevoEstado);
        turnoRepository.save(turno);
        // 3. Emitir evento a la pantalla del cliente (CDS)
        DatosCdsMensaje eventoPublico = new DatosCdsMensaje(turno.getCodigo(), nuevoEstado);
        simpMessagingTemplate.convertAndSend("/topic/cds/publico", eventoPublico);
    }

    @Transactional
    public void cancelarPedido(Long idPedido) {
        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        if ("LISTO".equals(pedido.getEstado()) || "ENTREGADO".equals(pedido.getEstado())) {
            throw new IllegalStateException("No se puede cancelar un pedido que ya está listo o entregado.");
        }

        pedido.setEstado("CANCELADO");
        pedidoRepository.save(pedido);

        // Actualizar el turno
        turnoRepository.findByPedido(pedido).ifPresent(turno -> {
            turno.setEstado("CANCELADO");
            turnoRepository.save(turno);
        });

        // Revertir puntos de lealtad si era un cliente registrado
        if (pedido.getCliente() != null) {
            int puntosARestar = pedido.getTotal().divide(BigDecimal.TEN, 0, RoundingMode.DOWN).intValue();
            Cliente cliente = pedido.getCliente();

            // Evitar que los puntos queden en negativo
            int nuevosPuntos = Math.max(0, cliente.getPuntos() - puntosARestar);
            cliente.setPuntos(nuevosPuntos);
            clienteRepository.save(cliente);
        }

        // Emitir alerta a KDS para detener preparación
        // Reemplaza los envíos de String por el nuevo objeto JSON
        DatosCancelaPedido alerta = new DatosCancelaPedido(
                idPedido,
                "CANCELACION",
                "Se ha cancelado el pedido " + idPedido + ", detener preparación."
        );

        simpMessagingTemplate.convertAndSend("/topic/kds/barra/alertas", alerta);
        simpMessagingTemplate.convertAndSend("/topic/kds/cocina/alertas", alerta);

        // Actualizar CSD público
        turnoRepository.findByPedido(pedido).ifPresent(turno -> {
            simpMessagingTemplate.convertAndSend("/topic/cds/publico",
                    new DatosCdsMensaje(turno.getCodigo(), "CANCELADO"));
        });
    }

    // GET /api/v1/pedidos/activos?area=Barra
    @Transactional(readOnly = true)
    public List<DatosKdsPedido> obtenerActivosDeHoy(String area) {
        List<Pedido> pedidos = pedidoRepository.findPedidosActivosDeHoy(area);

        return pedidos.stream().map(pedido -> {
            // Filtrar los detalles para que la pantalla solo vean productos
            List<DatosDetalleKds> detallesFiltrados = pedido.getDetalles().stream()
                    .filter(d -> d.getProducto().getCategoria().getAreaPreparacion().equalsIgnoreCase(area))
                    .map(d -> new DatosDetalleKds(d.getProducto().getIdProducto(), d.getProducto().getNombre(), d.getCantidad()))
                    .toList();

            return new DatosKdsPedido(
                    pedido.getIdPedido(),
                    pedido.getTurno().getCodigo(),
                    pedido.getEstado(),
                    pedido.getFechaCreacion(),
                    detallesFiltrados
            );
        }).toList();
    }

    @Transactional(readOnly = true)
    public List<DatosCdsMensaje> obtenerPedidosPantallaPublica() {
        // 1. Calculo límites de tiempo por día
        LocalDateTime inicioDia = LocalDateTime.of(LocalDate.now(), LocalTime.MIN); // Hoy a las 00:00:00
        LocalDateTime finDia = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);    // Hoy a las 23:59:59

        return pedidoRepository.findPedidosParaPantallaPublica(inicioDia, finDia).stream()
                .map(pedido -> {
                    String codigo = (pedido.getTurno() != null) ? pedido.getTurno().getCodigo() : "SIN-TURNO";
                    return new DatosCdsMensaje(codigo, pedido.getEstado());
                })
                .toList();
    }
}