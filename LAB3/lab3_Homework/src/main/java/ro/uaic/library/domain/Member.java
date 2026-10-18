package ro.uaic.library.domain;

import java.util.Objects;

public record Member(long id, String name, MemberRole role) {
    public Member {
        if (id <= 0) throw new IllegalArgumentException("Member ID must be positive");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Member name is required");
        Objects.requireNonNull(role, "Member role is required");
    }
}
