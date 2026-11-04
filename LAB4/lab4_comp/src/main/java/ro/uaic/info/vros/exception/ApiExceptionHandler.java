package ro.uaic.info.vros.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.Map;

// Trateaza centralizat exceptiile pentru controller-ele REST.
@RestControllerAdvice
public class ApiExceptionHandler {
    // Pentru o resursa inexistenta, returneaza 404 si mesajul exceptiei proprii.
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> notFound(ResourceNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", exception.getMessage()));
    }

    // Returneaza 409 pentru duplicate sau stergeri blocate de relatii existente.
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> conflict(DataIntegrityViolationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error",
                "Duplicate value or resource still referenced by another resource."));
    }
}
