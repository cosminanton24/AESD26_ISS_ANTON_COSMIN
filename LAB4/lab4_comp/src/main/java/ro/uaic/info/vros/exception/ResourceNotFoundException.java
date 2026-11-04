package ro.uaic.info.vros.exception;

// Exceptie proprie aruncata cand ID-ul cerut nu exista in baza de date.
// ApiExceptionHandler o transforma intr-un raspuns HTTP 404.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " with ID " + id + " was not found.");
    }
}
