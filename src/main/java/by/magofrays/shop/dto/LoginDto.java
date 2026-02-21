package by.magofrays.shop.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class LoginDto {
    @Email
    @NotBlank
    String email;
    @NotBlank
    String password;
}
