package ro.uaic.info.vros.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class LoginController {
    // Endpoint mock public: nu verifica parole si nu creeaza tokenuri sau sesiuni.
    // Autentificarea resurselor protejate este efectuata separat prin HTTP Basic.
    @PostMapping("/login")
    public Map<String, String> login() {
        return Map.of("message", "Mock login endpoint. Use HTTP Basic to access protected resources.");
    }
}
