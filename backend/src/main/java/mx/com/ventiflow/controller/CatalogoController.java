package mx.com.ventiflow.controller;

import mx.com.ventiflow.domain.producto.DatosProducto;
import mx.com.ventiflow.service.CatalogoService;
import mx.com.ventiflow.service.RecomendacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CatalogoController {

    private final CatalogoService catalogoService;
    private final RecomendacionService recomendacionService;

    public CatalogoController(CatalogoService catalogoService, RecomendacionService recomendacionService) {
        this.catalogoService = catalogoService;
        this.recomendacionService = recomendacionService;
    }

    @GetMapping(value = "/productos", produces = "application/json; charset=UTF-8")
    public ResponseEntity<List<DatosProducto>> obtenerCatalogo() {
        List<DatosProducto> catalogo = catalogoService.obtenerProductosDisponibles();
        return ResponseEntity.ok(catalogo);
    }

    @GetMapping("/recomendaciones/dinamica")
    public ResponseEntity<Map<String, String>> obtenerRecomendacionDinamica() {
        Map<String, String> recomendacion = recomendacionService.generarRecomendacionDinamica();
        return ResponseEntity.ok(recomendacion);
    }
}