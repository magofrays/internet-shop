package by.magofrays.shop.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;


@Data
@Builder
public class CreateUpdateProfileDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
