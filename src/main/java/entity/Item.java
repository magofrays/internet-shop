package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue
    private UUID itemId;
    private String title;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @ManyToMany
    @JoinTable(
            name = "supplier_item",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "supplier_id")
    )
    @Builder.Default
    private List<Supplier> suppliers = new ArrayList<>();

    @ManyToMany(mappedBy = "itemList")
    @Builder.Default
    private List<Category> categoryList = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    @Builder.Default
    private List<CartItem> cartItems = new ArrayList<>();
}
