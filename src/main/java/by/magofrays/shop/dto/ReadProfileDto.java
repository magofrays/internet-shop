package by.magofrays.shop.dto;

import by.magofrays.shop.entity.Role;
import lombok.Value;

import java.time.Instant;

@Value
public class ReadProfileDto {
    String firstName;
    String lastName;
    String email;
    Role role;
    Instant createdAt;
    Instant updatedAt;
}
