package by.magofrays.shop.controller;

import by.magofrays.shop.configuration.security.JwtUtils;
import by.magofrays.shop.dto.CreateUpdateProfileDto;
import by.magofrays.shop.dto.LoginDto;
import by.magofrays.shop.dto.LoginResponse;
import by.magofrays.shop.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;

    @PostMapping("/sign-in")
    public LoginResponse signIn(@RequestBody @Validated LoginDto loginDto){
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        UserDetails details = (UserDetails) auth.getPrincipal();
        return authService.createLoginResponse(details);
    }

    @PostMapping("/sign-up")
    public LoginResponse signUp(@RequestBody @Validated CreateUpdateProfileDto createProfileDto){
        return authService.createLoginResponse(
                authService
                        .createProfile(createProfileDto)
        );
    }




}
