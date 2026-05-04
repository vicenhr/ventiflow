package blav.ventiflow.domain.producto;

import blav.ventiflow.domain.categoria.Categoria;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Table(name = "productos")
@Entity(name = "Producto")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idProducto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long idProducto;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
    @Column(name = "nombre_producto")
    private String nombre;
    @Column(name = "precio_unitario")
    private BigDecimal precio;
    @Column(name = "estado_disponibilidad")
    private boolean estado;
}
