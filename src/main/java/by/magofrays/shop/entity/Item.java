package by.magofrays.shop.entity;

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
    private Long quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @ManyToMany(mappedBy = "itemList")
    @Builder.Default
    private List<Category> categories = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    @Builder.Default
    private List<CartItem> carts = new ArrayList<>();

    @OneToMany(mappedBy = "item")
    @Builder.Default
    private List<OrderItem> orders = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    private Profile addedBy;
}
