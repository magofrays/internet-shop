package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @GeneratedValue
    private UUID cartId;

    @OneToOne(mappedBy = "cart")
    private User user;

    @ManyToMany
    @Builder.Default
    private List<Item> itemList = new ArrayList<>();
}
