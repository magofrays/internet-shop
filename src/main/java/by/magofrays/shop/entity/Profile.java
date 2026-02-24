package by.magofrays.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class Profile {
    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne(mappedBy = "profile")
    private Cart cart;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    @Column(name = "role_id")
    private Role role;
    private Instant createdAt;
    private Instant updatedAt;
    @OneToMany(mappedBy = "createdBy")
    @Builder.Default
    private List<Category> createdCategories = new ArrayList<>();

    @OneToMany(mappedBy = "addedBy")
    @Builder.Default
    private List<Item> addedItems = new ArrayList<>();

    public void setCart(Cart cart){
        this.cart = cart;
        cart.setProfile(this);
    }
}
