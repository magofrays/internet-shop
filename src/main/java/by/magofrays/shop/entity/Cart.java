package by.magofrays.shop.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Cart {
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @OneToOne
    private Profile profile;

    @OneToMany(mappedBy = "cart")
    @Builder.Default
    private List<CartItem> itemList = new ArrayList<>();
}
