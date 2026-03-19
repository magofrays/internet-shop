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
public class Category {
    @Id
    @GeneratedValue
    private UUID id;
    private String title;
    private String description;

    @OneToMany(mappedBy = "parentCatalogue")
    @Builder.Default
    private List<Category> categoryList = new ArrayList<>();
    @ManyToOne(fetch = FetchType.LAZY)
    private Category parentCatalogue;

    @ManyToMany
    @JoinTable(
            name = "category_item",
            joinColumns = @JoinColumn(name = "category_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id")
    )
    @Builder.Default
    private List<Item> itemList = new ArrayList<>();
    private Instant createdAt;
    private Instant updatedAt;

    public void addChildCatalogue(Category category){
        categoryList.add(category);
        category.setParentCatalogue(this);
    }

    public void addItem(Item item){
        itemList.add(item);
        item.getCategories().add(this);
    }
}
