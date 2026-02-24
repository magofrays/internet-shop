package by.magofrays.shop.repository;

import by.magofrays.shop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> getCategoriesByParentCatalogue(Category category);
    List<Category> getCategoriesByParentCatalogue_Id(UUID categoryId);
}
