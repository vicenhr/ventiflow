package mx.com.ventiflow.domain.pedido;

import lombok.*;
import mx.com.ventiflow.domain.cliente.Cliente;
import mx.com.ventiflow.domain.detalle.DetallePedido;
import mx.com.ventiflow.domain.turno.Turno;
import mx.com.ventiflow.domain.usuario.Usuario;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "pedidos")
@Entity(name = "Pedido")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "idPedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pedido")
    private Long idPedido;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;
    @CreationTimestamp
    @Column(name = "fecha_hora_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    @Column(name = "estado_general")
    private String estado;
    @Column(name = "total_pagado")
    private BigDecimal total;

    // Relación inversa con DetallePedido
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetallePedido> detalles = new ArrayList<>();;

    @OneToOne(mappedBy = "pedido", cascade = CascadeType.ALL)
    private Turno turno;

    // Método utilitario para mantener la sincronización bidireccional
    public void agregarDetalle(DetallePedido detalle) {
        detalles.add(detalle);
        detalle.setPedido(this);
    }
}
