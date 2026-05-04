package blav.ventiflow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://127.0.0.1:5500") // Permite la conexión desde tu archivo local
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody Map<String, String> body) {
        String pin = body.get("clave");

        // Simulación de validación (Mock)
        if ("1234".equals(pin)) {
            UsuarioDTO mockUser = new UsuarioDTO(1L, "Administrador", "Administrador Principal");
            // Enviamos un token falso por ahora para que el JS no de error
            AuthResponse response = new AuthResponse("fake-jwt-token-123", mockUser);

            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
