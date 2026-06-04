package mx.com.ventiflow.service;

import mx.com.ventiflow.domain.detalle.DetallePedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RecomendacionService {

    private final DetallePedidoRepository detallePedidoRepository;

    public RecomendacionService(DetallePedidoRepository detallePedidoRepository) {
        this.detallePedidoRepository = detallePedidoRepository;
    }

    public Map<String, String> generarRecomendacionDinamica() {
        // 1. Consultar la carga actual en la base de datos
        List<Object[]> cargaPorArea = detallePedidoRepository.contarCargaActivaPorArea();

        int itemsEnBarra = 0;
        int itemsEnCocina = 0;

        // 2. Extraer los datos de la consulta
        for (Object[] resultado : cargaPorArea) {
            String area = (String) resultado[0];
            Long cantidad = (Long) resultado[1];

            if ("Barra".equalsIgnoreCase(area)) {
                itemsEnBarra = cantidad.intValue();
            } else if ("Cocina".equalsIgnoreCase(area)) {
                itemsEnCocina = cantidad.intValue();
            }
        }

        // 3. Algoritmo de decisión basado en el tráfico
        String tipo;
        String mensaje;
        int diferenciaTrafico = Math.abs(itemsEnBarra - itemsEnCocina);
        int UMBRAL_SATURACION = 3; // Si un área tiene 3 items más que la otra, está colapsando

        if (diferenciaTrafico >= UMBRAL_SATURACION) {
            if (itemsEnBarra > itemsEnCocina) {
                tipo = "cross_selling_cocina";
                mensaje = "🔥 ALTA CARGA EN BARRA (" + itemsEnBarra + " items). Ofrece productos de Cocina (Ej. Panadería o Alimentos Salados) para agilizar el servicio.";
            } else {
                tipo = "cross_selling_barra";
                mensaje = "🔥 ALTA CARGA EN COCINA (" + itemsEnCocina + " items). Sugiere a los clientes bebidas frías o de preparación rápida en Barra.";
            }
        } else {
            tipo = "upselling_general";
            mensaje = "✅ Flujo estable (Barra: " + itemsEnBarra + " | Cocina: " + itemsEnCocina + "). Sugiere combos completos o el especial del día para maximizar la venta.";
        }

        return Map.of(
                "tipoRecomendacion", tipo,
                "mensajeSugerido", mensaje // <-- Cambiado a camelCase
        );
    }
}