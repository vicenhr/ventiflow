package mx.com.ventiflow.controller;

import mx.com.ventiflow.domain.usuario.DatosUsuario;
import mx.com.ventiflow.domain.usuario.Usuario;
import mx.com.ventiflow.domain.usuario.UsuarioRepository;
import mx.com.ventiflow.infra.security.DatosRespuestaAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@RequestBody DatosUsuario datos) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByNombre(datos.usuario());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            // Validación directa de la contraseña
            if (usuario.getClave().equals(datos.clave())) {

                // Token simulado
                String tokenSimulado = UUID.randomUUID().toString();

                DatosRespuestaAuth respuesta = new DatosRespuestaAuth(tokenSimulado, usuario.getRol(), usuario.getNombre());
                return ResponseEntity.ok(respuesta); // Retorna 200 OK con el JSON
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas"); // Retorna 401
    }
}
