package blav.ventiflow.domain.usuario;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "Usuarios")
@Entity(name = "Usuario")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idUsuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    private String nombre;
    private String rol;
    private String clave;
}
