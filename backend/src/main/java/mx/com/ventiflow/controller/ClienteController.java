package mx.com.ventiflow.controller;

import jakarta.validation.Valid;
import mx.com.ventiflow.domain.cliente.DatosRegistroCliente;
import mx.com.ventiflow.domain.cliente.DatosRespuestaCliente;
import mx.com.ventiflow.service.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @GetMapping("/{telefono}")
    public ResponseEntity<DatosRespuestaCliente> obtenerClientePorTelefono(@PathVariable String telefono) {
        Optional<DatosRespuestaCliente> respuesta = service.buscarPorTelefono(telefono);

        if (respuesta.isPresent()) {
            return ResponseEntity.ok(respuesta.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<DatosRespuestaCliente> registrarCliente(@RequestBody @Valid DatosRegistroCliente datosRegistro,
            UriComponentsBuilder uriComponentsBuilder) {
        var respuesta = service.registrar(datosRegistro);
        URI url = uriComponentsBuilder.path("/api/v1/clientes/{telefono}").buildAndExpand(respuesta.telefono()).toUri();
        System.out.println(respuesta);
        return ResponseEntity.created(url).body(respuesta);
    }
}