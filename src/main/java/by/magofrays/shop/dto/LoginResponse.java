package by.magofrays.shop.dto;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class LoginResponse {
    String token;
    Instant expiresAt;
}
