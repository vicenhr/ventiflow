package blav.ventiflow.domain.cliente;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Table(name = "clientes")
@Entity(name = "Cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "idCliente")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Long idCliente;
    private String telefono;
    @Column(name = "nombre_alias")
    private String nombre;
    @Column(name = "puntos_acumulados")
    private Integer puntos;
    @Column(name = "fecha_registro")
    private LocalDateTime fecha;
}
