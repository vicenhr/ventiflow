package mx.com.ventiflow.infra.security;

import lombok.RequiredArgsConstructor;
import mx.com.ventiflow.domain.usuario.DatosUsuario;
import mx.com.ventiflow.domain.usuario.UsuarioRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String CREDENCIALES_INVALIDAS = "Credenciales invalidas";
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public DatosRespuestaAuth autenticar(DatosUsuario datosUsuario){
        var usuario = usuarioRepository.findByNombre(datosUsuario.usuario())
                .orElseThrow(() -> new BadCredentialsException(CREDENCIALES_INVALIDAS));

        if(!passwordEncoder.matches(datosUsuario.clave(), usuario.getClaveHash())){
            throw new BadCredentialsException(CREDENCIALES_INVALIDAS);
        }

        String jwtToken = jwtService.generarToken(usuario);
        return new DatosRespuestaAuth(jwtToken, usuario.getRol().name(), usuario.getNombre());
    }
}
