package blav.ventiflow.domain.categoria;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "categorias")
@Entity(name = "Categoria")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idCategoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long idCategoria;
    @Column(name = "nombre_categoria")
    private String nombreCategoria;
    @Column(name = "area_preparacion")
    private String areaPreparacion;
}
