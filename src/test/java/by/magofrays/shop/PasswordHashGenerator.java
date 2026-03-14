package by.magofrays.shop;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("пароль 'password' -> " + encoder.encode("password"));
        System.out.println("пароль 'admin123' -> " + encoder.encode("admin123"));
        System.out.println("пароль 'user123' -> " + encoder.encode("user123"));
    }
}