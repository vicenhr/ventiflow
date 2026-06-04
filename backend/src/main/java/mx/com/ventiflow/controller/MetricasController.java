package mx.com.ventiflow.controller;

import mx.com.ventiflow.domain.pedido.MetricasResumen;
import mx.com.ventiflow.service.MetricasService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/metricas")
public class MetricasController {

    private final MetricasService metricasService;

    public MetricasController(MetricasService metricasService) {
        this.metricasService = metricasService;
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/resumen")
    public ResponseEntity<MetricasResumen> obtenerResumen(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        MetricasResumen resumen = metricasService.calcularResumen(fechaInicio, fechaFin);
        return ResponseEntity.ok(resumen);
    }
}
