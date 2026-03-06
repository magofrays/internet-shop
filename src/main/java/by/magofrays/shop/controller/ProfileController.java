package by.magofrays.shop.controller;

import by.magofrays.shop.dto.CartDto;
import by.magofrays.shop.dto.OrderDto;
import by.magofrays.shop.dto.ReadProfileDto;
import by.magofrays.shop.dto.UpdateProfileDto;
import by.magofrays.shop.entity.Role;
import by.magofrays.shop.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN')")
    @GetMapping
    public ResponseEntity<ReadProfileDto> getProfileInfo(@AuthenticationPrincipal UserDetails principal){
        UUID profileId = UUID.fromString(principal.getUsername());
        return ResponseEntity.ok(profileService.getProfileInfo(profileId));
    }

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN')")
    @GetMapping
    public ResponseEntity<CartDto> getCart(@AuthenticationPrincipal UserDetails principal){
        UUID profileId = UUID.fromString(principal.getUsername());
        return ResponseEntity.ok(profileService.getCart(profileId));
    }

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN')")
    @GetMapping
    public ResponseEntity<List<OrderDto>> getOrders(@AuthenticationPrincipal UserDetails principal){
        UUID profileId = UUID.fromString(principal.getUsername());
        return ResponseEntity.ok(profileService.getOrders(profileId));
    }

    @PreAuthorize("hasAnyAuthority('CLIENT', 'ADMIN')")
    @PutMapping
    public ResponseEntity<ReadProfileDto> updateProfileInfo(
            @RequestBody @Validated UpdateProfileDto profileDto,
            @AuthenticationPrincipal UserDetails principal){
        UUID profileId = UUID.fromString(principal.getUsername());
        profileDto.setId(profileId);
        return ResponseEntity.ok(profileService.updateProfile(profileDto));
    }



    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping
    public ResponseEntity<?> deleteProfile(@AuthenticationPrincipal UserDetails principal){
        UUID profileId = UUID.fromString(principal.getUsername());
        profileService.deleteProfile(profileId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping
    public ResponseEntity<ReadProfileDto> updateProfileRole(@AuthenticationPrincipal UserDetails principal,
                                                            @RequestBody Role role){
        UUID profileId = UUID.fromString(principal.getUsername());
        ReadProfileDto profileDto = profileService.updateProfileRole(profileId, role);
        return ResponseEntity.ok(profileDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public ResponseEntity<List<ReadProfileDto>> getAllProfiles(){
        return ResponseEntity.ok(
                profileService.getAllProfiles()
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{profileId}")
    public ResponseEntity<ReadProfileDto> getProfileById(@PathVariable UUID profileId){
        return ResponseEntity.ok(
                profileService.getProfileById(profileId)
        );
    }
}
