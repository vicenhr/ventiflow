package blav.ventiflow.domain.detalle;

import blav.ventiflow.domain.pedido.Pedido;
import blav.ventiflow.domain.producto.Producto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Table(name = "detalle_pedidos")
@Entity(name = "DetallePedido")
@Setter
@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "idDetalle")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle")
    private Long idDetalle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto")
    private Producto producto;

    private Integer cantidad;
    private BigDecimal subtotal;
}
