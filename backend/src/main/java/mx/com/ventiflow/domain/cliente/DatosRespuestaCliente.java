package mx.com.ventiflow.domain.cliente;

public record DatosRespuestaCliente(
        Long idCliente,
        String telefono,
        String nombreAlias,
        Integer puntosAcumulados
) {
    // Un constructor secundario para mapear fácilmente desde la entidad Cliente
    public DatosRespuestaCliente(Cliente cliente) {
        this(cliente.getIdCliente(), cliente.getTelefono(), cliente.getNombre(), cliente.getPuntos());
    }
}
