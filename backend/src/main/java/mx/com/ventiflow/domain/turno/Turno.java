package mx.com.ventiflow.domain.turno;

import mx.com.ventiflow.domain.pedido.Pedido;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "turnos")
@Entity(name = "Turno")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idTurno")
public class Turno {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_turno")
    private Long idTurno;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido")
    private Pedido  pedido;
    @Column(name = "codigo_pantalla")
    private String codigo;
    @Column(name = "estado_visualizacion")
    private String estado;
    private Integer numero;
    private LocalDate fecha;
}
