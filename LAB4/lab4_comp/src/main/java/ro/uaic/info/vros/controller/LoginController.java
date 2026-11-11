package ro.uaic.info.vros.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import jakarta.validation.Valid;
import ro.uaic.info.vros.dto.*;
import ro.uaic.info.vros.service.AuthService;

@RestController
public class LoginController {
    private final AuthService service;
    public LoginController(AuthService service) { this.service = service; }

    // Login real: verifica email/parola si emite un JWT semnat, valabil o ora.
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return service.register(request);
    }
}
