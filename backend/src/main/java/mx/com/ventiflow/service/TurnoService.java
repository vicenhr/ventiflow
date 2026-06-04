package mx.com.ventiflow.service;

import mx.com.ventiflow.domain.pedido.Pedido;
import mx.com.ventiflow.domain.turno.Turno;
import mx.com.ventiflow.domain.turno.TurnoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class TurnoService {
    @Autowired
    private TurnoRepository turnoRepository;

    // synchronized para evitar que dos pedidos concurrentes obtengan el mismo turno
    public synchronized Turno asignarTurno(Pedido pedido) {
        LocalDate hoy = LocalDate.now();

        // Calcular el siguiente número
        Integer siguienteNumero = calcularSiguienteNumero(hoy);

        // Generar el código de pantalla (Ej: "A-001", "A-015")
        // %03d significa: rellena con ceros a la izquierda hasta tener 3 dígitos
        String codigoPantalla = String.format("A-%03d", siguienteNumero);

        Turno nuevoTurno = new Turno();
        nuevoTurno.setPedido(pedido);
        nuevoTurno.setNumero(siguienteNumero);
        nuevoTurno.setCodigo(codigoPantalla);
        nuevoTurno.setFecha(hoy);
        nuevoTurno.setEstado("PENDIENTE");

        return turnoRepository.save(nuevoTurno);
    }

    private Integer calcularSiguienteNumero(LocalDate fecha) {
        // Busqueda del último número
        Integer ultimoNumero = turnoRepository.encontrarUltimoNumeroPorFecha(fecha).orElse(0);
        return ultimoNumero + 1;
    }
}
