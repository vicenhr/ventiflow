package blav.ventiflow.domain.pedido;

import blav.ventiflow.domain.cliente.Cliente;
import blav.ventiflow.domain.usuario.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "pedidos")
@Entity(name = "Pedido")
@Setter
@Getter
@AllArgsConstructor
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
    @Column(name = "fecha_hora_creacion")
    private Date fechaCreacion;
    @Column(name = "estado_general")
    private String estado;
    @Column(name = "total_pagado")
    private BigDecimal total;
}
