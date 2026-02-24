package by.magofrays.shop.configuration.security;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Data
@Component
@NoArgsConstructor
@ConfigurationProperties(prefix = "security.jwt")
public class SecurityJwtProperties {
    private int expiresHours;
    private String secret;
}