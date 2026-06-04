package mx.com.ventiflow.domain.detalle;

import lombok.*;
import mx.com.ventiflow.domain.pedido.Pedido;
import mx.com.ventiflow.domain.producto.Producto;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Table(name = "detalle_pedidos")
@Entity(name = "DetallePedido")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
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
