package mx.com.ventiflow.domain.turno;

import mx.com.ventiflow.domain.pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface TurnoRepository extends JpaRepository<Turno, Long> {
    // Busca el número de turno más alto registrado en una fecha específica
    @Query("SELECT MAX(t.numero) FROM Turno t WHERE t.fecha = :fechaActual")
    Optional<Integer> encontrarUltimoNumeroPorFecha(@Param("fechaActual") LocalDate fechaActual);

    Optional<Turno> findByPedido(Pedido pedido);
}
