package by.magofrays.shop.dto;

import lombok.Data;

@Data
public class CreateUpdateProfileDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
