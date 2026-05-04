package blav.ventiflow.controller;

// AuthResponse.java
public class AuthResponse {
    private String token;
    private UsuarioDTO usuario;

    public AuthResponse(String token, UsuarioDTO usuario) {
        this.token = token;
        this.usuario = usuario;
    }

    // Getters y Setters
    public String getToken() { return token; }
    public UsuarioDTO getUsuario() { return usuario; }
}

// UsuarioDTO.java
class UsuarioDTO {
    private Long id_usuario;
    private String nombre;
    private String rol;

    public UsuarioDTO(Long id, String nombre, String rol) {
        this.id_usuario = id;
        this.nombre = nombre;
        this.rol = rol;
    }

    // Getters
    public Long getId_usuario() { return id_usuario; }
    public String getNombre() { return nombre; }
    public String getRol() { return rol; }
}
