package ro.uaic.info.vros.dto;

// Clientul trimite accessToken in antetul Authorization: Bearer <token>.
public record TokenResponse(String accessToken, String tokenType, long expiresIn) {
}
