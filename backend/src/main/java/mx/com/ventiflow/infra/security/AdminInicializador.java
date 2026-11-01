package mx.com.ventiflow.infra.security;

import mx.com.ventiflow.domain.usuario.Rol;
import mx.com.ventiflow.domain.usuario.Usuario;
import mx.com.ventiflow.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminInicializador implements CommandLineRunner {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String nombre;
    private final String clave;

    public AdminInicializador(UsuarioRepository usuarioRepository,
                              PasswordEncoder passwordEncoder,
                              @Value("${admin.nombre}") String nombre,
                              @Value("${admin.clave}") String clave) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.nombre = nombre;
        this.clave = clave;
    }

    @Override
    public void run(String... args){
        if(!usuarioRepository.existsByNombre(nombre)){
            Usuario usuario = new Usuario();
            usuario.setNombre(nombre);
            usuario.setRol(Rol.ADMIN);
            usuario.setClaveHash(passwordEncoder.encode(clave));
            usuarioRepository.save(usuario);
        }
    }
}