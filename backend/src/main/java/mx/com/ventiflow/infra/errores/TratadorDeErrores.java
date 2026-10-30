package mx.com.ventiflow.infra.errores;

import jakarta.persistence.EntityNotFoundException;
import mx.com.ventiflow.domain.ValidacionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErrores {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> tratarError400(MethodArgumentNotValidException ex) {
        var errores = ex.getFieldErrors().stream().map(DatosErrorValidacion::new).toList();
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?>  tratarError401(BadCredentialsException ex) {
        var error = new DatosErrorValidacion(ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<?> tratarError404(EntityNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(ValidacionException.class)
    public ResponseEntity<?> tratarErrorDeValidacion(ValidacionException e){
        return ResponseEntity.badRequest().body(new DatosErrorValidacion(e.getMessage()));
    }

    private record DatosErrorValidacion(String campo, String error){
        public DatosErrorValidacion(String mensaje) {
            this(null, mensaje);
        }
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}
