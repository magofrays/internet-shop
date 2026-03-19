package by.magofrays.shop.repository;

import by.magofrays.shop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCreatedBy_Id(UUID profileId);
}
