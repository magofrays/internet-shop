package by.magofrays.shop.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class UpdateProfileDto {
    @NotNull
    private UUID id;
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
}
