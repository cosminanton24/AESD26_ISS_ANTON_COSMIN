package ro.uaic.info.vros.entity;

import jakarta.persistence.*;

// Refoloseste campurile comune fara sa creeze un tabel separat.
@MappedSuperclass
public abstract class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 254)
    private String email;

    @Column(nullable = false, length = 30)
    private String phone;

    // Atat Customer, cat si Driver au un cont de utilizator unic in tabelul propriu.
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private AppUser user;

    protected Person() {
    }

    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
