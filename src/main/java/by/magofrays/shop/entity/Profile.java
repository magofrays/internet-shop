package by.magofrays.shop.entity;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = {"cart"})
public class Profile {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @OneToOne(mappedBy = "profile", cascade = CascadeType.ALL)
    private Cart cart;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Column(name = "role_id")
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;
    @Builder.Default
    @OneToMany(mappedBy = "createdBy")
    List<Order> orders = new ArrayList<>();

    public void setCart(Cart cart){
        this.cart = cart;
        cart.setProfile(this);
    }
}
