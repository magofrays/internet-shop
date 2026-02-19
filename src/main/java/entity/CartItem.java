package entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Data
@Entity
public class CartItem {
    @Id
    @GeneratedValue
    private UUID cartItemId;
    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Item item;
}
