package by.magofrays.shop.utils;

import by.magofrays.shop.configuration.security.JwtUtils;
import by.magofrays.shop.entity.Role;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;

@Getter
@Component
public class TokenGenerator {

    @Autowired
    private JwtUtils jwtUtils;

    private String adminToken;
    private String userToken;

    @PostConstruct
    public void init() {
        adminToken = jwtUtils.createJwt(
                new User(
                        "22222222-2222-2222-2222-222222222222", //alexey
                        "",
                        Collections.singletonList(new SimpleGrantedAuthority(Role.ADMIN.name()))
                )
        );

        userToken = jwtUtils.createJwt(
                new User(
                        "33333333-3333-3333-3333-333333333333", // matvey
                        "",
                        Collections.singletonList(new SimpleGrantedAuthority(Role.CLIENT.name()))
                )
        );
    }
}
