package by.magofrays.shop.dto;

import by.magofrays.shop.entity.Role;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.time.Instant;
import java.util.UUID;

@Value
@Builder
@EqualsAndHashCode
public class ReadProfileDto {
    UUID id;
    String firstName;
    String lastName;
    String email;
    Role role;
    Instant createdAt;
    Instant updatedAt;
}
