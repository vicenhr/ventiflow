package mx.com.ventiflow.controller;

import jakarta.validation.Valid;
import mx.com.ventiflow.domain.pedido.*;
import mx.com.ventiflow.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {
    @Autowired
    private PedidoService service;
    @Autowired
    private PedidoRepository pedidoRepository;

    @PostMapping
    public ResponseEntity<DatosRespuestaPedido> hacerPedido(@RequestBody @Valid DatosRegistroPedido pedido,
                                                            UriComponentsBuilder uriComponentsBuilder) {
        var respuesta = service.crearPedido(pedido);
        URI url = uriComponentsBuilder.path("/api/v1/pedidos/{id}").buildAndExpand(respuesta.idPedido()).toUri();
        return ResponseEntity.created(url).body(respuesta);
    }

    @PatchMapping("/{idPedido}/estado")
    public ResponseEntity<Void> actualizarEstado(@PathVariable Long idPedido, @RequestBody DatosActualizarEstado datos) {
        service.actualizarEstado(idPedido, datos.nuevoEstado());
        return ResponseEntity.ok().build(); // Cambiado a 200 OK según contrato
    }


//    @PreAuthorize("hasRole('Administrador')") // Rol base de datos
    @PostMapping("/{idPedido}/cancelar")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Long idPedido, @RequestBody(required = false) DatosCancelaPedido cancelacion) {
        service.cancelarPedido(idPedido);
        return ResponseEntity.ok().build();
    }

    // Endpoint para KDS (Barra/Cocina)
    @GetMapping("/activos")
    public ResponseEntity<List<DatosKdsPedido>> listarActivos(@RequestParam String area) {
        var pedidos = service.obtenerActivosDeHoy(area);
        return ResponseEntity.ok(pedidos);
    }

    // Endpoint para la Pantalla de Turnos
    @GetMapping("/pantalla-publica")
    public ResponseEntity<List<DatosCdsMensaje>> obtenerPantallaPublica() {
        return ResponseEntity.ok(service.obtenerPedidosPantallaPublica());
    }
}
