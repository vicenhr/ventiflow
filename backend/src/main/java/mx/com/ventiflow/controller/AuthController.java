package mx.com.ventiflow.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mx.com.ventiflow.domain.usuario.DatosUsuario;
import mx.com.ventiflow.infra.security.AuthService;
import mx.com.ventiflow.infra.security.DatosRespuestaAuth;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService login;

    @PostMapping("/login")
    public ResponseEntity<DatosRespuestaAuth> autenticarUsuario(@Valid @RequestBody DatosUsuario datos) {
        return ResponseEntity.ok(login.autenticar(datos));
    }
}