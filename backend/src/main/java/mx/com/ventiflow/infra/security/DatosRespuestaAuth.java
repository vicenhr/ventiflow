package mx.com.ventiflow.infra.security;

public record DatosRespuestaAuth(
        String token,
        String rol,
        String alias
) {
}
