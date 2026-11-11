package ro.uaic.info.vros.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.uaic.info.vros.entity.*;
import ro.uaic.info.vros.repository.AppUserRepository;
import java.util.UUID;

@Service
@Transactional
public class UserAccountService {
    private final AppUserRepository users;
    private final PasswordEncoder encoder;
    public UserAccountService(AppUserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    // Pastreaza compatibilitatea cu profilurile existente si cu CRUD-ul administrativ.
    // Conturile fara parola aleasa prin inregistrare primesc un hash aleator, nu o parola comuna.
    public void connect(Person person, Role role) {
        AppUser user = person.getUser();
        // Profilul poate veni dintr-o tranzactie anterioara; recitim contul in cea curenta.
        if (user != null) user = users.findById(user.getId()).orElseThrow();
        if (user == null) {
            user = users.findByEmail(person.getEmail()).orElseGet(() -> {
                AppUser created = new AppUser();
                created.setPasswordHash(encoder.encode(UUID.randomUUID().toString()));
                return created;
            });
        }
        user.setEmail(person.getEmail());
        user.getRoles().add(role);
        person.setUser(users.save(user));
    }
}
