package entity;

import lombok.Data;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
public class CartItem {
    @Id
    @GeneratedValue
    private UUID cartItemId;
    @ManyToOne(fetch = FetchType.LAZY)
    private Cart cart;

    @ManyToOne
    private Item item;
    Instant addedAt;
}
